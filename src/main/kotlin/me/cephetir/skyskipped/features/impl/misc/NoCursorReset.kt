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

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object NoCursorReset : Feature() {
    var last = -1L
    var guiOpen = false

    init {
        listener<GuiOpenEvent> {
            val oldGuiScreen = mc.currentScreen
            if (it.gui is GuiChest && (oldGuiScreen is GuiContainer || oldGuiScreen == null)) {
                last = System.currentTimeMillis()
            }
        }

        listener<ClientTickEvent> {
            guiOpen = mc.currentScreen != null
        }
    }
}