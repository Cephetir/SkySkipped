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