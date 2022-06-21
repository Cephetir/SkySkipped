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

import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import me.cephetir.skyskipped.features.impl.macro.macros.SugarCaneMacro
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

object MacroManager {
    val current: Macro
        get() = macros[Config.macroType]

    val macros = listOf(
        NetherwartMacro(),
        SugarCaneMacro()
    )

    var startTime = 0L

    @SubscribeEvent
    fun onInput(event: InputEvent.KeyInputEvent) {
        if (Minecraft.getMinecraft().thePlayer == null ||
            Minecraft.getMinecraft().theWorld == null ||
            !Keyboard.getEventKeyState() ||
            Keyboard.getEventKey() != SkySkipped.macroKey.keyCode
        ) return
        startTime = System.currentTimeMillis()
        if (current.enabled) return current.toggle()
        //if (!Cache.onIsland) return UChat.chat("§cSkySkipped §f:: §4You're not on private island!")
        current.toggle()
    }
}