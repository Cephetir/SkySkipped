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

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraft.util.Vec3i
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import kotlin.math.abs


class AutoGhostBlock : Feature() {

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || event.phase != TickEvent.Phase.START) return
        if (!Config.autoGB || !Cache.isInDungeon) return
        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            val playerPos: BlockPos = mc.thePlayer.position
            val playerVec: Vec3 = mc.thePlayer.positionVector
            val vec3i = Vec3i(3, 1, 3)
            val vec3i2 = Vec3i(3, 2, 3)
            for (blockPos in BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                val diffX = abs(blockPos.x + 0.5 - playerVec.xCoord)
                val diffZ = abs(blockPos.z + 0.5 - playerVec.zCoord)
                val diffY = blockPos.y - playerVec.yCoord
                if (diffX < 1 && diffZ < 1) {
                    val blockState: IBlockState = mc.theWorld.getBlockState(blockPos)

                    if (isStair(blockState) && diffY == -0.5)
                        mc.theWorld.setBlockToAir(blockPos)
                    else if (blockState.block == Blocks.skull && diffY == 0.0 && diffX < 0.5 && diffZ < 0.5)
                        mc.theWorld.setBlockToAir(blockPos)
                    else if (blockState.block == Blocks.hopper && diffY == -0.625)
                        mc.theWorld.setBlockToAir(blockPos)
                    else if (isFence(blockState) && diffY <= 0 && diffX < 0.5 && diffZ < 0.5)
                        mc.theWorld.setBlockToAir(blockPos)
                }
            }
        } else if (mc.gameSettings.keyBindJump.isKeyDown) {
            val playerPos: BlockPos = mc.thePlayer.position
            val playerVec: Vec3 = mc.thePlayer.positionVector
            val vec3i = Vec3i(3, 2, 3)
            val vec3i2 = Vec3i(3, 0, 3)
            for (blockPos in BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                val diffX = abs(blockPos.x + 0.5 - playerVec.xCoord)
                val diffZ = abs(blockPos.z + 0.5 - playerVec.zCoord)
                val diffY = blockPos.y - playerVec.yCoord
                if (diffX < 1 && diffZ < 1) {
                    val blockState: IBlockState = mc.theWorld.getBlockState(blockPos)

                    if (isStair(blockState) && diffY > 1.2 && diffY < 1.3)
                        mc.theWorld.setBlockToAir(blockPos)
                }
            }
        }
    }

    private fun isStair(blockState: IBlockState): Boolean =
        blockState.block == Blocks.acacia_stairs || blockState.block == Blocks.birch_stairs || blockState.block == Blocks.brick_stairs || blockState.block == Blocks.stone_brick_stairs || blockState.block == Blocks.stone_stairs || blockState.block == Blocks.dark_oak_stairs || blockState.block == Blocks.jungle_stairs || blockState.block == Blocks.spruce_stairs || blockState.block == Blocks.red_sandstone_stairs || blockState.block == Blocks.sandstone_stairs || blockState.block == Blocks.nether_brick_stairs || blockState.block == Blocks.oak_stairs || blockState.block == Blocks.quartz_stairs

    private fun isFence(blockState: IBlockState): Boolean =
        blockState.block == Blocks.acacia_fence || blockState.block == Blocks.birch_fence || blockState.block == Blocks.cobblestone_wall || blockState.block == Blocks.dark_oak_fence || blockState.block == Blocks.jungle_fence || blockState.block == Blocks.spruce_fence || blockState.block == Blocks.oak_fence || blockState.block == Blocks.nether_brick_fence
}