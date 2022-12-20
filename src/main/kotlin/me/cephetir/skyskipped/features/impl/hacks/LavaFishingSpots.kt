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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.threading.BackgroundJob
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import okhttp3.internal.toImmutableList
import java.awt.Color
import kotlin.math.floor

class LavaFishingSpots : Feature() {
    private var started = false
    private var lava = listOf<BlockPos>()

    private val job = BackgroundJob("Scan Lava", 100L) {
        if (mc.thePlayer == null || mc.theWorld == null) return@BackgroundJob

        val pY = floor(mc.thePlayer.posY).toInt()
        val belowY = (pY - 64).coerceAtLeast(31)
        val aboveY = (pY + 64).coerceAtMost(189)
        val l = mutableListOf<BlockPos>()
        var x = floor(mc.thePlayer.posX) - 64
        while (x < mc.thePlayer.posX + 64) {
            for (y in belowY until aboveY) {
                var z = floor(mc.thePlayer.posZ) - 64
                while (z < mc.thePlayer.posZ + 64) {
                    val pos = BlockPos(x, y.toDouble(), z)
                    val state = mc.theWorld.getBlockState(pos)
                    if (state.block == Blocks.lava || state.block == Blocks.flowing_lava)
                        l.add(pos)
                    ++z
                }
            }
            ++x
        }

        lava = l.toImmutableList()
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        if (Config.lavaFishingEsp && mc.theWorld != null && SkyblockListener.island == SkyblockIsland.CrystalHollows && !started) {
            started = true
            BackgroundScope.launchLooping(job)
        } else if (started) {
            BackgroundScope.cancel(job)
            started = false
        }
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Config.lavaFishingEsp || lava.isEmpty() || SkyblockListener.island != SkyblockIsland.CrystalHollows || !started) return

        val (viewerX, viewerY, viewerZ) = RenderUtils.getViewerPos(event.partialTicks)
        for (pos in lava) {
            val x = pos.x - viewerX
            val y = pos.y - viewerY
            val z = pos.z - viewerZ

            RenderUtils.drawFilledBoundingBox(AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), Color.RED.rgb)
        }
    }
}