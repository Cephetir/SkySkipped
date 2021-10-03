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

package cephetir.skyskipped.Features.impl;

import cephetir.skyskipped.Features.Feature;
import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import gg.essential.universal.UChat;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Watcher extends Feature {
    public Watcher() {
        super("WatcherNotifier", "Dungeons", "Notifies you when watcher did smth.");
    }

    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if (!Cache.isInDungeon) return;
        if (event.message.getFormattedText().equals("§r§c[BOSS] The Watcher§r§f: You have proven yourself. You may pass.§r") && Config.rabHat) {
            UChat.chat("§cWatcher cleared. RABBIT HAT!");
            Minecraft.getMinecraft().thePlayer.playSound("random.orb", 0.9f, 1f);
        } else if (event.message.getUnformattedText().contains("§r§c[BOSS] The Watcher§r§f: That will be enough for now.§r") && Config.watcher) {
            UChat.chat("§cWatcher ready!");
        }
    }
}
