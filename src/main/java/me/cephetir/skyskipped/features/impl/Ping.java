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

package me.cephetir.skyskipped.features.impl;

import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ping extends Feature {
    Pattern regex = Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)$");

    public Ping() {
        super("Name ping", "Chat", "Plays sound when someone says your name in chat.");
    }

    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if (!Config.ping) return;
        ChatComponentText text = (ChatComponentText) event.message;
        String unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(text.getUnformattedText());
        Matcher matcher = regex.matcher(unformattedText);
        if (matcher.matches()) {
            String substringBefore = StringUtils.substringAfter(text.getFormattedText(), matcher.group("senderUsername").substring(1));
            if (substringBefore.toLowerCase(Locale.ROOT).contains(mc.thePlayer.getName().toLowerCase(Locale.ROOT)))
                mc.thePlayer.playSound("random.pop", 0.8f, 1f);
        }
    }
}
