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

package me.cephetir.skyskipped.features.impl.cofl

import me.cephetir.skyskipped.SkySkipped
import net.minecraft.client.Minecraft
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class CoflWebsocket(version: String) :
    WebSocketClient(URI("wss://sky.coflnet.com/modsocket?version=$version&player=${Minecraft.getSessionInfo()["X-Minecraft-Username"]}&SId=${CoflSessions.getSession(Minecraft.getSessionInfo()["X-Minecraft-Username"]!!).uuid}")) {

    override fun onOpen(handshake: ServerHandshake) {
        SkySkipped.logger.info("Opened Cofl websocket successfully!")
    }

    override fun onMessage(string: String) {
        SkySkipped.logger.info("Received: $string")
        AutoFuckingCoflBuy.coflMessage(string)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        SkySkipped.logger.info("Closed Cofl websocket! Reason: $reason")
    }

    override fun onError(error: Exception) {
        SkySkipped.logger.error("Cofl websocket error!", error)
    }

    override fun send(text: String) {
        SkySkipped.logger.info("Sending: $text")
        super.send(text)
    }
}