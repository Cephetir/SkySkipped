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
import java.util.regex.Pattern
import kotlin.math.ceil

object TextUtils {
    private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]")
    private val SCOREBOARD_CHARACTERS = Pattern.compile("[^a-z A-Z:0-9/'.]")
    private val INTEGER_CHARACTERS = Pattern.compile("[^0-9]")

    @JvmStatic
    fun String.stripColor(): String {
        return STRIP_COLOR_PATTERN.matcher(this).replaceAll("")
    }

    @JvmStatic
    fun String.keepScoreboardCharacters(): String {
        return SCOREBOARD_CHARACTERS.matcher(this).replaceAll("")
    }

    @JvmStatic
    fun String.isNumeric(): Boolean {
        return this.matches(Regex("-?\\d+(\\.\\d+)?")) // ty stackoverflow
    }

    fun CharSequence?.containsAny(vararg sequences: CharSequence?): Boolean {
        return if (this == null) false
        else sequences.any { it != null && this.contains(it, true) }
    }

    fun CharSequence?.containsAny(sequences: List<CharSequence>): Boolean {
        return if (this == null) false
        else sequences.any { it != "" && this.contains(it, true) }
    }

    private val suffixes = TreeMap<Long, String>()
    fun String.keepIntegerCharactersOnly(): String {
        return INTEGER_CHARACTERS.matcher(this).replaceAll("")
    }

    fun join(list: List<*>, delimeter: String?): String {
        if (list.isEmpty()) return ""
        val stringBuilder = StringBuilder()
        for (i in 0 until list.size - 1) {
            stringBuilder.append(list[i].toString()).append(delimeter)
        }
        stringBuilder.append(list[list.size - 1].toString())
        return stringBuilder.toString()
    }

    init {
        suffixes[1000L] = "k"
        suffixes[1000000L] = "m"
        suffixes[1000000000L] = "b"
    }

    fun Long.formatTime(): String {
        var seconds = ceil(this / 1000.0).toLong()
        val hr = seconds / (60 * 60)
        seconds -= hr * 60 * 60
        val min = seconds / 60
        seconds -= min * 60
        val stringBuilder = StringBuilder()
        if (hr > 0) {
            stringBuilder.append(hr).append("h ")
        }
        if (hr > 0 || min > 0) {
            stringBuilder.append(min).append("m ")
        }
        if (hr > 0 || min > 0 || seconds > 0) {
            stringBuilder.append(seconds).append("s ")
        }
        return stringBuilder.toString()
    }

    fun insertDashUUID(uuid: String?): String {
        var sb = StringBuilder(uuid)
        sb.insert(8, "-")
        sb = StringBuilder(sb.toString())
        sb.insert(13, "-")
        sb = StringBuilder(sb.toString())
        sb.insert(18, "-")
        sb = StringBuilder(sb.toString())
        sb.insert(23, "-")
        return sb.toString()
    }
}