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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PetMacro(private val index: Int) {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawScreen(event: DrawScreenEvent.Pre) {
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        PetsOverlay.getPet(index, chest)
        BladeEventBus.unsubscribe(this, true)
    }
}
