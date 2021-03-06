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
import com.google.gson.JsonObject
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.event.SBInfo
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.macro.RemoteControlling
import me.cephetir.skyskipped.features.impl.misc.Metrics
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import me.cephetir.skyskipped.utils.HttpUtils
import net.minecraft.client.Minecraft
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
        const val VERSION = "3.2"

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
        private val regex = Regex("(?:??.)*(?<prefix>\\[\\w\\w\\w(?:(?:??.)*\\+)*(?:??.)*])? *(?<username>\\w{3,16})(?:??.)* *:*")

        @JvmStatic
        fun getCosmetics(message: String): String {
            if (Minecraft.getMinecraft().thePlayer == null) return message
            var text = message
            val result = regex.findAll(text)
            for (matcher in result) {
                val name = matcher.groups["username"]?.value?.trim() ?: continue
                val nameRange = matcher.groups["username"]?.range ?: continue
                val prefixRange = matcher.groups["prefix"]?.range

                val newName = cosmetics[name]?.first?.replace("&", "??") ?: continue
                val newPrefix = cosmetics[name]?.second?.replace("&", "??") ?: continue

                text = text.replaceRange(nameRange, newName)
                if (prefixRange != null) text = text.replaceRange(prefixRange, newPrefix)
            }
            if (text.contains(Minecraft.getMinecraft().thePlayer.displayNameString)) text = text.replace(
                Minecraft.getMinecraft().thePlayer.displayNameString,
                cosmetics[Minecraft.getMinecraft().thePlayer.displayNameString]?.component1()?.replace("&", "??") ?: return text
            )
            return text
        }

        @JvmStatic
        fun replaceCosmetics(message: String): String {
            return if (Minecraft.getMinecraft().thePlayer != null && message.contains(Minecraft.getMinecraft().thePlayer.displayName.formattedText))
                message.replace(
                    Minecraft.getMinecraft().thePlayer.displayNameString,
                    cosmetics[Minecraft.getMinecraft().thePlayer.displayNameString]?.component1()?.replace("&", "??") ?: return message
                )
            else message
        }

        fun loadCosmetics() {
            cosmetics.clear()
            val gson = Gson()
            val body = HttpUtils.sendGet("https://api.github.com/gists/7af203131b17bd470e5453785916ef69", mapOf("Content-Type" to "application/json"))
            val json = gson.fromJson(body, JsonObject::class.java) ?: return logger.info("Failed to download cosmetics!")
            val content = json.getAsJsonObject("files")
                .getAsJsonObject("cosmetics.json")
                .getAsJsonPrimitive("content").asString
            gson.fromJson(content, JsonArray::class.java).toList().forEach {
                cosmetics[it.asJsonObject.getAsJsonPrimitive("name").asString] =
                    Pair(
                        it.asJsonObject.getAsJsonPrimitive("nick").asString,
                        it.asJsonObject.getAsJsonPrimitive("prefix").asString
                    )
            }
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

        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        logger.info("Initializing SkySkipped...")

        MinecraftForge.EVENT_BUS.register(SBInfo)
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