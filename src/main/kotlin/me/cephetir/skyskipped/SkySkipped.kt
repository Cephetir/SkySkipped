/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.essential.api.EssentialAPI
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.cephetir.bladecore.core.cache.impl.MapCache
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.bladecore.utils.ShutdownHook
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.features.impl.misc.Metrics
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.Cosmetic
import me.cephetir.skyskipped.utils.FunnyShit
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.skyblock.Ping
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.input.Keyboard
import java.awt.Desktop
import java.io.File
import java.net.URI

@Mod(
    modid = SkySkipped.MODID,
    name = SkySkipped.MOD_NAME,
    version = SkySkipped.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    modLanguageAdapter = "me.cephetir.bladecore.utils.KotlinAdapter",
    clientSideOnly = true,
    dependencies = "after:skyskippedjdaaddon"
)
class SkySkipped {
    companion object {
        const val MODID = "skyskipped"
        const val MOD_NAME = "SkySkipped"
        const val VERSION = "3.6"

        val logger: Logger = LogManager.getLogger("SkySkipped")
        var devMode = false

        val keybinds = hashSetOf<GuiItemSwap.Keybind>()
        val giftAura = KeyBinding("Gift Aura", Keyboard.KEY_NONE, "SkySkipped")
        val playGuiRecorder = KeyBinding("Play gui recorder", Keyboard.KEY_NONE, "SkySkipped")

        private val cosmetics = hashSetOf<Cosmetic>()
        private val regex = Regex("(?:§.)*(?<rank>\\[(?:§.)*\\d+(?:§.)*\\])? ?(?:§.)*(?<prefix>\\[\\w\\w\\w(?:§.)*(?:\\+(?:§.)*)*])? ?(?<username>\\w{3,16})(?:§.)*:*")
        val cosmeticCache = MapCache<String, String>(10000)
        private val capeCache = MapCache<String, Cosmetic.Cape?>(100)

        @JvmStatic
        fun getCosmetics(message: String?): String? {
            if (mc.thePlayer == null || message == null) return message
            if (cosmeticCache.isCached(message))
                return cosmeticCache.get(message)!!

            var text: String = message
            val result = regex.findAll(text)
            var displace = 0
            for (matcher in result) {
                val username = matcher.groups["username"] ?: continue
                val name = username.value.trim()
                val nameRange = username.range
                val prefixRange = matcher.groups["prefix"]?.range

                val customName = cosmetics.find { it.name == name }?.getNick() ?: continue
                val newName = customName.nick.replace("&", "§")
                val newPrefix = customName.prefix.replace("&", "§")

                text = text.replaceRange(IntRange(nameRange.first + displace, nameRange.last + displace), newName)
                if (prefixRange != null) text = text.replaceRange(IntRange(prefixRange.first + displace, prefixRange.last + displace), newPrefix)

                displace += (newName.length - (nameRange.last - nameRange.first + 1)) + (if (prefixRange != null) (newPrefix.length - (prefixRange.last - prefixRange.first + 1)) else 0)
            }

            cosmeticCache.cache(message, text)
            return text
        }

        @JvmStatic
        fun getCape(uuid: String): ResourceLocation? {
            if (mc.thePlayer == null || mc.theWorld == null)
                return null
            if (capeCache.isCached(uuid))
                return capeCache.get(uuid)?.getCape()

            val cape = cosmetics.find { it.cape != null && it.cape.uuid == uuid }?.cape
            capeCache.cache(uuid, cape)
            return cape?.getCape()
        }

        val gson = Gson()
        fun loadCosmetics() {
            logger.info("Downloading cosmetics...")
            cosmetics.forEach { BladeEventBus.unsubscribe(it) }
            cosmetics.clear()
            val body = HttpUtils.sendGet(
                "https://gist.githubusercontent.com/Cephetir/327b7738f91cd11636a5ae35029dd83c/raw",
                mapOf("Content-Type" to "application/json")
            ) ?: return
            gson.fromJson(body, JsonArray::class.java)?.forEach {
                it as JsonObject
                val name = it.getAsJsonPrimitive("name").asString
                var cape: Cosmetic.Cape? = null
                if (it.has("cape")) {
                    val obj = it.getAsJsonObject("cape")
                    cape = Cosmetic.Cape(obj.getAsJsonPrimitive("uuid").asString, obj.getAsJsonPrimitive("url").asString)
                }
                if (it.getAsJsonPrimitive("animated").asBoolean) {
                    val nicks = mutableListOf<Cosmetic.Nick>()
                    val frames = it.getAsJsonArray("frames")
                    frames.sortedBy { frame ->
                        frame as JsonObject
                        frame.getAsJsonPrimitive("index").asInt
                    }.forEach { frame ->
                        frame as JsonObject
                        nicks.add(
                            Cosmetic.Nick(
                                frame.getAsJsonPrimitive("nick").asString,
                                frame.getAsJsonPrimitive("prefix").asString,
                                frame.getAsJsonPrimitive("nextIn").asInt
                            )
                        )
                    }
                    cosmetics.add(Cosmetic(name, true, nicks, cape))
                } else cosmetics.add(
                    Cosmetic(
                        name, false,
                        listOf(
                            Cosmetic.Nick(
                                it.asJsonObject.getAsJsonPrimitive("nick").asString,
                                it.asJsonObject.getAsJsonPrimitive("prefix").asString,
                                -1
                            )
                        ), cape
                    )
                )
            } ?: return logger.info("Failed to download cosmetics!")
            cosmeticCache.resetCache()
            capeCache.resetCache()
            logger.info("Successfully downloaded cosmetics!")
        }

        var newVersion = -1.0
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        logger.info("Starting SkySkipped...")

        Config.load()
        Config.loadKeybinds()
        Config.loadHotbars()
        Config.loadScripts()
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        logger.info("Initializing SkySkipped...")

        BladeEventBus.subscribe(this, true)
        Features.register()
        Ping
        FunnyShit

        updateJob = BackgroundScope.launch {
            newVersion = checkForUpdates()
            loadCosmetics()
        }

        ClientCommandHandler.instance.registerCommand(SkySkippedCommand())
        ClientCommandHandler.instance.registerCommand(Features.leaveCommand)
        ClientCommandHandler.instance.registerCommand(Features.partyCommand)

        ClientRegistry.registerKeyBinding(giftAura)
        ClientRegistry.registerKeyBinding(playGuiRecorder)

        ShutdownHook.register {
            Config.saveKeybinds()
            Config.saveHotbars()

            Metrics.update(false)
            RPC.shutdown()
            RemoteControlling.stop()
            File(Config.modDir, "capes").deleteRecursively()
        }
    }

    private var updateJob: Job? = null
    private fun checkForUpdates(): Double {
        logger.info("Checking for updates...")
        val version = HttpUtils.sendGet("https://raw.githubusercontent.com/Cephetir/SkySkipped/kotlin/h.txt", null)?.toDouble() ?: return -1.0
        if (VERSION.toDouble() < version) {
            logger.info("New version detected!")
            return version
        } else logger.info("Latest version!")
        return -1.0
    }

    @Mod.EventHandler
    fun onLoad(event: FMLLoadCompleteEvent) {
        if (updateJob?.isCompleted != true) runBlocking {
            updateJob?.join()
        }

        if (newVersion != -1.0) EssentialAPI.getNotifications().push(
            "SkySkipped",
            "New Version Detected: $newVersion\nClick to see more",
            20f,
            action = { Desktop.getDesktop().browse(URI("https://github.com/Cephetir/SkySkipped/releases/latest")) }
        )
    }
}