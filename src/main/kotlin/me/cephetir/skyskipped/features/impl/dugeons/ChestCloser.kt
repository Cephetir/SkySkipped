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

import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.server.S2DPacketOpenWindow

class ChestCloser : Feature() {
    init {
        listener<PacketEvent.Receive> {
            if (!Config.chestCloser || it.packet !is S2DPacketOpenWindow || !Cache.inDungeon || (it.packet as S2DPacketOpenWindow).windowTitle.unformattedText.stripColor() != "Chest") return@listener

            it.cancel()
            mc.netHandler.networkManager.sendPacket(C0DPacketCloseWindow((it.packet as S2DPacketOpenWindow).windowId))
        }
    }
}