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