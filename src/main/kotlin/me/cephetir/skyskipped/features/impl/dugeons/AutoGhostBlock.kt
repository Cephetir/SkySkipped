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

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.BlockCollisionEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.block.*
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.floor


class AutoGhostBlock : Feature() {
    private var canBeUsed = false
    private var active = false
    private var ticks = 0

    init {
        listener<ClientTickEvent> {
            if (!Config.autoGB.value || player == null || mc.currentScreen != null) return@listener
            canBeUsed = player!!.onGround &&
                    player!!.isCollidedVertically &&
                    isAllowed(world!!.getBlockState(BlockPos(player!!.posX, player!!.posY, player!!.posZ)).block)
            --ticks

            if (canBeUsed && !active && ((Config.autoGBMode.value == 1 && Config.autoGBKey.isKeyDown()) || (Config.autoGBMode.value == 0 && mc.gameSettings.keyBindSneak.isKeyDown))) {
                //world!!.setBlockToAir(BlockPos(player!!.posX, player!!.posY, player!!.posZ))
                active = true
                ticks = 2
            } else if (active && !insideOfBlock()) {
                player!!.setVelocity(0.0, player!!.motionY, 0.0)
                active = false
            } else if (active) {
                // Make ghost blocks in front
            }
        }

        listener<BlockCollisionEvent> {
            if (!Config.autoGB.value || player == null || !active || it.entity != player || it.boundingBox == null) return@listener

            if (it.boundingBox!!.maxY > player!!.entityBoundingBox.minY || mc.gameSettings.keyBindSneak.isKeyDown || ticks >= 0)
                it.cancel()
        }

        listener<WorldEvent.Load> {
            canBeUsed = false
            active = false
            ticks = 0
        }
    }

    private fun isAllowed(block: Block): Boolean {
        return block is BlockStairs || block is BlockFence || block is BlockFenceGate || block is BlockWall || block == Blocks.hopper || block is BlockSkull
    }

    private fun insideOfBlock(): Boolean {
        val box = player!!.entityBoundingBox
        for (i in floor(box.minX).toInt()..floor(box.maxX).toInt()) {
            for (j in floor(box.minY).toInt()..floor(box.maxY).toInt()) {
                for (k in floor(box.minZ).toInt()..floor(box.maxZ).toInt()) {
                    val block = world!!.getBlockState(BlockPos(i, j, k)).block
                    if (block == null || block is BlockAir) continue
                    val blockBB = block.getCollisionBoundingBox(world, BlockPos(i, j, k), world!!.getBlockState(BlockPos(i, j, k)))
                    if (blockBB == null || !box.intersectsWith(blockBB)) continue
                    return true
                }
            }
        }
        return false
    }
}