package cephetir.simplemod.listeners;

import cephetir.simplemod.config.Config;
import cephetir.simplemod.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class InDungeon {
    @SubscribeEvent
    public void inDungeon(WorldEvent.Load event) {
        try {
            Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
            Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
            boolean foundDungeon = false;
            int percentage = 0;
            String dungeonName = null;
            for (Score sc : scores) {
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
                String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
                if (strippedLine.contains("Dungeon Cleared: ")) {
                    foundDungeon = true;
                    percentage = Integer.parseInt(strippedLine.substring(17));
                }
                if (ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()).startsWith(" §7⏣")) {
                    dungeonName = strippedLine.trim();
                }
            }
            Config.isInDungeon = foundDungeon;
            Config.dungeonPercentage = percentage;
            Config.dungeonName = dungeonName;
        } catch (NullPointerException e) {
            //
        }
    }

    @SubscribeEvent
    public void notInDungeon(WorldEvent.Unload event) {
        Config.isInDungeon = false;
        Config.dungeonName = "dungeonName";
        Config.dungeonPercentage = 0;
    }
}
