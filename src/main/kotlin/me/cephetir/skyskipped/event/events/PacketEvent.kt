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

package me.cephetir.skyskipped.event.events

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
open class PacketEvent(val packet: Packet<*>) : Event() {
    var direction: Direction? = null

    class ReceiveEvent(packet: Packet<*>) : PacketEvent(packet) {
        init {
            direction = Direction.INBOUND
        }
    }

    class SendEvent(packet: Packet<*>) : PacketEvent(packet) {
        init {
            direction = Direction.OUTBOUND
        }
    }

    enum class Direction {
        INBOUND, OUTBOUND
    }
}