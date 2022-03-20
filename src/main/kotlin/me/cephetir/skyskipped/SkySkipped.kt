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

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.api.utils.WebUtil
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.utils.BlurUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLModDisabledEvent
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
    clientSideOnly = true
)
class SkySkipped {
    companion object {
        const val MODID = "skyskipped"
        const val MOD_NAME = "SkySkipped"
        const val VERSION = "2.7"

        val config = Config()
        val features = Features()
        val logger: Logger = LogManager.getLogger("SkySkipped")

        val autoGhostBlockKey = KeyBinding("Auto Ghost Block", Keyboard.KEY_NONE, "SkySkipped")
        val perspectiveToggle = KeyBinding("Better Perspective", Keyboard.KEY_NONE, "SkySkipped")
        val armorSwap = KeyBinding("Item Swap", Keyboard.KEY_NONE, "SkySkipped")

        val cosmetics = hashMapOf<String, Pair<String, String>>()

        val regex = Regex("(?:§.)*(?<prefix>\\[\\w\\w\\w(?:(?:§.)*\\+)*(?:§.)*])? *(?<username>\\w{3,16})(?:§.)* *:*")

        @JvmStatic
        fun getCosmetics(message: String): String {
            var text = message
            val result = regex.findAll(text)
            for (matcher in result) {
                val name = matcher.groups["username"]?.value?.trim() ?: continue
                val prefix = matcher.groups["prefix"]?.value?.trim()
                val newName = cosmetics[name]?.component1()?.replace("&", "§") ?: continue
                val newPrefix = cosmetics[name]?.component2()?.replace("&", "§") ?: continue
                text = text.replace(name, newName)
                if (prefix != null) text = text.replace(prefix, newPrefix)
            }
            if (text.contains(Minecraft.getMinecraft().thePlayer.displayNameString)) text = text.replace(
                Minecraft.getMinecraft().thePlayer.displayNameString,
                cosmetics[Minecraft.getMinecraft().thePlayer.displayNameString]!!.component1().replace("&", "§")
            )
            return text
        }

        @JvmStatic
        fun replaceCosmetics(message: String): String {
            if (message.contains(Minecraft.getMinecraft().thePlayer.displayNameString))
                return message.replace(
                    Minecraft.getMinecraft().thePlayer.displayNameString,
                    cosmetics[Minecraft.getMinecraft().thePlayer.displayNameString]?.component1()?.replace("&", "§") ?: return message
                )
            else return message
        }

    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        logger.info("Starting SkySkipped...")
        config.preload()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(RPC())
        MinecraftForge.EVENT_BUS.register(Listener())
        features.register()
        RPC.reset()
        BlurUtils.registerListener()

        ClientCommandHandler.instance.registerCommand(SkySkippedCommand())
        ClientCommandHandler.instance.registerCommand(Features.leaveCommand)
        ClientCommandHandler.instance.registerCommand(Features.partyCommand)

        ClientRegistry.registerKeyBinding(autoGhostBlockKey)
        ClientRegistry.registerKeyBinding(perspectiveToggle)
        ClientRegistry.registerKeyBinding(armorSwap)
    }

    @Mod.EventHandler
    fun onStop(event: FMLModDisabledEvent) = RPC.shutdown()

    @Mod.EventHandler
    fun onLoad(event: FMLLoadCompleteEvent) {
        logger.info("Checking for updates...")
        Multithreading.runAsync {
            val version = WebUtil.fetchString("https://raw.githubusercontent.com/Cephetir/SkySkipped/kotlin/h.txt")
                ?: return@runAsync
            if ((version != "Failed to fetch") && (VERSION.toDouble() < version.toDouble())) {
                logger.info("New version detected!")
                EssentialAPI.getNotifications().push(
                    "SkySkipped",
                    "New Version Detected: ${version.toDouble()}\nClick to Download",
                    10f,
                    action = { Desktop.getDesktop().browse(URI("https://github.com/Cephetir/SkySkipped/releases")) }
                )
            } else logger.info("Latest version!")

            val json = WebUtil.fetchJSON("https://raw.githubusercontent.com/Cephetir/SkySkipped/kotlin/n.json")
            if (json.size == 0) logger.info("Failed to download cosmetics!")
            else {
                json.optJSONArray("list").toList().forEach {
                    cosmetics[it.asJsonObject.getAsJsonPrimitive("name").asString] =
                        Pair(
                            it.asJsonObject.getAsJsonPrimitive("nick").asString,
                            it.asJsonObject.getAsJsonPrimitive("prefix").asString
                        )
                }
                logger.info("Successfully downloaded cosmetics!")
            }
        }
    }
}