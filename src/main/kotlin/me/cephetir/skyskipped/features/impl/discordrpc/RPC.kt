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

package me.cephetir.skyskipped.features.impl.discordrpc

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.SBInfo
import me.cephetir.skyskipped.event.SkyblockIsland
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object RPC : Feature() {
    private val discordRPCManager = DiscordRPCManager()

    fun init() = discordRPCManager.start()

    fun shutdown() = discordRPCManager.stop()

    fun reset(enabled: Boolean = Config.DRPC) {
        if (enabled && !discordRPCManager.isActive) discordRPCManager.start()
        else if (!enabled && discordRPCManager.isActive) discordRPCManager.stop()
    }

    //@SubscribeEvent
    fun update(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !discordRPCManager.isActive) return

        val detail = when (Config.drpcDetail) {
            0 ->
                if (SBInfo.island == SkyblockIsland.Unknown)
                    if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
                else SBInfo.island.formattedName

            1 -> mc.session.username
            2 -> if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
            3 -> mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
            4 -> Config.drpcText
            else -> ""
        }

        val state = when (Config.drpcState) {
            0 ->
                if (SBInfo.island == SkyblockIsland.Unknown)
                    if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
                else SBInfo.island.formattedName

            1 -> mc.session.username
            2 -> if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
            3 -> mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
            4 -> Config.drpcText2
            else -> ""
        }

        discordRPCManager.setDetailsLine(detail)
        discordRPCManager.setStateLine(state)

        /*if (Cache.inDungeon) {
            discordRPCManager.setDetailsLine("Playing " + Cache.dungeonName)
            discordRPCManager.setStateLine("Cleared: " + Cache.dungeonPercentage + " %")
        } else if (!Minecraft.getMinecraft().isSingleplayer && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().netHandler != null) {
            if (Cache.inSkyblock) {
                discordRPCManager.setDetailsLine("Playing on Hypixel Skyblock")
                discordRPCManager.setStateLine("Holding: " + Cache.itemheld)
            } else if (EssentialAPI.getMinecraftUtil().isHypixel()) {
                discordRPCManager.setDetailsLine("Playing on Hypixel")
                discordRPCManager.setStateLine("In game")
            } else {
                discordRPCManager.setDetailsLine("Playing on " + Minecraft.getMinecraft().currentServerData.serverIP)
                discordRPCManager.setStateLine("In game")
            }
        } else if (Minecraft.getMinecraft().isSingleplayer && Minecraft.getMinecraft().theWorld != null) {
            discordRPCManager.setDetailsLine("Playing Singleplayer")
            discordRPCManager.setStateLine("In game")
        } else {
            discordRPCManager.setDetailsLine("In main menu")
            discordRPCManager.setStateLine("Idle")
        }*/
    }
}
