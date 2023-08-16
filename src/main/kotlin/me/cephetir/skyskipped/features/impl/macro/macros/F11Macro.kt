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

package me.cephetir.skyskipped.features.impl.macro.macros

import gg.essential.universal.UChat
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.impl.macro.Macro
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.features.impl.macro.failsafes.Failsafes
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.truncate

class F11Macro : Macro("F11Macro") {
    private val events = Events(this)
    private var state = State.NORMAL
    private var lastFps = 60
    private var lastDist = 8

    private class Events(private val macro: F11Macro) {
        init {
            safeListener<TickEvent.ClientTickEvent> { macro.onTick(it) }
            safeListener<WorldEvent.Load> { macro.toggle() }
        }
    }

    override fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    private fun onEnable() {
        if (Config.macroCpuSaver.value) {
            lastFps = mc.gameSettings.limitFramerate
            mc.gameSettings.limitFramerate = 30
            lastDist = mc.gameSettings.renderDistanceChunks
            mc.gameSettings.renderDistanceChunks = 2
        }

        reset()
        unpressKeys()
        BladeEventBus.subscribe(events)
        UChat.chat("§cSkySkipped §f:: §eF11 Macro §aEnabled§e!")
    }

    private fun onDisable() {
        if (Config.macroCpuSaver.value) {
            mc.gameSettings.limitFramerate = lastFps
            mc.gameSettings.renderDistanceChunks = lastDist
        }

        BladeEventBus.unsubscribe(events)
        unpressKeys()
        reset()
        UChat.chat("§cSkySkipped §f:: §eF11 Macro §cDisabled§e!")
    }

    private fun reset() {
        state = State.NORMAL
    }

    override fun info(): String = "Macro: F11 Macro, State: ${state.name}"

    override fun isBanwave(): String = "False"

    override fun banwaveCheckIn(): Long = 0L

    override fun stopAndOpenInv() {
        unpressKeys()
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
    }

    override fun closeInvAndReturn() {
        unpressKeys()
        mc.displayGuiScreen(null)
    }

    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.currentScreen != null) return unpressKeys()

        when (state) {
            State.NORMAL -> {
                if (failsafes()) return unpressKeys()
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, Config.f11LMB.value)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, Config.f11W.value)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, Config.f11S.value)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, Config.f11A.value)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, Config.f11D.value)
            }

            State.DESYNC -> Failsafes.desynced(true) { state = State.NORMAL }
            State.WARPED -> Failsafes.warpBack { state = State.NORMAL }
        }
    }

    private fun unpressKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
    }

    private fun failsafes(): Boolean {
        if (warpBackFailsafe()) return true

        if (Config.autoWarpGarden.value) {
            val coords = runCatching { Config.autoWarpGardenCoords.value.split(",").map { it.trim().toInt() } }.getOrNull()
            if (coords == null || coords.size < 3) {
                UChat.chat("§cSkySkipped §f:: §4Failed to parse warp garden coords!")
                MacroManager.toggle()
                return true
            }
            if (coords[0] == truncate(player!!.posX).toInt() && coords[1] == truncate(player!!.posY).toInt() && coords[2] == truncate(player!!.posZ).toInt())
                player!!.sendChatMessage("/warp garden")
        }

        val desync = desyncFailsafe()
        if (desync == 2) return true
        else if (desync == 1) return false

        return false
    }

    private fun warpBackFailsafe(): Boolean {
        if (Cache.onIsland || !Config.warpBackFailsafe.value) return false
        state = State.WARPED

        UChat.chat("§cSkySkipped §f:: §eDetected not on island! Warping back...")
        sendWebhook("Warp failsafe", "Detected not on island! Warping back...", false)
        return true
    }

    private fun desyncFailsafe(): Int {
        if (mc.currentScreen != null && mc.currentScreen !is GuiChat) return 0
        var desynced = 0
        if (!Config.f11Desync.value) return 0
        if (Failsafes.ticksWarpDesync >= 0) {
            Failsafes.ticksWarpDesync--
            return 0
        }

        val ticksTimeout = Config.f11DesyncTime.value * 20
        val stack = mc.thePlayer.heldItem
        if (stack == null ||
            !stack.hasTagCompound() ||
            !stack.tagCompound.hasKey("ExtraAttributes", 10)
        ) return 0
        var newCount = -1L
        val tag = stack.tagCompound
        if (tag.hasKey("ExtraAttributes", 10)) {
            val ea = tag.getCompoundTag("ExtraAttributes")
            if (ea.hasKey("mined_crops", 99))
                newCount = ea.getLong("mined_crops")
            else if (ea.hasKey("farmed_cultivating", 99))
                newCount = ea.getLong("farmed_cultivating")
        }
        if (newCount == -1L) return 0
        if (newCount > Failsafes.lastCount) {
            if (Failsafes.startCount == -1L) Failsafes.startCount = newCount
            Failsafes.lastCount = newCount
            Failsafes.ticksDesync = 0
        } else {
            Failsafes.ticksDesync++
            if (Failsafes.ticksDesync >= ticksTimeout / 3) desynced = 1
        }

        if (Failsafes.ticksDesync >= ticksTimeout) {
            if (Config.soundFailsafes.value) {
                UChat.chat("§cSkySkipped §f:: §eDesync failsafe triggered!")
                player!!.playSound("random.anvil_land", 3f, 1f)
                return 0
            }
            state = State.DESYNC
            Failsafes.desyncedSteps = Failsafes.DesyncedSteps.SETUP
            desynced = 2
        }
        return desynced
    }

    private enum class State {
        NORMAL,
        WARPED,
        DESYNC
    }
}