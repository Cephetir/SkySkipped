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
import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.math.VectorUtils.distanceTo
import me.cephetir.bladecore.utils.mc
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.*
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue

class ShinyBlocks : Feature() {
    companion object {
        var breaking: BlockPos? = null
        var shouldBreak = false

        fun mine() {
            if (mc.currentScreen == null) {
                val facing = player!!.horizontalFacing
                if (mc.playerController.onPlayerDamageBlock(breaking, facing)) {
                    mc.effectRenderer.addBlockHitEffects(breaking, MovingObjectPosition(Vec3(breaking), facing, breaking))
                    mc.thePlayer.swingItem()
                }
            } else mc.playerController.resetBlockRemoving()
        }
    }

    private val particles = ConcurrentLinkedQueue<BlockPos>()

    init {
        listener<PacketEvent.Receive> {
            if (SkyblockListener.island == SkyblockIsland.TheEnd && it.packet is S2APacketParticles && (it.packet as S2APacketParticles).particleType == EnumParticleTypes.SPELL_WITCH) {
                val packet = it.packet as S2APacketParticles
                printdev("PARTICLE IN X ${packet.xCoordinate - packet.xOffset} Y ${packet.yCoordinate - packet.yOffset} Z ${packet.zCoordinate - packet.zOffset}")
                val particle = BlockPos(packet.xCoordinate - packet.xOffset, packet.yCoordinate - packet.yOffset, packet.zCoordinate - packet.zOffset)

                var pos: BlockPos? = null
                run {
                    // Below
                    val pos1 = particle.add(0, -1, 0)
                    val state1 = world!!.getBlockState(pos1).block
                    if (state1 == Blocks.obsidian || state1 == Blocks.end_stone) {
                        pos = pos1
                        return@run
                    }

                    // Up
                    val pos2 = particle.add(0, 1, 0)
                    val state2 = world!!.getBlockState(pos2).block
                    if (state2 == Blocks.obsidian || state2 == Blocks.end_stone) {
                        pos = pos2
                        return@run
                    }

                    // +X
                    val pos3 = particle.add(1, 0, 0)
                    val state3 = world!!.getBlockState(pos3).block
                    if (state3 == Blocks.obsidian || state3 == Blocks.end_stone) {
                        pos = pos3
                        return@run
                    }

                    // -X
                    val pos4 = particle.add(-1, 0, 0)
                    val state4 = world!!.getBlockState(pos4).block
                    if (state4 == Blocks.obsidian || state4 == Blocks.end_stone) {
                        pos = pos4
                        return@run
                    }

                    // +Z
                    val pos5 = particle.add(0, 0, 1)
                    val state5 = world!!.getBlockState(pos5).block
                    if (state5 == Blocks.obsidian || state5 == Blocks.end_stone) {
                        pos = pos5
                        return@run
                    }

                    // -Z
                    val pos6 = particle.add(0, 0, -1)
                    val state6 = world!!.getBlockState(pos6).block
                    if (state6 == Blocks.obsidian || state6 == Blocks.end_stone) {
                        pos = pos6
                        return@run
                    }
                }
                if (pos == null) return@listener

                if (!particles.contains(pos)) particles.add(pos)
            }
        }

        listener<RenderWorldLastEvent> {
            shouldBreak = false
            if (player == null || world == null) return@listener

            val (viewerX, viewerY, viewerZ) = RenderUtils.getViewerPos(it.partialTicks)
            if (particles.isNotEmpty()) {
                if (Config.shinyBlocksAura) {
                    val pos = particles.minByOrNull { pos -> pos.distanceTo(player!!.position) }!!
                    if (pos.distanceTo(player!!.position) < 4.5) {
                        breaking = pos
                        shouldBreak = true
                    }
                }

                if (Config.shinyBlocksEsp) for (block in particles) {
                    val x = block.x - viewerX
                    val y = block.y - viewerY
                    val z = block.z - viewerZ
                    RenderUtils.drawFilledBoundingBox(AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), Color.RED.rgb)
                }
            }
        }

        listener<WorldEvent.Load> {
            particles.clear()
            shouldBreak = false
        }
    }
}