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

import kotlin.math.ceil

object TextUtils {
    private val STRIP_COLOR_PATTERN = Regex("(?i)§[\\dA-FK-OR]")
    private val SCOREBOARD_CHARACTERS = Regex("[^a-z A-Z:\\d/'.]")
    private val INTEGER_CHARACTERS = Regex("\\D")
    private val NUMERIC = Regex("-?\\d+(\\.\\d+)?")

    @JvmStatic
    fun String.stripColor(): String = STRIP_COLOR_PATTERN.replace(this, "")

    @JvmStatic
    fun String.keepScoreboardCharacters(): String = SCOREBOARD_CHARACTERS.replace(this, "")

    @JvmStatic
    fun String.isNumeric(): Boolean = this.matches(NUMERIC) // ty stackoverflow

    fun CharSequence?.containsAny(vararg sequences: CharSequence): Boolean = this.containsAny(sequences.toList())

    fun CharSequence?.containsAny(sequences: List<CharSequence>): Boolean =
        if (this == null) false
        else sequences.any { it != "" && this.contains(it, true) }

    fun String.keepIntegerCharactersOnly(): String = INTEGER_CHARACTERS.replace(this, "")

    fun join(list: List<*>, delimeter: String?): String {
        if (list.isEmpty()) return ""
        val stringBuilder = StringBuilder()
        for (i in 0 until list.size - 1) {
            stringBuilder.append(list[i].toString()).append(delimeter)
        }
        stringBuilder.append(list[list.size - 1].toString())
        return stringBuilder.toString()
    }

    fun Long.formatTime(): String {
        var seconds = ceil(this / 1000.0).toLong()
        val hr = seconds / (60 * 60)
        seconds -= hr * 60 * 60
        val min = seconds / 60
        seconds -= min * 60
        val stringBuilder = StringBuilder()
        if (hr > 0) stringBuilder.append(hr).append("h ")
        if (hr > 0 || min > 0) stringBuilder.append(min).append("m ")
        if (hr > 0 || min > 0 || seconds > 0) stringBuilder.append(seconds).append("s ")
        return stringBuilder.toString()
    }
}