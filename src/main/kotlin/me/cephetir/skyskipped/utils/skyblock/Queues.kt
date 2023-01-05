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

package me.cephetir.skyskipped.utils.skyblock

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.events.RunGameLoopEvent
import me.cephetir.bladecore.core.event.events.SendChatMessageEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.skyskipped.utils.mc
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.event.world.WorldEvent
import java.util.*

object Queues {
    private var worldLoadTime = -1L

    private val commandQueue = LinkedList<String>()
    private var lastCommandTime = -1L

    private val messageQueue = LinkedList<String>()
    private var lastMessageTime = -1L

    fun sendMessage(message: String) {
        messageQueue.add(message)
    }

    fun sendCommand(command: String) {
        commandQueue.add(command)
    }

    init {
        listener<WorldEvent.Load> {
            worldLoadTime = System.currentTimeMillis()
        }

        listener<RunGameLoopEvent.Start> {
            if (mc.theWorld == null || mc.thePlayer == null)
                return@listener commandQueue.clear()

            val currentTime = System.currentTimeMillis()
            if (currentTime - worldLoadTime < 1300L || currentTime - lastCommandTime < 250L)
                return@listener

            mc.thePlayer.sendChatMessage(commandQueue.poll() ?: return@listener)
            lastCommandTime = System.currentTimeMillis()
        }

        listener<RunGameLoopEvent.Start> {
            if (mc.theWorld == null || mc.thePlayer == null)
                return@listener messageQueue.clear()

            val currentTime = System.currentTimeMillis()
            if (currentTime - worldLoadTime < 1300L || currentTime - lastMessageTime < 500L)
                return@listener

            mc.thePlayer.sendChatMessage(messageQueue.poll() ?: return@listener)
            lastMessageTime = System.currentTimeMillis()
        }

        listener<PacketEvent.Send> {
            if (it.packet !is C01PacketChatMessage) return@listener
            if ((it.packet as C01PacketChatMessage).message.startsWith("/")) lastCommandTime = System.currentTimeMillis()
            else lastMessageTime = System.currentTimeMillis()
        }

        listener<SendChatMessageEvent> {
            if (it.message.startsWith("/")) lastCommandTime = System.currentTimeMillis()
            else lastMessageTime = System.currentTimeMillis()
        }

        BladeEventBus.subscribe(this)
    }
}