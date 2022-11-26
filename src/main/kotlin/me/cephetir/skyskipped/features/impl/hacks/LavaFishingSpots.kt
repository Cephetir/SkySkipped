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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color
import java.util.*

class LavaFishingSpots : Feature() {
    private val lava = LinkedList<BlockPos>()

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !Config.lavaFishingEsp || SkyblockListener.island != SkyblockIsland.CrystalHollows) return

        lava.clear()
        val blocks = world?.capturedBlockSnapshots?.map { it.pos }?.filter {
            val block = mc.theWorld.getBlockState(it).block
            it.y >= 65 && (block == Blocks.lava || block == Blocks.flowing_lava)
        } ?: return
        lava.addAll(blocks)
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Config.lavaFishingEsp || lava.isEmpty() || SkyblockListener.island != SkyblockIsland.CrystalHollows) return

        for (pos in lava)
            RenderUtils.drawBox(Vec3(pos), Color.RED, event.partialTicks)
    }
}