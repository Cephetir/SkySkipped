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
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ChatSwapper : Feature() {
    private var inParty = false

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!EssentialAPI.getMinecraftUtil().isHypixel() || !Config.chatSwapper.value) return
        val msg = event.message.unformattedText.stripColor()
        if ((msg.startsWith("You have been kicked from the party") || msg.contains("has disbanded") || msg.startsWith("You left the party") || msg.contains("was disbanded")) && inParty) {
            Queues.sendCommand("/chat all")
            inParty = false
        } else if (((msg.startsWith("You have joined") && msg.endsWith(" party!")) || msg.startsWith("Party Members") || msg.contains("joined the party") || msg.contains("joined the dungeon group") || msg.contains(
                "joined your party"
            )) && !inParty
        ) {
            Queues.sendCommand("/chat p")
            inParty = true
        }
    }
}