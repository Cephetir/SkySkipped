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
