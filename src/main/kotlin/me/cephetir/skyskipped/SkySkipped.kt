/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import me.cephetir.bladecore.core.cache.impl.MapCache
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.bladecore.utils.ShutdownHook
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.features.impl.misc.Metrics
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.CustomName
import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.settings.KeyBinding
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
        const val VERSION = "3.4"

        lateinit var config: Config
        val features = Features()
        val logger: Logger = LogManager.getLogger("SkySkipped")
        var devMode = false

        val autoGhostBlockKey = KeyBinding("Auto Ghost Block", Keyboard.KEY_NONE, "SkySkipped")
        val perspectiveToggle = KeyBinding("Better Perspective", Keyboard.KEY_NONE, "SkySkipped")
        val autoDojo = KeyBinding("Auto Dojo", Keyboard.KEY_NONE, "SkySkipped")
        val giftAura = KeyBinding("Gift Aura", Keyboard.KEY_NONE, "SkySkipped")
        val keybinds = hashSetOf<GuiItemSwap.Keybind>()
        val macroKey = KeyBinding("Toggle Macro", Keyboard.KEY_NONE, "SkySkipped")
        val hotbarKey = KeyBinding("Swap Hotbar Layouts", Keyboard.KEY_NONE, "SkySkipped")

        private val cosmetics = hashSetOf<CustomName>()
        private val regex =
            Regex("(?:§.)*(?<rank>\\[(?:§.)*\\d+(?:§.)*\\])? ?(?:§.)*(?<prefix>\\[\\w\\w\\w(?:§.)*(?:\\+(?:§.)*)*])? ?(?<username>\\w{3,16})(?:§.)*:*")
        val cosmeticCache = MapCache<String, String>(10000)

        @JvmStatic
        fun getCosmetics(message: String?): String? {
            if (mc.thePlayer == null || message == null) return message
            if (cosmeticCache.isCached(message))
                return cosmeticCache.get(message)!!

            var text = message!!
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

        private val gson = Gson()
        fun loadCosmetics() {
            cosmetics.forEach { BladeEventBus.unsubscribe(it) }
            cosmetics.clear()
            val body = HttpUtils.sendGet(
                "https://gist.githubusercontent.com/Cephetir/327b7738f91cd11636a5ae35029dd83c/raw",
                mapOf("Content-Type" to "application/json")
            )
            gson.fromJson(body, JsonArray::class.java)?.toList()?.forEach {
                it as JsonObject
                val name = it.getAsJsonPrimitive("name").asString
                if (it.getAsJsonPrimitive("animated").asBoolean) {
                    val nicks = mutableListOf<CustomName.Nick>()
                    val frames = it.getAsJsonArray("frames")
                    frames.sortedBy { frame ->
                        frame as JsonObject
                        frame.getAsJsonPrimitive("index").asInt
                    }.forEach { frame ->
                        frame as JsonObject
                        nicks.add(
                            CustomName.Nick(
                                frame.getAsJsonPrimitive("nick").asString,
                                frame.getAsJsonPrimitive("prefix").asString,
                                frame.getAsJsonPrimitive("nextIn").asInt
                            )
                        )
                    }
                    cosmetics.add(CustomName(name, true, nicks))
                } else cosmetics.add(
                    CustomName(
                        name, false,
                        listOf(
                            CustomName.Nick(
                                it.asJsonObject.getAsJsonPrimitive("nick").asString,
                                it.asJsonObject.getAsJsonPrimitive("prefix").asString,
                                -1
                            )
                        )
                    )
                )
            } ?: return logger.info("Failed to download cosmetics!")
            cosmeticCache.resetCache()
            logger.info("Successfully downloaded cosmetics!")
        }
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        logger.info("Starting SkySkipped...")

        config = Config()
        config.preload()
        config.loadKeybinds()
        config.loadHotbars()
        config.loadScripts()
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        logger.info("Initializing SkySkipped...")

        features.register()

        ClientCommandHandler.instance.registerCommand(SkySkippedCommand())
        ClientCommandHandler.instance.registerCommand(Features.leaveCommand)
        ClientCommandHandler.instance.registerCommand(Features.partyCommand)

        ClientRegistry.registerKeyBinding(autoGhostBlockKey)
        ClientRegistry.registerKeyBinding(perspectiveToggle)
        ClientRegistry.registerKeyBinding(autoDojo)
        ClientRegistry.registerKeyBinding(giftAura)
        ClientRegistry.registerKeyBinding(macroKey)
        ClientRegistry.registerKeyBinding(hotbarKey)

        ShutdownHook.register {
            config.saveKeybinds()
            config.saveHotbars()
            config.markDirty()
            config.writeData()

            Metrics.update(false)
            RPC.shutdown()
            RemoteControlling.stop()
        }
    }

    @Mod.EventHandler
    fun onLoad(event: FMLLoadCompleteEvent) {
        logger.info("Checking for updates...")
        Multithreading.runAsync {
            val version = HttpUtils.sendGet("https://raw.githubusercontent.com/Cephetir/SkySkipped/kotlin/h.txt", null)?.toDouble() ?: return@runAsync
            if (VERSION.toDouble() < version) {
                logger.info("New version detected!")
                EssentialAPI.getNotifications().push(
                    "SkySkipped",
                    "New Version Detected: $version\nClick to Download",
                    10f,
                    action = { Desktop.getDesktop().browse(URI("https://github.com/Cephetir/SkySkipped/releases")) }
                )
            } else logger.info("Latest version!")

            loadCosmetics()
        }
    }
}