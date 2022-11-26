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

import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.exceptions.NoDiscordClientException
import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.discord.AbstractDiscordIPC
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.utils.mc
import java.time.OffsetDateTime

class DiscordRPCManager : AbstractDiscordIPC() {
    override val APP_ID = 867366183057752094L
    override val rpcBuilder: RichPresence.Builder = RichPresence.Builder()
        .setLargeImage("large", "SkySkipped v${SkySkipped.VERSION}")

    override fun handleConnect() {
        SkySkipped.logger.info("Starting Discord RPC...")
        try {
            ipc!!.connect()
            rpcBuilder.setStartTimestamp(OffsetDateTime.now())
            val richPresence = rpcBuilder.build()
            ipc!!.sendRichPresence(richPresence)
            BackgroundScope.launchLooping(updateJob)
            SkySkipped.logger.info("Discord RPC initialised successfully!")
        } catch (e: NoDiscordClientException) {
            SkySkipped.logger.error("No discord client found for RPC, stopping")
        }
    }

    override fun handleDisconnect() {
        SkySkipped.logger.info("Shutting down Discord RPC...")
        BackgroundScope.cancel(updateJob)
        ipc!!.close()
    }

    override fun updateRPC(): RichPresence = rpcBuilder
        .setDetails(getLine(Config.drpcDetail))
        .setState(getLine(if (Config.drpcState == 4) 5 else Config.drpcState))
        .build()

    private fun getLine(line: Int): String = when (line) {
        0 ->
            if (SkyblockListener.island == SkyblockIsland.Unknown)
                if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
            else SkyblockListener.island.formattedName

        1 -> mc.session.username
        2 -> if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
        3 -> mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
        4 -> Config.drpcText
        5 -> Config.drpcText2
        else -> ""
    }
}