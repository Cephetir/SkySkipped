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
import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.utils.render.RenderUtils
import me.cephetir.skyskipped.utils.skyblock.PingUtils
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

object DungeonScan {
    private const val roomSize = 32
    private const val startX = -185
    private const val startZ = -185
    private const val adminRoom = -1989372370

    private var lastScanTime = 0L
    private var isScanning = false
    private var hasScanned = false
    private val enabled: Boolean
        get() = Config.witherDoorEsp.value || Config.adminRoom.value
    private val shouldScan: Boolean
        get() = !isScanning && !hasScanned && Cache.inDungeon && System.currentTimeMillis() - lastScanTime >= 500
    private var pingedAdminRoom = false
    private val witherDoors = ConcurrentHashMap<BlockPos, Boolean>()

    init {
        listener<TickEvent.ClientTickEvent> {
            if (!enabled || it.phase != TickEvent.Phase.START || !shouldScan) return@listener

            lastScanTime = System.currentTimeMillis()
            isScanning = true
            BackgroundScope.launch {
                scanDungeon()
                isScanning = false
            }
        }

        listener<WorldEvent.Load> {
            isScanning = false
            hasScanned = false
            pingedAdminRoom = false
            witherDoors.clear()
        }

        listener<RenderWorldLastEvent> {
            if (!Config.witherDoorEsp.value || witherDoors.isEmpty()) return@listener

            val (renderPosX, renderPosY, renderPosZ) = RenderUtils.getViewerPos(it.partialTicks)
            for ((door, isBlood) in witherDoors) {
                val x = door.x - renderPosX
                val y = door.y - renderPosY
                val z = door.z - renderPosZ
                val aabb = AxisAlignedBB(
                    x - 1, y - 2, z - 1,
                    x + 2, y + 2, z + 2
                )
                val color = if (isBlood) Color.RED.rgb else Color.BLACK.rgb
                RenderUtils.drawFilledBoundingBox(aabb, color)
            }
        }
    }

    private fun scanDungeon() {
        var allLoaded = true

        x@ for (x in 0..10) {
            z@ for (z in 0..10) {
                if (world == null) break@x
                val xPos = startX + x * (roomSize shr 1)
                val zPos = startZ + z * (roomSize shr 1)

                if (!world!!.getChunkFromChunkCoords(xPos shr 4, zPos shr 4).isLoaded) {
                    allLoaded = false
                    continue@z
                }

                when (getRoom(xPos, zPos)) {
                    0 -> if (Config.adminRoom.value && !pingedAdminRoom) {
                        UChat.chat("§cSkySkipped §f:: §aFound admin room at X: $xPos, Z: $zPos!")
                        PingUtils(100, "Admin Room!")
                        pingedAdminRoom = true
                    }

                    1 -> witherDoors[BlockPos(xPos, 71, zPos)] = false
                    2 -> witherDoors[BlockPos(xPos, 71, zPos)] = true
                }
            }
        }

        if (allLoaded)
            hasScanned = true
    }

    private fun getRoom(x: Int, z: Int): Int {
        val roomCore = getCore(x, z)
        if (roomCore == -318865360) return 3

        return when {
            roomCore == adminRoom -> 0
            isDoor(x, z) -> {
                val bs = world!!.getBlockState(BlockPos(x, 69, z))
                when (bs.block) {
                    Blocks.coal_block -> 1
                    Blocks.stained_hardened_clay -> 2
                    else -> 3
                }
            }

            else -> 3
        }
    }

    private fun isColumnAir(x: Int, z: Int): Boolean {
        for (y in 12..140)
            if (world!!.getBlockState(BlockPos(x, y, z)).block != Blocks.air)
                return false
        return true
    }

    private fun isDoor(x: Int, z: Int): Boolean {
        val xPlus4 = isColumnAir(x + 4, z)
        val xMinus4 = isColumnAir(x - 4, z)
        val zPlus4 = isColumnAir(x, z + 4)
        val zMinus4 = isColumnAir(x, z - 4)
        return xPlus4 && xMinus4 && !zPlus4 && !zMinus4 || !xPlus4 && !xMinus4 && zPlus4 && zMinus4
    }

    private fun getCore(x: Int, z: Int): Int {
        val blocks = arrayListOf<Int>()
        for (y in 140 downTo 12) {
            val id = Block.getIdFromBlock(world!!.getBlockState(BlockPos(x, y, z)).block)
            if (id != 5 && id != 54)
                blocks.add(id)
        }
        return blocks.joinToString("").hashCode()
    }
}