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