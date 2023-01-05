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

import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.DrawSlotEvent
import me.cephetir.skyskipped.features.Feature
import org.lwjgl.input.Mouse

class AutoCookieClicker : Feature() {
    init {
        safeListener<DrawSlotEvent.Pre> {
            if (it.slot.hasStack && it.slot.stack.displayName.stripColor().endsWith(" Cookies") && Config.cookieClicker)
                playerController.windowClick(
                    it.gui.inventorySlots.windowId,
                    it.slot.slotNumber,
                    Mouse.getButtonIndex("BUTTON2"),
                    0,
                    mc.thePlayer
                )
        }
    }
}