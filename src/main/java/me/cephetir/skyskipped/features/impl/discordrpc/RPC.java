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

package me.cephetir.skyskipped.features.impl.discordrpc;

import gg.essential.api.EssentialAPI;
import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RPC {
    public static final DiscordRPCManager discordRPCManager = new DiscordRPCManager();

    public static void init() {
        RPC.discordRPCManager.start();
    }

    public static void shutdown() {
        RPC.discordRPCManager.stop();
    }

    public static void reset() {
        if (Config.DRPC && !RPC.discordRPCManager.isActive()) RPC.discordRPCManager.start();
        else if (!Config.DRPC && RPC.discordRPCManager.isActive()) RPC.discordRPCManager.stop();
    }

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        if (!(event.phase == TickEvent.Phase.START) && !RPC.discordRPCManager.isActive()) return;

        if (Cache.isInDungeon) {
            RPC.discordRPCManager.setDetailsLine("Playing " + Cache.dungeonName);
            RPC.discordRPCManager.setStateLine("Cleared: " + Cache.dungeonPercentage + " %");
        } else if ((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
            if (Cache.inSkyblock) {
                RPC.discordRPCManager.setDetailsLine("Playing on Hypixel Skyblock");
                RPC.discordRPCManager.setStateLine("Holding: " + Cache.itemheld);
            } else if (EssentialAPI.getMinecraftUtil().isHypixel()) {
                RPC.discordRPCManager.setDetailsLine("Playing on Hypixel");
                RPC.discordRPCManager.setStateLine("In game");
            } else {
                RPC.discordRPCManager.setDetailsLine("Playing on " + Minecraft.getMinecraft().getCurrentServerData().serverIP);
                RPC.discordRPCManager.setStateLine("In game");
            }
        } else if (Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
            RPC.discordRPCManager.setDetailsLine("Playing Singleplayer");
            RPC.discordRPCManager.setStateLine("In game");
        } else {
            RPC.discordRPCManager.setDetailsLine("In main menu");
            RPC.discordRPCManager.setStateLine("Idle");
        }
    }
}
