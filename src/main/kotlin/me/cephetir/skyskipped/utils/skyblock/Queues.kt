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

package me.cephetir.skyskipped.utils.skyblock

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.events.SendChatMessageEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.skyskipped.utils.mc
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraftforge.client.event.RenderWorldLastEvent
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

        listener<RenderWorldLastEvent> {
            if (mc.theWorld == null || mc.thePlayer == null)
                return@listener commandQueue.clear()

            val currentTime = System.currentTimeMillis()
            if (currentTime - worldLoadTime < 1300L || currentTime - lastCommandTime < 250L)
                return@listener

            mc.thePlayer.sendChatMessage(commandQueue.poll() ?: return@listener)
            lastCommandTime = System.currentTimeMillis()
        }

        listener<RenderWorldLastEvent> {
            if (mc.theWorld == null || mc.thePlayer == null)
                return@listener messageQueue.clear()

            val currentTime = System.currentTimeMillis()
            if (currentTime - worldLoadTime < 1300L || currentTime - lastMessageTime < 250L)
                return@listener

            mc.thePlayer.sendChatMessage(messageQueue.poll() ?: return@listener)
            lastMessageTime = System.currentTimeMillis()
        }

        listener<PacketEvent.PostSend> {
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