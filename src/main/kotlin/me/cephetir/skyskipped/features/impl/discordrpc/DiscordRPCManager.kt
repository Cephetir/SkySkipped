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
    override val APP_ID = 1102633365858947162L
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
        .setDetails(getLine(Config.drpcDetail.value))
        .setState(getLine(if (Config.drpcState.value == 4) 5 else Config.drpcState.value))
        .build()

    private fun getLine(line: Int): String = when (line) {
        0 ->
            if (SkyblockListener.island == SkyblockIsland.Unknown)
                if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
            else SkyblockListener.island.formattedName

        1 -> mc.session.username
        2 -> if (mc.isIntegratedServerRunning) "Singleplayer" else mc.currentServerData?.serverIP ?: "Main Menu"
        3 -> mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
        4 -> Config.drpcText.value
        5 -> Config.drpcText2.value
        else -> ""
    }
}