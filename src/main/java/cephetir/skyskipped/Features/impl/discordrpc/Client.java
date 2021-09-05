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

package cephetir.skyskipped.Features.impl.discordrpc;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Client {

    @Getter
    private static final Client INSTANCE = new Client();

    @Getter
    private final DiscordRPCManager discordRPCManager = new DiscordRPCManager();

    public void init() {
        if (!Config.DRPC) return;
        discordRPCManager.start();
    }

    public void shutdown() {
        discordRPCManager.stop();
    }

    private long timer = 0;
    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        if (!(event.phase == TickEvent.Phase.START) && !discordRPCManager.isActive()) return;
        if (System.currentTimeMillis() - timer < 2000) return;
        timer = System.currentTimeMillis();


        if (Cache.isInDungeon) {
            discordRPCManager.setDetailsLine("Playing Dungeons");
            discordRPCManager.setStateLine("Cleared: " + Cache.dungeonPercentage + " %");
        } else if ((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
            if (Cache.inSkyblock) {
                discordRPCManager.setDetailsLine("Playing on Hypixel Skyblock");
                discordRPCManager.setStateLine("Holding: " + Cache.itemheld);
            } else if (Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
                discordRPCManager.setDetailsLine("Playing on Hypixel");
                discordRPCManager.setStateLine("In game");
            } else {
                discordRPCManager.setDetailsLine("Playing on " + Minecraft.getMinecraft().getCurrentServerData().serverIP);
                discordRPCManager.setStateLine("In game");
            }
        } else if (Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
            discordRPCManager.setDetailsLine("Playing Singleplayer");
            discordRPCManager.setStateLine("In game");
        } else {
            discordRPCManager.setDetailsLine("In main menu");
            discordRPCManager.setStateLine("Idle");
        }
    }
}
