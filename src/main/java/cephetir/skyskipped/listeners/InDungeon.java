package cephetir.skyskipped.listeners;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class InDungeon {
    int percentage = 0;
    String dungeonName = "";

    @SubscribeEvent
    public void updateDung(TickEvent.ClientTickEvent event) {
        try {
            if(!Cache.isInDungeon) {
                Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
                ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
                Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
                boolean foundDungeon = false;
                for (Score sc : scores) {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
                    String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
                    if (strippedLine.contains("Dungeon Cleared:")) {
                        foundDungeon = true;
                        System.out.println("Dungeon found!");
                    }
                }
                Cache.isInDungeon = foundDungeon;
            }
        } catch (NullPointerException e) {
            //
        }
    }

    private boolean called = false;
    @SubscribeEvent
    public void updateDungInfo(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            if(Cache.isInDungeon) {
                if(called) return;
                called = true;
                Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
                ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
                Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
                for (Score sc : scores) {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
                    String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
                    if (strippedLine.contains("Dungeon Cleared: ")) {
                        percentage = Integer.parseInt(strippedLine.substring(17));
                    }
                    if (ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()).startsWith(" §7⏣")) {
                        dungeonName = strippedLine.trim();
                    }
                }
                Cache.dungeonPercentage = percentage;
                Cache.dungeonName = dungeonName;
                called = false;
            }
        }
    }

    @SubscribeEvent
    public void notInDungeon(WorldEvent.Unload event) {
        Cache.isInDungeon = false;
        Cache.dungeonName = "";
        Cache.dungeonPercentage = 0;
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
