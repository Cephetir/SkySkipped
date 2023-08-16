/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.features.impl.macro

import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.BackgroundJob
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.macros.F11Macro
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import me.cephetir.skyskipped.features.impl.macro.macros.SugarCaneMacro
import net.minecraft.util.MouseHelper
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import org.lwjgl.input.Mouse
import java.io.File
import java.util.*
import kotlin.math.roundToLong


object MacroManager : Feature() {
    val macros = listOf(
        NetherwartMacro(),
        SugarCaneMacro(),
        F11Macro()
    )

    var current = macros[0]

    var startTime = 0L
    var stopTime = 0L
    private lateinit var scriptJob: BackgroundJob

    private var keybindLastState = false

    @SubscribeEvent
    fun onInput(event: ClientTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) return

        val down = Config.macroKeybind.isKeyDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        toggle()
    }

    fun toggle() {
        startTime = System.currentTimeMillis()
        stopTime = (Config.macroStopTime.value * 60 * 60 * 1000).roundToLong()
        if (current.enabled) {
            if (::scriptJob.isInitialized) {
                UChat.chat("§cSkySkipped §f:: §eCancelling script job...")
                BackgroundScope.cancel(scriptJob)
            }
            current.toggle()
            if (Config.macroUngrab.value) regrabMouse()
            return
        }
        if (!Cache.onIsland) return UChat.chat("§cSkySkipped §f:: §4You're not on private island!")
        startScript()
        current.toggle()
        if (Config.macroUngrab.value) ungrabMouse()
    }

    private var oldMouseHelper: MouseHelper? = null
    private var doesGameWantUngrab = false
    fun ungrabMouse() {
        if (!mc.inGameHasFocus) return
        if (oldMouseHelper == null) oldMouseHelper = mc.mouseHelper
        mc.gameSettings.pauseOnLostFocus = false
        doesGameWantUngrab = !Mouse.isGrabbed()
        oldMouseHelper!!.ungrabMouseCursor()
        mc.inGameHasFocus = true
        mc.mouseHelper = object : MouseHelper() {
            override fun mouseXYChange() {}
            override fun grabMouseCursor() {
                doesGameWantUngrab = false
            }
            override fun ungrabMouseCursor() {
                doesGameWantUngrab = true
            }
        }
    }

    fun regrabMouse() {
        if (oldMouseHelper == null) return
        mc.mouseHelper = oldMouseHelper
        if (!doesGameWantUngrab) mc.mouseHelper.grabMouseCursor()
        oldMouseHelper = null
    }

    var packetThrottleAmout = 0

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText.stripColor() == "Packet Throttle")
            packetThrottleAmout++
    }

    private fun startScript() {
        if (Config.macroScript.value.isBlank()) return
        val file = File(Config.macroScriptsFolder, Config.macroScript.value)
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