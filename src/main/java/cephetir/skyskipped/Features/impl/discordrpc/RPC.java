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

public class RPC {

    private static RPC INSTANCE = null;

    // Make this a Singleton
    public static RPC getINSTANCE() {
        if (RPC.INSTANCE == null) {
            RPC.INSTANCE = new RPC();
        }

        return RPC.INSTANCE;
    }

    private RPC() {}

    @Getter
    private final DiscordRPCManager discordRPCManager = new DiscordRPCManager();

    public void init() {
        // if (!Config.DRPC) return; TODO Config is not working?
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

        String details;
        String state;
        if (Cache.isInDungeon) {
            details = "Playing Dungeons";
            state = "Cleared: " + Cache.dungeonPercentage + " %";
        } else if ((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
            if (Cache.inSkyblock) {
                details = "Playing on Hypixel Skyblock";
                state = "Holding: " + Cache.itemheld;
            } else if (Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
                details = "Playing on Hypixel";
                state = "In game";
            } else {
                details = "Playing on " + Minecraft.getMinecraft().getCurrentServerData().serverIP;
                state = "In game";
            }
        } else if (Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
            details = "Playing Singleplayer";
            state = "In game";
        } else {
            details = "In main menu";
            state = "Idle";
        }

        if (!discordRPCManager.detailsLine.equals(details)) {
            discordRPCManager.setDetailsLine(details);
        }
        if (!discordRPCManager.stateLine.equals(state)) {
            discordRPCManager.setStateLine(state);
        }
    }
}
