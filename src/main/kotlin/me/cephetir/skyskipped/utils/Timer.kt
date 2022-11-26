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

import java.util.*

class Timer {
    companion object {
        private val runningTimers = LinkedList<Timer>()
        private var currentTime = -1L

        fun update() {
            if (runningTimers.isEmpty()) return
            currentTime = System.currentTimeMillis()
            runningTimers.forEach { it.time = currentTime - it.startTime }
        }
    }

    private var startTime = System.currentTimeMillis()
    var time = -1L

    fun reset() {
        startTime = System.currentTimeMillis()
    }

    fun start() {
        if (!runningTimers.contains(this))
            runningTimers.add(this)
    }

    fun stop() {
        runningTimers.remove(this)
    }
}