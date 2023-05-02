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

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

class ItemRadius : Feature() {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Cache.inDungeon && Config.onlyDungeonRadius.value) return
        if (mc.gameSettings.thirdPersonView == 0 && Config.onlyThirdPersonRadius.value) return
        if (mc.thePlayer?.heldItem == null) return

        if (Config.gyroRadius.value && mc.thePlayer.heldItem.displayName.stripColor().contains("Gyrokinetic Wand")) {
            val pos = mc.thePlayer.rayTrace(25.0, event.partialTicks)?.blockPos ?: return
            if (mc.theWorld.getBlockState(pos).block == Blocks.air) return
            printdev("drawing gyro cycle")
            RenderUtils.drawCycle(
                pos.x + 0.5, pos.y + 1.0, pos.z + 0.5,
                10f, 0.2f,
                if (Config.radiusColorChroma.value) RenderUtils.getChroma(3000f, 0) else Color(
                    Config.radiusColorR.value.toInt(),
                    Config.radiusColorG.value.toInt(),
                    Config.radiusColorB.value.toInt()
                ).rgb,
                event.partialTicks
            )
        } else if (Config.hypRadius.value && mc.thePlayer.heldItem.displayName.stripColor().containsAny("Hyperion", "Scylla", "Astraea", "Valkyrie")) {
            printdev("drawing hyp cycle")
            val x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks
            val y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks + 1.0
            val z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks
            RenderUtils.drawCycle(
                x, y, z,
                6f, 0.2f,
                if (Config.radiusColorChroma.value) RenderUtils.getChroma(3000f, 0) else Color(
                    Config.radiusColorR.value.toInt(),
                    Config.radiusColorG.value.toInt(),
                    Config.radiusColorB.value.toInt()
                ).rgb,
                event.partialTicks
            )
        }
    }
}