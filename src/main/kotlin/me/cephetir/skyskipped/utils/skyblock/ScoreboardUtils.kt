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

package me.cephetir.skyskipped.utils.skyblock

import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam

object ScoreboardUtils {
    fun String.cleanSB(): String = this.stripColor().toCharArray().filter { it.code in 21..126 }.joinToString("")

    val sidebarLines: List<String>
        get() {
            val scoreboard = Minecraft.getMinecraft().theWorld?.scoreboard ?: return emptyList()
            val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()
            val scores = scoreboard.getSortedScores(objective).filter { input: Score? ->
                input != null && input.playerName != null && !input.playerName.startsWith("#")
            }.take(15)
            return scores.map {
                ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.playerName), it.playerName)
            }
        }
}