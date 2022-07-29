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
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import me.cephetir.skyskipped.features.impl.macro.macros.SugarCaneMacro
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import org.lwjgl.input.Keyboard
import kotlin.math.roundToLong

object MacroManager : Feature() {
    val current: Macro
        get() = macros[Config.macroType]

    val macros = listOf(
        NetherwartMacro(),
        SugarCaneMacro()
    )

    var startTime = 0L
    var stopTime = 0L

    @SubscribeEvent
    fun onInput(event: InputEvent.KeyInputEvent) {
        if (mc.thePlayer == null ||
            mc.theWorld == null ||
            !Keyboard.getEventKeyState() ||
            Keyboard.getEventKey() != SkySkipped.macroKey.keyCode
        ) return
        startTime = System.currentTimeMillis()
        stopTime = (Config.macroStopTime * 60 * 60 * 1000).roundToLong()
        if (current.enabled) return current.toggle()
        if (!Cache.onIsland) return UChat.chat("§cSkySkipped §f:: §4You're not on private island!")
        current.toggle()
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !current.enabled) return
        if (stopTime != 0L && startTime - stopTime <= 0) {
            current.toggle()
            Macro.sendWebhook("Macro Disabled", "Macro disabled due to scheduled toggle!", true)
            stopTime = 0L
        }
    }
}