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

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ItemRadius : Feature() {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Cache.inDungeon && Config.onlyDungeonRadius) return
        if (mc.gameSettings.thirdPersonView == 0 && Config.onlyThirdPersonRadius) return
        if (mc.thePlayer?.heldItem == null) return

        if (Config.gyroRadius && mc.thePlayer.heldItem.displayName.stripColor().contains("Gyrokinetic Wand")) {
            val pos = mc.thePlayer.rayTrace(25.0, event.partialTicks)?.blockPos ?: return
            if (mc.theWorld.getBlockState(pos).block == Blocks.air) return
            printdev("drawing gyro cycle")
            RenderUtils.drawCycle(
                pos.x + 0.5, pos.y + 1.0, pos.z + 0.5,
                10f, 0.2f,
                if (Config.radiusColorChroma) RenderUtils.getChroma(3000f, 0) else Config.radiusColor.rgb,
                event.partialTicks
            )
        } else if (Config.hypRadius && mc.thePlayer.heldItem.displayName.stripColor().containsAny("Hyperion", "Scylla", "Astraea", "Valkyrie")) {
            printdev("drawing hyp cycle")
            val x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks
            val y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks + 1.0
            val z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks
            RenderUtils.drawCycle(
                x, y, z,
                6f, 0.2f,
                if (Config.radiusColorChroma) RenderUtils.getChroma(3000f, 0) else Config.radiusColor.rgb,
                event.partialTicks
            )
        }
    }
}