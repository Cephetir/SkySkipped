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

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.DrawSlotEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse

class AutoCookieClicker : Feature() {
    @SubscribeEvent
    fun onDraw(event: DrawSlotEvent.Pre) {
        if (event.slot.hasStack &&
            event.slot.stack.displayName.stripColor().endsWith(" Cookies") &&
            Config.cookieClicker
        )
            mc.playerController.windowClick(
                event.gui.inventorySlots.windowId,
                event.slot.slotNumber,
                Mouse.getButtonIndex("BUTTON2"),
                0,
                mc.thePlayer
            )
    }
}