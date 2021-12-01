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

package me.cephetir.skyskipped.features.impl.fragrun;

import gg.essential.api.EssentialAPI;
import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.List;

public class PartyCommand extends CommandBase {
    private boolean started = false;

    @Override
    public String getCommandName() {
        return "fragrun";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("frag");
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
            SkySkipped.features.getLeaveCommand().start(true);
        }
    }

    public void start() {
        if (started || !EssentialAPI.getMinecraftUtil().isHypixel()) return;
        started = true;
        MinecraftForge.EVENT_BUS.register(this);
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
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/p leave");
                    timer(200L);
                    break;
                }
                case 1: {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/p " + Config.BotName);
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
