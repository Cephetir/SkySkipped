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