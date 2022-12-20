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

import gg.essential.api.EssentialAPI
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ChatSwapper : Feature() {
    private var inParty = false

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!EssentialAPI.getMinecraftUtil().isHypixel() || !Config.chatSwapper) return
        val msg = event.message.unformattedText.stripColor()
        if ((msg.startsWith("You have been kicked from the party") || msg.contains("has disbanded") || msg.startsWith("You left the party") || msg.contains("was disbanded")) && inParty) {
            Queues.sendCommand("/chat all")
            inParty = false
        } else if (((msg.startsWith("You have joined") && msg.endsWith(" party!")) || msg.startsWith("Party Members") || msg.contains("joined the party") || msg.contains("joined the dungeon group")) && !inParty) {
            Queues.sendCommand("/chat p")
            inParty = true
        }
    }
}