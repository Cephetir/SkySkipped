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

package me.cephetir.skyskipped.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<String> fetchScoreboardLines() {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream().filter((input) ->
                input != null && input.getPlayerName() != null && !input.getPlayerName()
                        .startsWith("#")
        ).collect(Collectors.toList());
        scores = list.size() > 15 ? list.subList(15, list.size() - 1) : list;
        return scores.stream().map((it) -> ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.getPlayerName()), it.getPlayerName())).collect(Collectors.toList());
    }
}
