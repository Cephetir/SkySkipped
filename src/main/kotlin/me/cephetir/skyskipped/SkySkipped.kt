/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped

import com.google.gson.Gson
import com.google.gson.JsonArray
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.features.impl.misc.Metrics
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.HttpUtils
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
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
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter",
    clientSideOnly = true,
    dependencies = "after:skyskippedjdaaddon"
)
class SkySkipped {
    companion object {
        const val MODID = "skyskipped"
        const val MOD_NAME = "SkySkipped"
        const val VERSION = "3.3"

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

        private val cosmetics = hashMapOf<String, Pair<String, String>>()
        private val regex = Regex("(?:§.)*(?<prefix>\\[\\w\\w\\w(?:(?:§.)*\\+)*(?:§.)*])? *(?<username>\\w{3,16})(?:§.)* *:*")

        @JvmStatic
        fun getCosmetics(message: String): String {
            if (mc.thePlayer == null) return message
            var text = message
            val result = regex.findAll(text)
            var displace = 0
            for (matcher in result) {
                val username = matcher.groups["username"] ?: continue
                val name = username.value.trim()
                val nameRange = username.range
                val prefixRange = matcher.groups["prefix"]?.range

                val newName = cosmetics[name]?.first?.replace("&", "§") ?: continue
                val newPrefix = cosmetics[name]?.second?.replace("&", "§") ?: continue

                text = text.replaceRange(IntRange(nameRange.first + displace, nameRange.last + displace), newName)
                if (prefixRange != null) text = text.replaceRange(IntRange(prefixRange.first + displace, prefixRange.last + displace), newPrefix)

                displace += (newName.length - (nameRange.last - nameRange.first + 1)) + (if (prefixRange != null) (newPrefix.length - (prefixRange.last - prefixRange.first + 1)) else 0)
            }
            return text
        }

        fun loadCosmetics() {
            cosmetics.clear()
            val gson = Gson()
            val body = HttpUtils.sendGet(
                "https://gist.githubusercontent.com/Cephetir/7af203131b17bd470e5453785916ef69/raw/cosmetics.json",
                mapOf("Content-Type" to "application/json")
            )
            gson.fromJson(body, JsonArray::class.java)?.toList()?.forEach {
                cosmetics[it.asJsonObject.getAsJsonPrimitive("name").asString] =
                    Pair(
                        it.asJsonObject.getAsJsonPrimitive("nick").asString,
                        it.asJsonObject.getAsJsonPrimitive("prefix").asString
                    )
            } ?: return logger.info("Failed to download cosmetics!")
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

        BackgroundScope.start()

        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        logger.info("Initializing SkySkipped...")

        MinecraftForge.EVENT_BUS.register(Listener)
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

        EssentialAPI.getShutdownHookUtil().register {
            config.saveKeybinds()
            config.saveHotbars()
            config.markDirty()
            config.writeData()

            Metrics.update(false)
            RPC.shutdown()
            RemoteControlling.stop()
            BackgroundScope.stop()
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