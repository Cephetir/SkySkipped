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
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Taken from non-mod
 * https://github.com/Wyvest/non-mod
 *
 * @author Wyvest
 */
public class Nons extends Feature {
    Pattern regex = Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)$");

    public Nons() {
        super("[NON] Rank", "Chat", "Adds the [NON] rank, given to people without a rank.");
    }

    @SubscribeEvent
    public void doThings(ClientChatReceivedEvent event) {
        if (!Config.nons || !Cache.isHypixel) return;
        ChatComponentText text = (ChatComponentText) event.message;
        String unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(text.getUnformattedText());
        Matcher matcher = regex.matcher(unformattedText);
        if (matcher.matches()) {
            String substringBefore = StringUtils.substringBefore(text.getFormattedText(), matcher.group("senderUsername").substring(1));
            if (substringBefore.charAt(substringBefore.lastIndexOf("§") + 1) == '7') {
                Minecraft.getMinecraft().thePlayer.addChatMessage(addNonTag(text, matcher));
                event.setCanceled(true);
            } else if (substringBefore.charAt(substringBefore.lastIndexOf("§") + 1) == 'r') {
                if (substringBefore.charAt(substringBefore.lastIndexOf("§", substringBefore.lastIndexOf("§"))) == '7') {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(addNonTag(text, matcher));
                    event.setCanceled(true);
                }
            }
        }
    }

    private ChatComponentText addNonTag(ChatComponentText message, Matcher matcher) {
        String oldText = message.getUnformattedTextForChat();
        ChatComponentText newText = (ChatComponentText) new ChatComponentText(oldText).setChatStyle(message.getChatStyle());
        boolean found = false;
        for (IChatComponent sibling : message.getSiblings()) {
            if (sibling.getUnformattedText().contains(matcher.group("senderUsername")) && !found) {
                found = true;
                newText.appendSibling(new ChatComponentText(sibling.getUnformattedText().replaceFirst(Pattern.compile(matcher.group("senderUsername"), Pattern.LITERAL).toString(), "[NON] " + matcher.group("senderUsername"))).setChatStyle(sibling.getChatStyle()));
            } else {
                newText.appendSibling(new ChatComponentText(sibling.getUnformattedText()).setChatStyle(sibling.getChatStyle()));
            }
        }
        return newText;
    }
}
