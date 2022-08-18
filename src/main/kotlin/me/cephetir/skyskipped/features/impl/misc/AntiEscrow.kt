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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AntiEscrow : Feature() {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.inSkyblock || (!Config.antiEscrowAh && !Config.antiEscrowAhBin && !Config.antiEscrowBz)) return
        val msg = event.message.unformattedText.stripColor()

        if (
            (msg.contains("There was an error with the auction house! (AUCTION_EXPIRED_OR_NOT_FOUND)") || msg.contains("There was an error with the auction house! (INVALID_BID)"))
            && Config.antiEscrowAh
        ) mc.thePlayer.sendChatMessage("/ah")
        else if (
            (msg.contains("Claiming BIN auction...") || msg.contains("Visit the Auction House to collect your item!"))
            && Config.antiEscrowAhBin
        ) mc.thePlayer.sendChatMessage("/ah")
        else if (
            msg.contains("for Bazaar Instant Buy Submit!")
            && Config.antiEscrowBz
        ) mc.thePlayer.sendChatMessage("/bz")
    }
}