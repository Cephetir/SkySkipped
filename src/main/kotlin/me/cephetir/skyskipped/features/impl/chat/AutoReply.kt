/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
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
            if (!Config.autoReply.value) return@safeAsyncListener

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
            if (Config.autoReplyGuild.value && command != "/gc") return@safeAsyncListener
            printdev("Command: $command")
            val message = m.groupValues[5]
            if (message.containsAny(" wc", "wc ") || message.equals("wc", true))
                Queues.sendMessage("$command \"wc\" - ${m.groupValues[4]}")
            else if (message.contains(" gg", true) || message.equals("gg", true))
                Queues.sendMessage("$command \"gg\" - ${m.groupValues[4]}")
            else if (message.containsAny(" good game", "good game ") || message.equals("good game", true))
                Queues.sendMessage("$command \"good game\" - ${m.groupValues[4]}")
            else if (message.containsAny(" wrong chat", "wrong chat ") || message.equals("wrong chat", true))
                Queues.sendMessage("$command \"wrong chat\" - ${m.groupValues[4]}")
            else if (message.contains("ඞ"))
                Queues.sendMessage("$command \"ඞ\" - ${m.groupValues[4]}")
        }
    }
}