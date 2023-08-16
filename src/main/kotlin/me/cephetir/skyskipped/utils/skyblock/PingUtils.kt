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

class PingUtils(private var ticks: Int, private val text: String, private val sound: Boolean = true, private val dynamicText: (() -> String)? = null, private val callback: () -> Unit = {}) {

    init {
        listener<ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START) return@listener
            if (world == null || player == null) return@listener BladeEventBus.unsubscribe(this)

            if (ticks <= 0) {
                callback()
                BladeEventBus.unsubscribe(this)
            }
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