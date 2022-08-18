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
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

class ItemRadius : Feature() {
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Cache.inDungeon && Config.onlyDungeonRadius) return

        if (mc.thePlayer?.heldItem == null) return
        if (Config.gyroRadius && mc.thePlayer.heldItem.displayName.stripColor().contains("Gyrokinetic Wand")) {
            val pos = mc.thePlayer.rayTrace(25.0, event.partialTicks)?.blockPos ?: return
            if (mc.theWorld.getBlockState(pos).block == Blocks.air) return
            printdev("drawing gyro cycle")
            RenderUtils.drawCycle(
                pos.x + 0.5, pos.y + 1.0, pos.z + 0.5,
                10f, 0.5f, Color.GREEN.rgb, event.partialTicks
            )
        } else if (Config.hypRadius && mc.thePlayer.heldItem.displayName.stripColor().contains("Hyperion")) {
            val pos = mc.thePlayer.position
            printdev("drawing hyp cycle")
            RenderUtils.drawCycle(
                pos.x + 0.5, pos.y + 1.0, pos.z + 0.5,
                6f, 0.5f, Color.GREEN.rgb, event.partialTicks
            )
        }
    }
}