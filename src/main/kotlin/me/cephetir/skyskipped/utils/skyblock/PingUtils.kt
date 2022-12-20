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