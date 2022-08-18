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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import me.cephetir.skyskipped.features.impl.macro.macros.SugarCaneMacro
import me.cephetir.skyskipped.utils.KeybindUtils.isDown
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.threading.BackgroundJob
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.io.File
import java.util.*
import kotlin.math.roundToLong

object MacroManager : Feature() {
    val macros = listOf(
        NetherwartMacro(),
        SugarCaneMacro()
    )

    var current = macros[Config.macroType]

    var startTime = 0L
    var stopTime = 0L
    private lateinit var scriptJob: BackgroundJob

    private var keybindLastState = false

    @SubscribeEvent
    fun onInput(event: ClientTickEvent) {
        if (!Cache.inSkyblock || mc.thePlayer == null || mc.theWorld == null) return

        val down = SkySkipped.macroKey.isDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        startTime = System.currentTimeMillis()
        stopTime = (Config.macroStopTime * 60 * 60 * 1000).roundToLong()
        if (current.enabled) {
            if (::scriptJob.isInitialized) {
                UChat.chat("§cSkySkipped §f:: §eCancelling script job...")
                BackgroundScope.cancel(scriptJob)
            }
            current.toggle()
            return
        }
        if (!Cache.onIsland) return UChat.chat("§cSkySkipped §f:: §4You're not on private island!")
        startScript()
        current.toggle()
    }

    var packetThrottleAmout = 0

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText.stripColor() == "Packet Throttle")
            packetThrottleAmout++
    }

    private fun startScript() {
        if (Config.macroScript.isBlank()) return
        val file = File(Config.macroScriptsFolder, Config.macroScript)
        if (!file.exists()) return UChat.chat("§cSkySkipped §f:: §4Can't find macro script file!")

        var loop: Boolean
        val actions = LinkedList<Action>()
        file.reader().use { reader ->
            val lines = reader.readLines().toMutableList()
            if (lines.isEmpty()) return throwEx("Action list is empty")

            loop = lines.removeAt(0) == ":loop"
            for ((i, text) in lines.withIndex()) {
                val line = text.split("//")[0]
                when {
                    line.startsWith("macro ") -> {
                        val act = line.removePrefix("macro ")
                        when {
                            act.startsWith("start") -> actions.add(Action(0))
                            act.startsWith("stop") -> actions.add(Action(1))
                            act.startsWith("restart") -> actions.add(Action(2))
                            else -> return throwEx("Unknown operation \"$line\" at line $i")
                        }
                    }

                    line.startsWith("sleep ") -> {
                        val delay = line.removePrefix("sleep ").toLongOrNull() ?: return throwEx("Unknown operation \"$line\" at line $i")
                        actions.add(Action(3, delay))
                    }

                    else -> return throwEx("Unknown operation \"$line\" at line $i")
                }
            }
        }

        if (actions.isEmpty())
            return throwEx("Action list is empty")

        val job = BackgroundJob("Macro Script", 0L) {
            for (action in actions) {
                if (!isActive) break
                when (action.type) {
                    0 -> if (!current.enabled) current.toggle()
                    1 -> if (current.enabled) current.toggle()
                    2 -> {
                        current.toggle()
                        current.toggle()
                    }

                    3 -> delay(action.value)
                }
            }
        }

        UChat.chat("§cSkySkipped §f:: §eStarting script...")
        scriptJob = if (loop) BackgroundScope.launchLooping(job)
        else BackgroundScope.launch(job)
    }

    private fun throwEx(reason: String) {
        UChat.chat("§cSkySkipped §f:: §4Failed to read macro script! Reason: $reason")
        SkySkipped.logger.warn("Failed to read macro script! Reason: $reason")
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

    data class Action(val type: Int, val value: Long = 0L)
}