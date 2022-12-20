/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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