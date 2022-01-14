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
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent

class ChatSwapper : Feature() {
    private val partyLeave = Regex("^(?:You have been kicked from the party by (?:\\[.+] )?\\w{1,16}|(?:\\[.+] )?\\w{1,16} has disbanded the party!|You left the party(?:\\[.+] )?\\w{0,16}|(?:\\[.+] )?(?:.*)The party was disbanded(?:.*))$")
    private val partyJoin = Regex("^(?:You have joined (?:\\[.+] )?(?:.*)|Party Members(?:\\[.+] )?\\w{1,100}|(?:\\[.+] )?\\w{1,100} joined the(?:.*))$")

    override fun onChat(event: ClientChatReceivedEvent) {
        if (!EssentialAPI.getMinecraftUtil().isHypixel()) return
        if (partyLeave.matches(event.message.unformattedText) && Cache.inParty) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat all")
            Cache.inParty = false
        } else if (partyJoin.matches(event.message.unformattedText) && !Cache.inParty) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat p")
            Cache.inParty = true
        }
    }

    override fun isEnabled(): Boolean {
        return Config.chatSwapper
    }
}