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

package me.cephetir.skyskipped.features.impl.macro

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

object MacroManager {
    var current: Macro? = null
    val macros = listOf<Macro>(
        NetherwartMacro()
    )

    fun <T : Macro> getMacro(macro: T): T? = macros.find { it.javaClass == macro } as T?
    fun getMacro(macro: String): Macro? = macros.find { it.name.equals(macro, true) }

    @SubscribeEvent
    fun onInput(event: InputEvent.KeyInputEvent) {
        if (Minecraft.getMinecraft().thePlayer == null ||
            Minecraft.getMinecraft().theWorld == null ||
            !Keyboard.getEventKeyState() ||
            Keyboard.getEventKey() != SkySkipped.macroKey.keyCode
        ) return
        if (current == null) return UChat.chat("§cSkySkipped §f:: §4Select macro in config!")
        if (current!!.enabled) return current!!.toggle()
        if (!Cache.onIsland) return UChat.chat("§cSkySkipped §f:: §4You're not on private island!")
        current!!.toggle()
    }
}