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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.network.play.server.S0EPacketSpawnObject

class StopFallingBlocks : Feature() {
    init {
        listener<PacketEvent.Receive> {
            if (!Config.stopFallingBlocks || !SkyblockListener.onSkyblock || it.packet !is S0EPacketSpawnObject || (it.packet as S0EPacketSpawnObject).type != 70) return@listener
            it.cancel()
        }
    }
}