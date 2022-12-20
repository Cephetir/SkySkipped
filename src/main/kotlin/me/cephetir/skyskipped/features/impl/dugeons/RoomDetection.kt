/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped.features.impl.dugeons

import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.PingUtils
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class RoomDetection : Feature() {
    private var scanned = false

    private val adminRoom = -1989372370
    private val roomSize = 32
    private val startX = -185
    private val startZ = -185

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        scanned = false
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!scanned && Config.adminRoom && Cache.inDungeon) {
            scanned = true
            BackgroundScope.launch {
                delay(3000L)
                scan()
            }
        }
    }

    // Funny Map
    private fun scan() {
        while (!scanned && mc.theWorld != null) {
            var x = 0
            x@ while (x < 11) {
                var z = 0
                z@ while (z < 11) {
                    val xPos = startX + x * (roomSize shr 1)
                    val zPos = startZ + z * (roomSize shr 1)

                    if (!mc.theWorld.getChunkFromChunkCoords(xPos shr 4, zPos shr 4).isLoaded)
                        continue@z
                    if (isColumnAir(xPos, zPos)) continue

                    if (getRoom(xPos, zPos, z, x)) {
                        UChat.chat("§cSkySkipped §f:: §aFound admin room at X: $xPos, Z: $zPos!")
                        PingUtils(100, "Admin Room!")
                        scanned = true
                        break
                    }
                    z++
                }
                x++
            }

            UChat.chat("§cSkySkipped §f:: §4Can't find any admin room!")
            break
        }
    }

    private fun getRoom(x: Int, z: Int, row: Int, column: Int): Boolean {
        val rowEven = row and 1 == 0
        val columnEven = column and 1 == 0

        return when {
            rowEven && columnEven -> getRoomData(x, z)
            !rowEven && !columnEven -> adminRoom == (row - 1) * 11 + column - 1
            else -> adminRoom == if (rowEven) row * 11 + column - 1 else (row - 1) * 11 + column
        }
    }

    private fun getRoomData(x: Int, z: Int): Boolean = adminRoom == getCore(x, z)

    private fun isColumnAir(x: Int, z: Int): Boolean {
        for (y in 12..140)
            if (mc.theWorld.getBlockState(BlockPos(x, y, z)).block != Blocks.air)
                return false
        return true
    }

    private fun getCore(x: Int, z: Int): Int {
        val blocks = arrayListOf<Int>()
        for (y in 140 downTo 12) {
            val id = Block.getIdFromBlock(mc.theWorld.getBlockState(BlockPos(x, y, z)).block)
            if (id != 5 && id != 54) blocks.add(id)
        }
        return blocks.joinToString("").hashCode()
    }
}