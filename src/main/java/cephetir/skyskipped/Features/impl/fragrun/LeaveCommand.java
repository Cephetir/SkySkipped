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

package cephetir.skyskipped.Features.impl.fragrun;

import cephetir.skyskipped.utils.TextUtils;
import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaveCommand extends CommandBase {
    private boolean started = false;

    @Override
    public String getCommandName() {
        return "leavedungeon";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ld");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            start();
        }
    }

    public void start() {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return;
        started = true;

        new Thread(() -> {
            try {
                Thread.sleep(300L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/play skyblock");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
            Collection<Score> scores = scoreboard.getSortedScores(scoreObjective);
            boolean ok = false;
            for (Score sc : scores) {
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(sc.getPlayerName());
                String strippedLine = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, sc.getPlayerName()))).trim();
                if (strippedLine.contains("Dungeon Hub")) {
                    ok = true;
                }
            }

            if (!ok) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp dungeon_hub");
            }

            started = false;
        }).start();
    }
}
