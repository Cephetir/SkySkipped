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

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.safeAsyncListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraftforge.client.event.ClientChatReceivedEvent

class AutoReply : Feature() {
    private val regex =
        Regex("(?:(?<channel>:P|G|Guild|Party|Co-op) > )?(?<rank>\\[\\d+] )?(?<prefix>\\[\\w\\w\\w\\+{0,2}] )?(?<username>\\w{3,16})(?: \\[\\w+])?: (?<message>.+)")

    init {
        safeAsyncListener<ClientChatReceivedEvent> {
            if (!Config.autoReply) return@safeAsyncListener

            val msg = it.message.unformattedText.stripColor()
            val m = regex.matchEntire(msg) ?: return@safeAsyncListener
            if (m.groupValues[4].contains(player.name.stripColor())) return@safeAsyncListener

            printdev("Got message $msg")
            val channel = m.groups[1]
            printdev("First group ${channel?.value ?: "null"}")
            val command = if (channel == null) "/ac" else when (channel.value) {
                "Guild", "G" -> "/gc"
                "Party", "P" -> "/pc"
                "Co-op" -> "/cc"
                else -> "/ac"
            }
            if (Config.autoReplyGuild && command != "/gc") return@safeAsyncListener
            printdev("Command: $command")
            val message = m.groupValues[5]
            if (message.containsAny(" wc", "wc ") || message.equals("wc", true))
                Queues.sendMessage("$command \"wc\" - ${m.groupValues[4]}")
            else if (message.containsAny(" gg", "gg ") || message.equals("gg", true))
                Queues.sendMessage("$command \"gg\" - ${m.groupValues[4]}")
            else if (message.containsAny(" good game", "good game ") || message.equals("good game", true))
                Queues.sendMessage("$command \"good game\" - ${m.groupValues[4]}")
            else if (message.containsAny(" wrong chat", "wrong chat ") || message.equals("wrong chat", true))
                Queues.sendMessage("$command \"wrong chat\" - ${m.groupValues[4]}")
        }
    }
}