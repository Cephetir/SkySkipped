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
import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ChatSwapper extends Feature {

    public ChatSwapper() {
        super("Party chat swapper", "Chat", "Automatically swaps between party chat and global chat.");
    }

    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if (!Config.chatSwapper || !EssentialAPI.getMinecraftUtil().isHypixel()) return;
        if (event.message.getUnformattedText().startsWith("You have been kicked from the party") || event.message.getUnformattedText().contains("has disbanded") || event.message.getUnformattedText().startsWith("You left the party") || event.message.getUnformattedText().contains("has been disbanded")) {
            if (Cache.inParty) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat all");
                Cache.inParty = false;
            }
        } else if (event.message.getUnformattedText().startsWith("You have joined") || event.message.getUnformattedText().startsWith("Party M") || event.message.getUnformattedText().contains("joined the ")) {
            if (!Cache.inParty) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat p");
                Cache.inParty = true;
            }
        }
    }
}