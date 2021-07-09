package cephetir.simplemod.core;

import cephetir.simplemod.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collection;

public class InDungeon {
    private boolean isInDungeon = false;
    private int percentage;
    private String dungeonName;

    public boolean getIsInDungeon() {
        return isInDungeon;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setIsInDungeon(boolean isInDungeon) {
        this.isInDungeon = isInDungeon;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void inDungeon() {
        try {
            Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
            Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
            boolean foundDungeon = false;
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
            isInDungeon = foundDungeon;
        } catch (NullPointerException event) {
            event.printStackTrace();
        }
    }
}
