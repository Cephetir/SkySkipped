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

package me.cephetir.skyskipped.event.events

import me.cephetir.bladecore.core.event.Event
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Slot

abstract class DrawSlotEvent(open val gui: GuiContainer, open val slot: Slot) : Event {
    data class Pre(override val gui: GuiContainer, override val slot: Slot) : DrawSlotEvent(gui, slot)

    data class Post(override val gui: GuiContainer, override val slot: Slot) : DrawSlotEvent(gui, slot)
}