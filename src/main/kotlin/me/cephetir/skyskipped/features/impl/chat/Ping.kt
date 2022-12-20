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
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

class Ping : Feature() {
    var regex: Pattern =
        Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)$")

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if(!Config.ping || !EssentialAPI.getMinecraftUtil().isHypixel()) return
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
}