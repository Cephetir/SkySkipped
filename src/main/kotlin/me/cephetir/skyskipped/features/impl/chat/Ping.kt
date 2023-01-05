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