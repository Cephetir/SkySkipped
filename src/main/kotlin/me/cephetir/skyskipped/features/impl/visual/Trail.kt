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

package me.cephetir.skyskipped.features.impl.visual

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Trail : Feature() {
    private var timer = 0L
    private val particle: EnumParticleTypes
        get() = EnumParticleTypes.values().find { it.name.equals(Config.trailParticle, true) } ?: EnumParticleTypes.DRIP_LAVA

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (System.currentTimeMillis() - timer < Config.trailInterval) return
        timer = System.currentTimeMillis()
        if (event.phase != TickEvent.Phase.START || !Config.trail || mc.thePlayer == null) return
        if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0) return

        mc.theWorld.spawnParticle(
            particle,
            mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ,
            0.0, 0.0, 0.0
        )
    }
}