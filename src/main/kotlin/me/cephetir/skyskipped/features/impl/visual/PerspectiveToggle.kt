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

package me.cephetir.skyskipped.features.impl.visual

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.minecraft.KeybindUtils.isDown
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class PerspectiveToggle : Feature() {
    private var keybindLastState = false

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null || !Config.betterPerspective) return

        val down = SkySkipped.perspectiveToggle.isDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down && Config.betterPerspectiveMode == 1) return

        if (Config.betterPerspectiveItems == "" || mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.displayName.containsAny(Config.betterPerspectiveItems.split(", ")))
            mc.gameSettings.thirdPersonView = if (mc.gameSettings.thirdPersonView == 3) 0 else 3
    }
}