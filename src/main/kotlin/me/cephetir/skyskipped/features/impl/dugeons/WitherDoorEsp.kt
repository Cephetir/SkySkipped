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

import me.cephetir.bladecore.utils.threading.BackgroundJob
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinRenderManager
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color
import java.util.*

class WitherDoorEsp : Feature() {
    private var witherDoors = LinkedList<BlockPos>()
    private var job: BackgroundJob? = null

    private val roomSize = 32
    private val startX = -185
    private val startZ = -185

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (Config.witherDoorEsp && Cache.inDungeon && witherDoors.isNotEmpty()) {
            val rm = mc.renderManager as IMixinRenderManager
            val renderPosX = rm.renderPosX
            val renderPosY = rm.renderPosY
            val renderPosZ = rm.renderPosZ

            for (door in witherDoors) {
                val x = door.x - renderPosX
                val y = door.y - renderPosY
                val z = door.z - renderPosZ
                val aabb = AxisAlignedBB(
                    x - 1,
                    y - 2,
                    z - 1,

                    x + 2,
                    y + 2,
                    z + 2
                )
                RenderUtils.drawFilledBoundingBox(aabb, Color.RED.rgb)
            }
        }
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        if (job != null) {
            BackgroundScope.cancel(job!!)
            job = null
        }
        witherDoors.clear()
    }

    init {
        safeListener<ClientTickEvent> {
            if (job == null && Config.witherDoorEsp && Cache.inDungeon)
                job = BackgroundScope.launchLooping("Wither Door Esp Scan", 500L) { scan() }
        }
    }

    private fun scan() {
        val wds = LinkedList<BlockPos>()
        var x = 0
        x@ while (x < 11) {
            var z = 0
            z@ while (z < 11) {
                if (mc.theWorld == null) return
                val xPos = startX + x * (roomSize shr 1)
                val zPos = startZ + z * (roomSize shr 1)

                if (!mc.theWorld.getChunkFromChunkCoords(xPos shr 4, zPos shr 4).isLoaded) {
                    wds.removeIf { witherDoors.contains(it) }
                    wds.addAll(witherDoors)
                    witherDoors = wds
                    continue@z
                }
                if (isColumnAir(xPos, zPos)) continue@z

                if (isDoor(xPos, zPos)) {
                    val blockPos = BlockPos(xPos, 71, zPos)
                    val bState = mc.theWorld.getBlockState(blockPos)
                    if (bState.block == Blocks.coal_block || bState.block == Blocks.stained_hardened_clay)
                        wds.add(blockPos)
                }
                z++
            }
            x++
        }

        witherDoors = wds
    }

    private fun isDoor(x: Int, z: Int): Boolean {
        val xPlus4 = isColumnAir(x + 4, z)
        val xMinus4 = isColumnAir(x - 4, z)
        val zPlus4 = isColumnAir(x, z + 4)
        val zMinus4 = isColumnAir(x, z - 4)
        return xPlus4 && xMinus4 && !zPlus4 && !zMinus4 || !xPlus4 && !xMinus4 && zPlus4 && zMinus4
    }

    private fun isColumnAir(x: Int, z: Int): Boolean {
        for (y in 12..140)
            if (mc.theWorld.getBlockState(BlockPos(x, y, z)).block != Blocks.air)
                return false
        return true
    }
}