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

package me.cephetir.skyskipped.utils

import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

object KeybindUtils {
    fun KeyBinding.isDown(inGui: Boolean = false) = if (this.keyCode == Keyboard.KEY_NONE || (!inGui && (mc.currentScreen != null || mc.currentScreen is GuiChat))) false else Keyboard.isKeyDown(this.keyCode)
    fun GuiItemSwap.Keybind.isDown(inGui: Boolean = false) = if (this.keyCode == Keyboard.KEY_NONE || (!inGui && (mc.currentScreen != null || mc.currentScreen is GuiChat))) false else Keyboard.isKeyDown(this.keyCode)
}