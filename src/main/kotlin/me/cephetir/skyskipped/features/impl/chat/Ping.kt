/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
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

package me.cephetir.skyskipped.features.impl.chat

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.ClientChatReceivedEvent
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

class Ping : Feature() {
    var regex: Pattern =
        Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)$")

    override fun onChat(event: ClientChatReceivedEvent) {
        val text = event.message as ChatComponentText
        val unformattedText = text.unformattedText.stripColor()
        val matcher = regex.matcher(unformattedText)
        if (matcher.matches()) {
            val substringBefore =
                StringUtils.substringAfter(text.formattedText, matcher.group("senderUsername").substring(1))
            if (substringBefore.lowercase().contains(mc.thePlayer.name.lowercase()))
                mc.thePlayer.playSound("random.pop", 0.8f, 1f)
        }
    }

    override fun isEnabled(): Boolean {
        return Config.ping
    }
}