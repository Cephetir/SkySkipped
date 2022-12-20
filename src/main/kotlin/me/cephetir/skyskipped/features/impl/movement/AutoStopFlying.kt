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

package me.cephetir.skyskipped.features.impl.movement

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoStopFlying : Feature() {

    // #HypixelBestDevs
    @SubscribeEvent
    fun onWorld(event: WorldEvent.Load) {
        if (!Config.stopFly) return
        BackgroundScope.launch {
            val last = System.currentTimeMillis()
            var state = false
            while (!state) {
                if (System.currentTimeMillis() - last >= 6000) return@launch
                state = mc.thePlayer?.capabilities?.isFlying ?: continue && Cache.onIsland
                delay(100)
            }
            mc.thePlayer.capabilities.isFlying = false
        }
    }
}