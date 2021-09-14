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

import cephetir.skyskipped.SkySkipped;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.json.JSONObject;

import java.time.OffsetDateTime;

@Getter
public class DiscordRPCManager implements IPCListener {
    private static final long APPLICATION_ID = 867366183057752094L;
    private static final long UPDATE_PERIOD = 4200L;

    private IPCClient client;
    private String detailsLine;
    private String stateLine;
    private OffsetDateTime startTimestamp;

    //private Timer updateTimer;
    private boolean connected;

    public void start() {
        try {
            if (isActive()) {
                return;
            }

            System.out.println("Starting Discord RP...");
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
//        if (isActive()) {
//            updatePresence();
//        }
    }

    public void setDetailsLine(String status) {
        this.detailsLine = status;
//        if (isActive()) {
//            updatePresence();
//        }
    }

    @Override
    public void onReady(IPCClient client) {
        System.out.println("Discord RPC started");
        connected = true;
//        updateTimer = new Timer();
//        updateTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updatePresence();
//            }
//        }, 0, UPDATE_PERIOD);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private int tickCounter;
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        tickCounter++;
        if (tickCounter % 200 == 0) {
            updatePresence();
            tickCounter = 0;
        }
    }

    @Override
    public void onClose(IPCClient client, JSONObject json) {
        System.out.println("Discord RPC closed");
        this.client = null;
        connected = false;
        //cancelTimer();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void onDisconnect(IPCClient client, Throwable t) {
        System.out.println("Discord RPC disconnected");
        this.client = null;
        connected = false;
        //cancelTimer();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

//    private void cancelTimer() {
//        if(updateTimer != null) {
//            updateTimer.cancel();
//            updateTimer = null;
//        }
//    }
}