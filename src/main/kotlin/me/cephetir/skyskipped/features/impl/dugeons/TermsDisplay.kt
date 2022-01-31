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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class TermsDisplay : Feature() {
    val players = mutableMapOf<String, String>()
    private val regex = Regex("Party > (?:\\[.+] )?(?<username>\\w+): (?<message>.+)")
    private val terminalPhrase = listOf("ss", "s1", "1", "2b", "2t", "3", "4", "s3", "s4", "s2", "2")

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.inSkyblock) return

        val text = event.message.formattedText.stripColor()
        val m = regex.find(text) ?: return
        val message = m.groups["message"]?.value ?: return

        var term = ""
        for (s in terminalPhrase)
            if (message == s || message.startsWith("$s ") || message.endsWith(" $s") || message.contains(" $s "))
                term += "$s "

        if (term.isNotEmpty()) players[m.groups["username"]?.value ?: return] = term
    }
}