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

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.bladecore.utils.world
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class PingUtils(private var ticks: Int, private val text: String, private val sound: Boolean = true, private val dynamicText: (() -> String)? = null) {

    init {
        listener<ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START) return@listener
            if (world == null || player == null) return@listener BladeEventBus.unsubscribe(this)

            if (ticks <= 0) BladeEventBus.unsubscribe(this, true)
            if (sound && ticks % 2 == 0) player!!.playSound("random.orb", 1f, 1f)
            ticks--
        }

        safeListener<RenderGameOverlayEvent.Text> {
            GlStateManager.pushMatrix()
            GlStateManager.scale(1.5f, 1.5f, 1.5f)
            val displayText = dynamicText?.invoke() ?: text
            mc.fontRendererObj.drawStringWithShadow(
                "§4§l$displayText",
                it.resolution.scaledWidth / 1.5f / 2f - mc.fontRendererObj.getStringWidth(displayText) / 2f,
                it.resolution.scaledHeight / 1.5f / 2f - 6.75f, -1
            )
            GlStateManager.scale(1f / 1.5f, 1f / 1.5f, 1f / 1.5f)
            GlStateManager.popMatrix()
        }

        BladeEventBus.subscribe(this)
    }
}