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

package cephetir.skyskipped.Features.impl;

import cephetir.skyskipped.Features.Feature;
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
        if (!Config.nons) return;
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