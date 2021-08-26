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

package cephetir.skyskipped.Features.impl.discordrpc;

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
            //updatePresence();
        }
    }

    public void setDetailsLine(String status) {
        this.detailsLine = status;
        if (isActive()) {
            //updatePresence();
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