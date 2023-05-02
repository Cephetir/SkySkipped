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
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.minecraft.KeybindUtils.isDown
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.ClickSlotControllerEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.io.File
import java.util.*

object GuiRecorder : Feature() {
    private val dir = File(Config.modDir, "gui_records")
    private var recording = false
    private var playing = false

    private val sequence = LinkedList<Node>()
    private var lastClick = -1L

    private var loop = false
    private var currentIndex = 0
    private var done = false
    private var lastPlayClick = -1L

    fun record() {
        if (playing)
            return UChat.chat("§cSkySkipped §f:: §cAlready playing!")
        if (recording) {
            recording = false
            return UChat.chat("§cSkySkipped §f:: §eStopped recording!")
        }

        lastClick = -1L
        recording = true
        UChat.chat("§cSkySkipped §f:: §eStarted recording...")
    }

    fun loop(loop: Boolean?) {
        if (loop != null) {
            this.loop = loop
            UChat.chat("§cSkySkipped §f:: §eSet recording loop to $loop.")
        } else UChat.chat("§cSkySkipped §f:: §cInvalid argument!")
    }

    fun play() {
        if (recording)
            return UChat.chat("§cSkySkipped §f:: §cAlready recording!")
        if (playing) {
            playing = false
            return UChat.chat("§cSkySkipped §f:: §eStopped playing!")
        }
        if (sequence.isEmpty())
            return UChat.chat("§cSkySkipped §f:: §cNothing to play! Forgot to load sequence?")
        if (mc.currentScreen == null || mc.currentScreen !is GuiContainer)
            return UChat.chat("§cSkySkipped §f:: §cNo gui opened!")

        lastPlayClick = -1L
        done = false
        currentIndex = 0
        playing = true
        UChat.chat("§cSkySkipped §f:: §eStarted playing...")
    }

    fun save(fileName: String) {
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.txt")
        if (file.exists()) file.delete()
        file.createNewFile()

        runCatching {
            val sb = StringBuilder()
            sequence.forEach {
                sb.append("${it.slot}:${it.button}:${it.mode}:${it.delayToNext}\n")
            }
            file.writeText(sb.toString())
        }.onFailure {
            UChat.chat("§cSkySkipped §f:: §cFailed to save the file!")
        }.onSuccess {
            UChat.chat("§cSkySkipped §f:: §aSuccessfully saved sequence!")
        }
    }

    fun load(fileName: String) {
        val file = File(dir, "$fileName.txt")
        if (!dir.exists() || !file.exists())
            return UChat.chat("§cSkySkipped §f:: §cFile doesn't exist!")

        runCatching {
            sequence.clear()
            val lines = file.readLines()
            lines.forEach {
                val split = it.split(":")
                sequence.add(Node(split[0].toInt(), split[1].toInt(), split[2].toInt(), split[3].toLong()))
            }
        }.onFailure {
            UChat.chat("§cSkySkipped §f:: §cFailed to read the file!")
        }.onSuccess {
            UChat.chat("§cSkySkipped §f:: §aSuccessfully loaded sequence!")
        }
    }

    private var keybindLastState = false

    init {
        listener<ClientTickEvent> {
            if (player == null || world == null) return@listener

            val down = SkySkipped.playGuiRecorder.isDown(true)
            if (down == keybindLastState) return@listener
            keybindLastState = down
            if (!down) return@listener

            play()
        }

        listener<ClickSlotControllerEvent> {
            if (!recording) return@listener
            if (player == null || world == null) {
                recording = false
                return@listener
            }

            sequence.add(Node(it.slot, it.button, it.mode))
            val currentTime = System.currentTimeMillis()
            if (lastClick != -1L)
                sequence[sequence.size - 2].delayToNext = currentTime - lastClick

            lastClick = currentTime
        }

        listener<TickEvent.RenderTickEvent> {
            if (!playing) return@listener
            if (player == null || world == null) {
                playing = false
                return@listener
            }
            if (currentIndex >= sequence.size) {
                playing = false
                return@listener UChat.chat("§cSkySkipped §f:: §cInvalid sequence index! Restart record.")
            }

            val node = sequence[currentIndex]
            if (!done) {
                if (mc.currentScreen == null || mc.currentScreen !is GuiContainer) {
                    playing = false
                    return@listener UChat.chat("§cSkySkipped §f:: §cGui closed!")
                }

                val stack = (mc.currentScreen as GuiContainer).inventorySlots.slotClick(node.slot, node.button, node.mode, mc.thePlayer)
                mc.netHandler.addToSendQueue(C0EPacketClickWindow((mc.currentScreen as GuiContainer).inventorySlots.windowId, node.slot, node.button, node.mode, stack, 0))
                //mc.playerController.windowClick(player!!.openContainer.windowId, node.slot, node.button, node.mode, player)

                lastPlayClick = System.currentTimeMillis()
                done = true
            } else if (System.currentTimeMillis() - lastPlayClick > node.delayToNext) {
                done = false
                if (++currentIndex == sequence.size) {
                    if (loop) currentIndex = 0
                    else {
                        playing = false
                        UChat.chat("§cSkySkipped §f:: §aFinished playing!")
                    }
                }
            }
        }
    }

    private data class Node(val slot: Int, val button: Int, val mode: Int, var delayToNext: Long = 500L)
}