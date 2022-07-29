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

package me.cephetir.skyskipped.utils.threading

import kotlinx.coroutines.CoroutineScope

class BackgroundJob(
    val name: String,
    val delay: Long,
    val block: suspend CoroutineScope.() -> Unit
) {
    override fun equals(other: Any?) = this === other
            || (other is BackgroundJob
            && name == other.name
            && delay == other.delay)

    override fun hashCode() = 31 * name.hashCode() + delay.hashCode()
}