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

package me.cephetir.skyskipped.utils.skyblock

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.math.MathUtils.round
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S37PacketStatistics
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs

object Ping {
    var ping = 0.0
    private var pingedAt = -1L
    private var checking = false
    private var lastCheck = System.currentTimeMillis()

    init {
        listener<ClientTickEvent> {
            if (player == null || world == null || System.currentTimeMillis() - lastCheck < 5000L) return@listener
            checking = true
            lastCheck = System.currentTimeMillis()
            player!!.sendQueue.networkManager.sendPacket(
                C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS),
                { pingedAt = System.nanoTime() }
            )
        }

        listener<PacketEvent.Receive> {
            if (checking && pingedAt > 0) when (it.packet) {
                is S01PacketJoinGame -> {
                    pingedAt = -1L
                    checking = false
                }

                is S37PacketStatistics -> {
                    ping = (abs(System.nanoTime() - pingedAt) / 1_000_000.0).round(2)
                    pingedAt = -1L
                    checking = false
                }
            }
        }

        BladeEventBus.subscribe(this)
    }
}