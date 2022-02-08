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

package me.cephetir.skyskipped.features.impl.visual

import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.containsAny
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard


class PerspectiveToggle : Feature() {
    @SubscribeEvent
    fun onKey(event: InputEvent.KeyInputEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (!Config.betterPerspective) return
        if (!Keyboard.getEventKeyState()) return
        if (Keyboard.getEventKey() == SkySkipped.perspectiveToggle.keyCode)
            if (mc.thePlayer.heldItem.displayName.containsAny(Config.betterPerspectiveItems.split(", ")) || Config.betterPerspectiveItems == "")
                mc.gameSettings.thirdPersonView = if (mc.gameSettings.thirdPersonView == 3) 0 else 3
    }
}