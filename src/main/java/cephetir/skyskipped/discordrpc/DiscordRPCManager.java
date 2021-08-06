package cephetir.skyskipped.discordrpc;

import cephetir.skyskipped.SkySkipped;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordRPCManager implements IPCListener {
    private static final long APPLICATION_ID = 867366183057752094L;
    private static final long UPDATE_PERIOD = 4200L;

    private IPCClient client;
    private String detailsLine;
    private String stateLine;
    private OffsetDateTime startTimestamp;

    private Timer updateTimer;
    private boolean connected;

    public void start() {
        try {
            System.out.println("Starting Discord RP...");
            if (isActive()) {
                return;
            }

            stateLine = "Starting...";
            detailsLine = "";
            startTimestamp = OffsetDateTime.now();
            client = new IPCClient(APPLICATION_ID);
            client.setListener(this);
            try {
                client.connect();
            } catch (Exception e) {
                System.out.println("Failed to connect to Discord RPC: " + e.getMessage());
            }
        } catch (Throwable ex) {
            System.out.println("DiscordRP has thrown an unexpected error while trying to start...");
            ex.printStackTrace();
        }

//        new Thread(() -> {
//            System.out.println("THREAD STARTED");
//            while(!Thread.currentThread().isInterrupted()) {
//                //if(Config.DRPC) {
//                    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
//                    if(Cache.isInDungeon) {
//                        //Client.getINSTANCE().getDiscordRP().update("Playing Dungeons", "Cleared: " + Config.dungeonPercentage);
//                        setDetailsLine("Playing Dungeons");
//                        setStateLine("Cleared: " + Cache.dungeonPercentage +" %");
//                    } else if((!Minecraft.getMinecraft().isSingleplayer()) && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null) {
//                        //Client.getINSTANCE().getDiscordRP().update("Playing Multiplayer"/*+Minecraft.getMinecraft().getCurrentServerData().serverIP*/, "In game");
//                        if(Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net")) {
//                            setDetailsLine("Playing on Hypixel");
//                            setStateLine("In game");
//                        } else {
//                            setDetailsLine("Playing on "+Minecraft.getMinecraft().getCurrentServerData().serverIP);
//                            setStateLine("In game");
//                        }
//                    } else if(Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null) {
//                        //Client.getINSTANCE().getDiscordRP().update("Playing Singleplayer", "In game");
//                        setDetailsLine("Playing Singleplayer");
//                        setStateLine("In game");
//                    } else {
//                        //Client.getINSTANCE().getDiscordRP().update("In main menu", "Idle");
//                        setDetailsLine("In main menu");
//                        setStateLine("Idle");
//                    }
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                //}
//            }
//        }).start();
    }

    public void stop() {
        if (isActive()) {
            client.close();
            connected = false;
        }
    }

    public boolean isActive() {
        return client != null && connected;
    }

    public void updatePresence() {
        RichPresence presence = new RichPresence.Builder()
                .setState(stateLine)
                .setDetails(detailsLine)
                .setStartTimestamp(startTimestamp)
                .setLargeImage("large", "SkySkipped v"+ SkySkipped.VERSION)
                .build();
        client.sendRichPresence(presence);
    }

    public void setStateLine(String status) {
        this.stateLine = status;
        if (isActive()) {
            updatePresence();
        }
    }

    public void setDetailsLine(String status) {
        this.detailsLine = status;
        if (isActive()) {
            updatePresence();
        }
    }

    @Override
    public void onReady(IPCClient client) {
        System.out.println("Discord RPC started");
        connected = true;
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePresence();
            }
        }, 0, UPDATE_PERIOD);
    }

    @Override
    public void onClose(IPCClient client, JSONObject json) {
        System.out.println("Discord RPC closed");
        this.client = null;
        connected = false;
        cancelTimer();
    }

    @Override
    public void onDisconnect(IPCClient client, Throwable t) {
        System.out.println("Discord RPC disconnected");
        this.client = null;
        connected = false;
        cancelTimer();
    }

    private void cancelTimer() {
        if(updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }
}