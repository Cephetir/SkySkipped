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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AntiEscrow : Feature() {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.onSkyblock || (!Config.antiEscrowAh.value && !Config.antiEscrowAhBin.value && !Config.antiEscrowBz.value)) return
        val msg = event.message.unformattedText.stripColor()

        if (
            (msg.contains("There was an error with the auction house! (AUCTION_EXPIRED_OR_NOT_FOUND)") || msg.contains("There was an error with the auction house! (INVALID_BID)"))
            && Config.antiEscrowAh.value
        ) Queues.sendCommand("/ah")
        else if (
            (msg.contains("Claiming BIN auction...") || msg.contains("Visit the Auction House to collect your item!"))
            && Config.antiEscrowAhBin.value
        ) Queues.sendCommand("/ah")
        else if (
            msg.contains("for Bazaar Instant Buy Submit!")
            && Config.antiEscrowBz.value
        ) Queues.sendCommand("/bz")
    }
}