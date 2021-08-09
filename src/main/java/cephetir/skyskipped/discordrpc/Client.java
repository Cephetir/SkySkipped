/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cephetir.skyskipped.discordrpc;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Client {

    @Getter
    private static final Client INSTANCE = new Client();

    @Getter
    private final DiscordRPCManager discordRPCManager = new DiscordRPCManager();

    public void init() {
        if (Config.DRPC) {
            discordRPCManager.start();
        }
    }

    public void shutdown() {
        discordRPCManager.stop();
    }

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            if (Config.DRPC && (!discordRPCManager.isActive())) {
                discordRPCManager.start();
            } else if ((!Config.DRPC) && discordRPCManager.isActive()) {
                discordRPCManager.stop();
                return;
            }

            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if(Cache.isInDungeon) {
                discordRPCManager.setDetailsLine("Playing Dungeons");
                discordRPCManager.setStateLine("Cleared: " + Cache.dungeonPercentage +" %");
            } else if((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
                if(Cache.inSkyblock) {
                    discordRPCManager.setDetailsLine("Playing on Hypixel Skyblock");
                    discordRPCManager.setStateLine("Holding: " + Cache.itemheld);
                } else if(Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
                    discordRPCManager.setDetailsLine("Playing on Hypixel");
                    discordRPCManager.setStateLine("In game");
                } else {
                    discordRPCManager.setDetailsLine("Playing on "+Minecraft.getMinecraft().getCurrentServerData().serverIP);
                    discordRPCManager.setStateLine("In game");
                }
            } else if(Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
                discordRPCManager.setDetailsLine("Playing Singleplayer");
                discordRPCManager.setStateLine("In game");
            } else {
                discordRPCManager.setDetailsLine("In main menu");
                discordRPCManager.setStateLine("Idle");
            }
        }
    }
}
