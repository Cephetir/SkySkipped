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