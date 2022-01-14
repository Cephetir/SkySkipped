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

import gg.essential.api.EssentialAPI
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class RPC {

    @SubscribeEvent
    fun update(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START && !discordRPCManager.isActive) return
        if (Cache.isInDungeon) {
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
        }
    }

    companion object {
        val discordRPCManager = DiscordRPCManager()

        fun init() {
            discordRPCManager.start()
        }

        fun shutdown() {
            discordRPCManager.stop()
        }

        fun reset() {
            if (Config.DRPC && !discordRPCManager.isActive) discordRPCManager.start() else if (!Config.DRPC && discordRPCManager.isActive) discordRPCManager.stop()
        }
    }
}
