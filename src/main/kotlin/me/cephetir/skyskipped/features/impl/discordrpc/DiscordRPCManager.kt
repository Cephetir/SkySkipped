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

import com.google.gson.JsonObject
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.SBInfo
import me.cephetir.skyskipped.event.SkyblockIsland
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.time.OffsetDateTime

class DiscordRPCManager : IPCListener {
    private var client: IPCClient? = null
    private var detailsLine: String? = null
    private var stateLine: String? = null
    private var startTimestamp: OffsetDateTime? = null
    private var connected = false

    fun start() {
        try {
            if (isActive) return
            SkySkipped.logger.info("Starting Discord RP...")
            stateLine = "Starting..."
            detailsLine = ""
            startTimestamp = OffsetDateTime.now()
            client = IPCClient(APPLICATION_ID)
            client!!.setListener(this)
            try {
                client!!.connect()
            } catch (e: Exception) {
                SkySkipped.logger.error("Failed to connect to Discord RPC: " + e.message)
            }
        } catch (ex: Throwable) {
            SkySkipped.logger.error("DiscordRP has thrown an unexpected error while trying to start...")
            ex.printStackTrace()
        }
    }

    fun stop() {
        if (isActive) {
            client!!.close()
            connected = false
        }
    }

    val isActive: Boolean
        get() = client != null && connected

    private fun updatePresence() {
        detailsLine = when (Config.drpcDetail) {
            0 ->
                if (SBInfo.island == SkyblockIsland.Unknown)
                    if (RPC.mc.isIntegratedServerRunning) "Singleplayer" else RPC.mc.currentServerData?.serverIP ?: "Main Menu"
                else SBInfo.island.formattedName

            1 -> RPC.mc.session.username
            2 -> if (RPC.mc.isIntegratedServerRunning) "Singleplayer" else RPC.mc.currentServerData?.serverIP ?: "Main Menu"
            3 -> RPC.mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
            4 -> Config.drpcText
            else -> ""
        }

        stateLine = when (Config.drpcState) {
            0 ->
                if (SBInfo.island == SkyblockIsland.Unknown)
                    if (RPC.mc.isIntegratedServerRunning) "Singleplayer" else RPC.mc.currentServerData?.serverIP ?: "Main Menu"
                else SBInfo.island.formattedName

            1 -> RPC.mc.session.username
            2 -> if (RPC.mc.isIntegratedServerRunning) "Singleplayer" else RPC.mc.currentServerData?.serverIP ?: "Main Menu"
            3 -> RPC.mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
            4 -> Config.drpcText2
            else -> ""
        }

        val presence = RichPresence.Builder()
            .setDetails(detailsLine)
            .setState(stateLine)
            .setStartTimestamp(startTimestamp)
            .setLargeImage("large", "SkySkipped v" + SkySkipped.VERSION)
            .build()
        client!!.sendRichPresence(presence)
    }

    fun setStateLine(status: String?) {
        stateLine = status
    }

    fun setDetailsLine(status: String?) {
        detailsLine = status
    }

    override fun onReady(client: IPCClient) {
        SkySkipped.logger.info("Discord RPC started")
        connected = true
        MinecraftForge.EVENT_BUS.register(this)
    }

    private var tickCounter = 0
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !isActive) return
        tickCounter++
        if (tickCounter % 200 == 0) {
            updatePresence()
            tickCounter = 0
        }
    }

    override fun onClose(client: IPCClient, json: JsonObject?) {
        SkySkipped.logger.info("Discord RPC closed")
        this.client = null
        connected = false
        MinecraftForge.EVENT_BUS.unregister(this)
    }

    override fun onDisconnect(client: IPCClient, t: Throwable) {
        SkySkipped.logger.info("Discord RPC disconnected")
        this.client = null
        connected = false
        MinecraftForge.EVENT_BUS.unregister(this)
    }

    companion object {
        private const val APPLICATION_ID = 867366183057752094L
    }
}