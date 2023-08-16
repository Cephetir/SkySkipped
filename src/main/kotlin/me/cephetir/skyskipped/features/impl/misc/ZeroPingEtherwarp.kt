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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getExtraAttributes
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getSkyBlockID
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import org.lwjgl.util.vector.Vector3f
import kotlin.math.ceil

class ZeroPingEtherwarp : Feature() {
    init {
        listener<PlayerInteractEvent> {
            if (!Config.zeroPingEtherwarp.value || it.entityPlayer != player || !player!!.isSneaking || it.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return@listener
            val heldItem = player!!.heldItem
            if (!heldItem.getSkyBlockID().containsAny("ASPECT_OF_THE_VOID", "ASPECT_OF_THE_END")) return@listener
            val extraAttributes = heldItem.getExtraAttributes()
            val etherwarp = extraAttributes?.getInteger("ethermerge") == 1
            if (!etherwarp) return@listener
            val tuners = extraAttributes!!.getInteger("tuned_transmission")

            val pos = raycast(player!!, 1f, 57f + tuners, 0.1f) ?: return@listener
            player!!.setPosition(pos.x + 0.5, pos.y + 1.05, pos.z + 0.5)
            player!!.setVelocity(0.0, 0.0, 0.0)
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