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

import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.core.listeners.SkyblockListener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class BanDetector : Feature() {
    private var lastOnlinePlayers = emptyList<String>()
    private val playersLeftList = HashMap<String, Long>()

    init {
        listener<ClientChatReceivedEvent> {
            if (!Config.banDetector.value || !SkyblockListener.onSkyblock) return@listener

            val msg = it.message.unformattedText.stripColor()
            if (msg.contains("A player has been removed from")) BackgroundScope.launch {
                delay(1000L)
                if (playersLeftList.isEmpty()) {
                    UChat.chat("§cSkySkipped §f:: §4Couldn't detect any banned players!")
                    return@launch
                }
                val players = playersLeftList.keys.joinToString(separator = "§c, §e")
                UChat.chat("§cSkySkipped §f:: §cPossiblely banned players: §e$players§c!")
            }
        }

        listener<ClientTickEvent> {
            if (!Config.banDetector.value || !SkyblockListener.onSkyblock || player == null) return@listener

            playersLeftList.values.removeIf { System.currentTimeMillis() - it > 2000L }

            val players = mc.netHandler.playerInfoMap.map { it.gameProfile.name }
            val iter = lastOnlinePlayers.iterator()
            while (iter.hasNext()) {
                val player = iter.next()
                if (!players.contains(player))
                    playersLeftList[player] = System.currentTimeMillis()
            }
            lastOnlinePlayers = players
        }

        listener<WorldEvent.Load> {
            lastOnlinePlayers = emptyList()
            playersLeftList.clear()
        }
    }
}