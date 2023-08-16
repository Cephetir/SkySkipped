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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.concurrent.ConcurrentLinkedQueue


class FishingAssist : Feature() {
    private var casted = false
    private var lastCast = -1L
    private var lastY = 0.0
    private val particles = ConcurrentLinkedQueue<Pair<Vec3, Long>>()

    init {
        listener<ClientTickEvent> {
            if (!Config.fishingAssist.value || player == null) return@listener
            val cast = player!!.fishEntity != null
            if (casted != cast && cast) lastCast = System.currentTimeMillis()
            casted = cast
            if (!cast || System.currentTimeMillis() - lastCast < 500L) return@listener
            particles.removeIf { System.currentTimeMillis() - it.second > 1000L }

            val delta = player!!.fishEntity.posY - lastY
            lastY += delta
            if (player!!.fishEntity.caughtEntity == null &&
                (delta > -0.04 || particles.none { it.first.distanceTo(player!!.fishEntity.positionVector) < 0.4 })
            ) return@listener
            mc.playerController.sendUseItem(player, world, player!!.heldItem)
            lastCast = System.currentTimeMillis()
        }

        listener<PacketEvent.Receive> {
            val packet = it.packet
            if (!Config.fishingAssist.value || packet !is S2APacketParticles) return@listener
            if (packet.particleType == EnumParticleTypes.WATER_WAKE || packet.particleType == EnumParticleTypes.SMOKE_NORMAL)
                particles.add(Pair(Vec3(packet.xCoordinate, packet.yCoordinate, packet.zCoordinate), System.currentTimeMillis()))
        }
    }
}