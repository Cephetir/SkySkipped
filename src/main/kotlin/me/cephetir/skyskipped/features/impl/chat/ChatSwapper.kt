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