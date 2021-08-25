/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
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

package cephetir.skyskipped.listeners;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class Status {

    @SubscribeEvent
    public void updateSkyblock(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            try {
                boolean foundDungeon = false;
                boolean foundSkyblock = false;
                int percentage = 0;
                String dungeonName = "";
                String itemheld = "Nothing";

                Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
                ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
                Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
                String objectiveName = TextUtils.stripColor(scoreObjective.getDisplayName());

                if (objectiveName.startsWith("SKYBLOCK")) {
                    foundSkyblock = true;
                }

                for (Score sc : scores) {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
                    String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
                    if (strippedLine.contains("Dungeon Cleared: ")) {
                        foundDungeon = true;
                    }
                    if (Cache.isInDungeon) {
                        if (strippedLine.contains("Dungeon Cleared: ")) {
                            percentage = Integer.parseInt(strippedLine.substring(17));
                        }
                        if (ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()).startsWith(" §7⏣")) {
                            dungeonName = strippedLine.trim();
                        }
                    }
                }
                Cache.inSkyblock = foundSkyblock;
                Cache.isInDungeon = foundDungeon;
                Cache.dungeonPercentage = percentage;
                Cache.dungeonName = dungeonName;
                if (foundSkyblock) {
                    if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                        itemheld = TextUtils.stripColor(Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem().getDisplayName());
                    }
                }
                Cache.itemheld = itemheld;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
//    ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
//    Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
//    boolean foundDungeon = false;
//    int percentage = 0;
//    String dungeonName = null;
//    String dungeonTeam = null;
//    for (Score sc : scores) {
//        ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
//        String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
//        if (strippedLine.contains("Dungeon Cleared: ")) {
//            foundDungeon = true;
//            percentage = Integer.parseInt(strippedLine.substring(17));
//        }
//        if (ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()).startsWith(" §7⏣")) {
//            dungeonName = strippedLine.trim();
//        }
//        if (ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()).startsWith("§e[")) {
//            dungeonTeam = strippedLine.trim();
//        }
//    }
//    Config.isInDungeon = foundDungeon;
//    Config.dungeonPercentage = percentage;
//    Config.dungeonName = dungeonName;
//    Config.dungeonTeam = dungeonTeam;
}
