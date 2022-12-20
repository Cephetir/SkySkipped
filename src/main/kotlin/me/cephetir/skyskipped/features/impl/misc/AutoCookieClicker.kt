/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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