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

package me.cephetir.skyskipped.utils.skyblock

import gg.essential.universal.ChatColor
import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class PingUtils(private var ticks: Int, private val text: String) {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return MinecraftForge.EVENT_BUS.unregister(this)

        if (ticks <= 0) MinecraftForge.EVENT_BUS.unregister(this)
        if (ticks % 4 == 0) mc.thePlayer.playSound("random.orb", 1f, 1f)
        ticks--
    }

    @SubscribeEvent
    fun draw(event: RenderGameOverlayEvent.Text) {
        GlStateManager.pushMatrix()
        GlStateManager.scale(1.5f, 1.5f, 1.5f)
        mc.fontRendererObj.drawStringWithShadow(
            ChatColor.DARK_RED.toString() + ChatColor.BOLD + text,
            event.resolution.scaledWidth / 1.5f / 2f - mc.fontRendererObj.getStringWidth(text) / 2f,
            event.resolution.scaledHeight / 1.5f / 2f - 6.75f, -1
        )
        GlStateManager.scale( 1f / 1.5f, 1f / 1.5f, 1f / 1.5f)
        GlStateManager.popMatrix()
    }
}