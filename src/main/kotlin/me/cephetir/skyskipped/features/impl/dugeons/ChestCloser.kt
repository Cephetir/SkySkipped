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