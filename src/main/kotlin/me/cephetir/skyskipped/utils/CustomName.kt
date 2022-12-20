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

package me.cephetir.skyskipped.utils

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.skyskipped.SkySkipped
import net.minecraftforge.client.event.RenderWorldLastEvent

class CustomName(val name: String, private val animated: Boolean, private val nicks: List<Nick>) {
    private var time = -1L
    private var currentIndex = 0

    init {
        listener<RenderWorldLastEvent> {
            if (!animated) return@listener
            val currentTime = System.currentTimeMillis()
            if (time == -1L) time = currentTime
            else if (currentTime - time > nicks[currentIndex].delay) {
                if (++currentIndex >= nicks.size) currentIndex = 0
                time = currentTime
                SkySkipped.cosmeticCache.entries().removeIf { it.key.contains(name) }
            }
        }

        BladeEventBus.subscribe(this)
    }

    fun getNick(): Nick = nicks[currentIndex]

    data class Nick(val nick: String, val prefix: String, val delay: Int)
}