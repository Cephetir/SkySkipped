package cephetir.simplemod.discordrpc;

import cephetir.simplemod.SimpleMod;
import cephetir.simplemod.config.Config;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class DiscordRP {

    private boolean running = true;
    private long created = 0;

    public void start() {
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser user) {
                System.out.println("Welcome "+user.username+"#"+user.discriminator+".");
                update("Starting...", "");
            }
        }).build();
        DiscordRPC.discordInitialize("867366183057752094", handlers, true);

        new Thread("Discord RPC CallBack"){
            @Override
            public void run() {
                while(running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
        new Thread(() -> {
            System.out.println("THREAD STARTED");
            while(!Thread.currentThread().isInterrupted()) {
                if(Config.DRPC) {
                    System.out.println("THREAD UPDATED");
                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    if(Config.isInDungeon) {
                        //Client.getINSTANCE().getDiscordRP().update("Playing Dungeons", "Cleared: " + Config.dungeonPercentage);
                    } else if((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
                        //Client.getINSTANCE().getDiscordRP().update("Playing Multiplayer"/*+Minecraft.getMinecraft().getCurrentServerData().serverIP*/, "Ingame");
                        //System.out.println("DRPC changed to multiplayer");
                    } else if(Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
                        //Client.getINSTANCE().getDiscordRP().update("Playing Singleplayer", "Ingame");
                    } else {
                        //Client.getINSTANCE().getDiscordRP().update("In main menu", "Idle");
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String firstLine, String secondLine) {
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(secondLine);
        builder.setBigImage("large", "SimpleMod v" + SimpleMod.VERSION);
        builder.setDetails(firstLine);
        builder.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(builder.build());
    }
}
