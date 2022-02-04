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

import gg.essential.api.utils.Multithreading
import gg.essential.api.utils.WebUtil
import gg.essential.universal.UChat
import gg.essential.universal.wrappers.message.UTextComponent
import me.cephetir.skyskipped.commands.SkySkippedCommand
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.features.Features
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.utils.BlurUtils
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLModDisabledEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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
        const val VERSION = "2.5"

        val config = Config()
        val features = Features()
        val logger: Logger = LogManager.getLogger("SkySkipped")
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
    }

    @Mod.EventHandler
    fun stop(event: FMLModDisabledEvent) = RPC.shutdown()

    var checked = false
    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        if(checked || Minecraft.getMinecraft().theWorld == null) return
        checked = true
        logger.info("Checking for updates...")
        Multithreading.runAsync Thread@ {
            val version = WebUtil.fetchString("https://raw.githubusercontent.com/Cephetir/SkySkipped/kotlin/h.txt") ?: return@Thread
            if (version == "Failed to fetch") return@Thread
            if (VERSION.toDouble() < version.toDouble()) {
                logger.info("New version detected!")
                UChat.chat(
                    UTextComponent("""
                    §4§l-----------------------------------------
                    §cSkySkipped §f:: §eNew Version Detected: §c ${version.toDouble()} §8(Click to Download)
                    §4§l-----------------------------------------
                    """.trimIndent())
                        .setClick(ClickEvent.Action.OPEN_URL, "https://github.com/Cephetir/SkySkipped/releases/latest")
                )
            } else logger.info("Latest version!")
        }
    }
}