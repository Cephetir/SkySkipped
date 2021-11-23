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

import cephetir.skyskipped.SkySkipped;
import cephetir.skyskipped.config.Config;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
            start(false);
        }
    }

    private boolean party = false;
    public void start(boolean party) {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return;
        started = true;
        MinecraftForge.EVENT_BUS.register(this);
        this.party = party;
    }

    private int step = 0;
    private boolean startedd = false;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(startedd) return;
        new Thread(() -> {
            startedd = true;
            switch (step) {
                case 0: {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");
                    timer(Config.delay);
                    break;
                }
                case 1: {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/play skyblock");
                    timer(Config.delay);
                    break;
                }
                case 2: {
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

                    if ((Config.EndParty && !Config.BotName.equals("")) || party) SkySkipped.features.getPartyCommand().start();
                    MinecraftForge.EVENT_BUS.unregister(this);
                    started = false;
                    step = 0;
                    break;
                }
            }
            startedd = false;
        }).start();
    }

    private void timer(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        step++;
    }
}
