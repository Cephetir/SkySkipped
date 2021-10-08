/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
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

package cephetir.skyskipped.listeners;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.utils.TextUtils;
import gg.essential.api.EssentialAPI;
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
    public void update(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null && !EssentialAPI.getMinecraftUtil().isHypixel()) {
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

                if (foundSkyblock) {
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
                    if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                        itemheld = TextUtils.stripColor(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName());
                    }
                }

                Cache.inSkyblock = foundSkyblock;
                Cache.isInDungeon = foundDungeon;
                Cache.dungeonPercentage = percentage;
                Cache.dungeonName = dungeonName;
                Cache.itemheld = itemheld;
            } catch (NullPointerException ignored) {
            }
        }
    }
}
