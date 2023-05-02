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

package me.cephetir.skyskipped.features.impl.visual

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.ceil


class AOTVDisplay : Feature() {

    init {
        listener<RenderWorldLastEvent> {
            if (!Config.aotvDisplay.value || player == null || mc.currentScreen != null) return@listener
            val item = player!!.heldItem ?: return@listener
            if (!item.hasDisplayName() || !item.displayName.containsAny("Aspect of the End", "Aspect of the Void")) return@listener
            if (Config.aotvDisplayKey.value != 0 && !Config.aotvDisplayKey.isKeyDown()) return@listener

            var reach = if (mc.gameSettings.keyBindSneak.isKeyDown) {
                if (Config.aotvDisplayDisableEther.value) return@listener
                58f
            } else 8f
            if (Config.aotvDisplayTuners.value)
                reach += 4
            val pos = raycast(player!!, it.partialTicks, reach, 1f) ?: return@listener

            val (renderPosX, renderPosY, renderPosZ) = RenderUtils.getViewerPos(it.partialTicks)
            val box = AxisAlignedBB(
                pos.x - renderPosX, pos.y - renderPosY, pos.z - renderPosZ,
                pos.x - renderPosX + 1, pos.y - renderPosY + 1, pos.z - renderPosZ + 1
            )
            RenderUtils.drawFilledBoundingBox(box, Color.GREEN.rgb)
            //RenderUtils.drawLine(vec, vec.addVector(0.0, 2.0, 0.0), 5f, Config.aotvDisplayColor.rgb)
        }
    }

    private fun raycast(player: EntityPlayerSP, partialTicks: Float, dist: Float, step: Float): BlockPos? {
        val pos = Vector3f(player.posX.toFloat(), player.posY.toFloat() + player.getEyeHeight(), player.posZ.toFloat())
        val lookVec3 = player.getLook(partialTicks)
        val look = Vector3f(lookVec3.xCoord.toFloat(), lookVec3.yCoord.toFloat(), lookVec3.zCoord.toFloat())
        look.scale(step / look.length())
        val stepCount = ceil(dist / step).toInt()
        var position: BlockPos? = null
        for (i in 0 until stepCount) {
            Vector3f.add(pos, look, pos)
            position = BlockPos(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            val state = world!!.getBlockState(position)
            if (state.block != Blocks.air) {
                Vector3f.sub(pos, look, pos)
                look.scale(0.1f)
                for (j in 0..10) {
                    Vector3f.add(pos, look, pos)
                    val position2 = BlockPos(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                    val state2 = world!!.getBlockState(position2)
                    if (state2.block != Blocks.air)
                        return position2
                }
                return position
            }
        }
        return position
    }
}