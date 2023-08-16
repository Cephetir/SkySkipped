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

package me.cephetir.skyskipped.features.impl.dugeons

import gg.essential.universal.UChat
import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.RaytracingUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.network.play.server.S22PacketMultiBlockChange
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


object GhostBlocks : Feature() {
    private val blacklist = listOf(
        "Chest"
    )
    private var lastBlock = -1L
    private var ghostBlocks = HashMap<BlockPos, IBlockState>()
    var active = false

    init {
        listener<ClientTickEvent> {
            if (!active || player == null || world == null || mc.currentScreen != null) return@listener
            if (System.currentTimeMillis() - lastBlock < Config.ghostBlocksDelay.value) return@listener
            lastBlock = System.currentTimeMillis()

            val ray = RaytracingUtils.raytraceToBlock(reach = Config.ghostBlocksRange.value)
            if (ray == null || ray.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return@listener
            val pos = ray.blockPos
            val state = world!!.getBlockState(pos)
            val blockId = state.block.registryName.split(":")[1]
            blacklist.forEach { entry ->
                if (blockId.contains(entry, true))
                    return@listener
            }
            ghostBlocks[pos] = state
            world!!.setBlockToAir(pos)
        }
        listener<PacketEvent.Receive> {
            if (ghostBlocks.isEmpty()) return@listener
            when (val packet = it.packet) {
                is S23PacketBlockChange -> ghostBlocks.remove(packet.blockPosition)

                is S22PacketMultiBlockChange -> packet.changedBlocks.forEach { data ->
                    ghostBlocks.remove(data.pos)
                }
            }
        }
        listener<WorldEvent.Load> {
            lastBlock = -1L
            ghostBlocks.clear()
            active = false
        }
    }

    fun restore() {
        if (player == null || world == null) return
        for ((pos, state) in ghostBlocks)
            world!!.setBlockState(pos, state)
        ghostBlocks.clear()
        UChat.chat("§cSkySkipped §f:: §eRestored ghost blocks!")
    }
}