/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped.features.impl.dugeons

import gg.essential.universal.UChat
import kotlinx.coroutines.launch
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.PingUtils
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AdminRoomDetection : Feature() {
    companion object {
        var scanned = false
    }

    private val adminRoom = -1989372370
    private val roomSize = 32
    private val startX = -185
    private val startZ = -185

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!scanned && Config.adminRoom && Cache.softInDungeon) {
            scanned = true
            BackgroundScope.launch { scan() }
        }
    }

    // Funny Map
    private fun scan() {
        for (x in 0..10) {
            for (z in 0..10) {
                val xPos = startX + x * (roomSize shr 1)
                val zPos = startZ + z * (roomSize shr 1)

                if (!mc.theWorld.getChunkFromChunkCoords(xPos shr 4, zPos shr 4).isLoaded) {
                    UChat.chat("§cSkySkipped §f:: §4Can't find any admin room!")
                    return
                }
                if (isColumnAir(xPos, zPos)) continue

                if (getRoom(xPos, zPos, z, x)) {
                    UChat.chat("§cSkySkipped §f:: §aFound admin room at X: $x, Z: $z!")
                    PingUtils(100, "Admin Room!")
                    return
                }
            }
        }

        UChat.chat("§cSkySkipped §f:: §4Can't find any admin room!")
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