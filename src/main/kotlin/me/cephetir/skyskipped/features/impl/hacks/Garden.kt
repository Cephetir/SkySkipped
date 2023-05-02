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

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.BackgroundJob
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color
import kotlin.math.roundToInt

class Garden : Feature() {
    private var scanning = false
    private val job = BackgroundJob("SkySkipped Grass Scanner", 0L) { scanGrass() }

    init {
        listener<ClientTickEvent> {
            if (player != null && Config.grassEsp.value && SkyblockListener.onSkyblock && SkyblockListener.island == SkyblockIsland.Garden && !scanning) {
                BackgroundScope.launch(job)
                scanning = true
            } else if (scanning) {
                BackgroundScope.cancel(job)
                scanning = false
            }
        }

        listener<RenderWorldLastEvent> {
            if (!Config.grassEsp.value || !scanning) return@listener

            val (viewerX, viewerY, viewerZ) = RenderUtils.getViewerPos(it.partialTicks)
            for (pos in grass) {
                val x = pos.x - viewerX
                val y = pos.y - viewerY
                val z = pos.z - viewerZ
                RenderUtils.drawFilledBoundingBox(AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), Color.RED.rgb)
            }
        }

        listener<WorldEvent.Unload> {
            if (scanning) {
                BackgroundScope.cancel(job)
                scanning = false
            }
        }
    }

    private var grass = emptyList<BlockPos>()
    private fun scanGrass() {
        val poses = mutableListOf<BlockPos>()
        for (x in player!!.posX.roundToInt() - 32..player!!.posX.roundToInt() + 32)
            for (y in player!!.posY.roundToInt() - 5..player!!.posY.roundToInt() + 5)
                for (z in player!!.posZ.roundToInt() - 32..player!!.posZ.roundToInt() + 32) {
                    val pos = BlockPos(x, y, z)
                    val block = world!!.getBlockState(pos).block
                    if (block == Blocks.tallgrass || block == Blocks.double_plant)
                        poses.add(pos)
                }
        grass = poses
    }
}