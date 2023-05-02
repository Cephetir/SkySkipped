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

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.asyncListener
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.Timer
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Slot
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object HotbarSaver : Feature() {
    private var keybindLastState = false
    val presets = mutableListOf<HotbarPreset>()
    private var currentPreset: HotbarPreset? = null

    fun savePreset(name: String) {
        val items = mutableListOf<String>()
        val inv = mc.thePlayer.inventoryContainer
        for (slot in inv.inventorySlots.subList(36, 44))
            items.add(slot.stack?.displayName ?: "EMPTY")
        val preset = HotbarPreset(name, items)
        presets.add(preset)
        if (currentPreset == null) currentPreset = preset
        UChat.chat("§cSkySkipped §f:: §eSuccessfully saved preset \"$name\"!")
    }

    fun selectPreset(name: String) {
        currentPreset = presets.find { it.name.equals(name, true) }
        if (currentPreset != null) UChat.chat("§cSkySkipped §f:: §eSuccessfully selected preset \"${currentPreset!!.name}\"!")
        else UChat.chat("§cSkySkipped §f:: §4Unknown preset \"$name\"!")
    }

    fun removePreset(name: String) {
        val preset = presets.find { it.name.equals(name, true) } ?: return UChat.chat("§cSkySkipped §f:: §4Unknown preset \"$name\"!")
        presets.remove(preset)
        if (currentPreset == preset) currentPreset = null
        UChat.chat("§cSkySkipped §f:: §eSuccessfully removed preset \"$name\"!")
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !Cache.onSkyblock || Listener.called) return
        if (mc.currentScreen !is GuiChest && mc.currentScreen !is GuiInventory) return

        val down = Config.hotbarSwapKey.isKeyDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        if (currentPreset == null)
            return UChat.chat("§cSkySkipped §f:: §4No hotbar preset selected!")

        UChat.chat("§cSkySkipped §f:: §eStarted swapping hotbar...")
        Listener.called = true
        Listener.startTimer.reset()
        BladeEventBus.subscribe(Listener())
    }

    private class Listener {
        companion object {
            val startTimer = Timer()
            var called = false
        }

        private var count = 81
        private var step = 0
        private var hotbarSlot = -1
        private val timer = Timer()
        private var slot: Slot? = null

        init {
            asyncListener<RenderWorldLastEvent> {
                Timer.update()
                if (mc.currentScreen == null || (mc.currentScreen !is GuiChest && mc.currentScreen !is GuiInventory)) {
                    UChat.chat("§cSkySkipped §f:: §4Error! Chest was closed!")
                    called = false
                    return@asyncListener BladeEventBus.unsubscribe(this)
                }
                if (currentPreset == null) {
                    called = false
                    return@asyncListener BladeEventBus.unsubscribe(this)
                }
                if (startTimer.time >= 30_000) {
                    UChat.chat("§cSkySkipped §f:: §4Something went wrong while swapping hotbar!")
                    called = false
                    return@asyncListener BladeEventBus.unsubscribe(this)
                }

                when (step) {
                    // Init
                    0 -> {
                        count = if (mc.currentScreen is GuiChest) 81 else 36
                        hotbarSlot = count
                        slot = null
                        timer.reset()
                        timer.start()
                        startTimer.start()
                        step = 1
                    }
                    // Proccess
                    1 -> {
                        if (timer.time < Config.hotbarSwapDelay.value) return@asyncListener
                        if (hotbarSlot >= count + 8) {
                            step = 2
                            return@asyncListener
                        }
                        val item = currentPreset!!.items[hotbarSlot - count]
                        if (item == "EMPTY") {
                            hotbarSlot++
                            return@asyncListener
                        }
                        timer.reset()

                        val container = mc.thePlayer.openContainer
                        val current = container.inventorySlots[hotbarSlot]
                        if (current.hasStack && current.stack.displayName != item) {
                            mc.playerController.windowClick(container.windowId, hotbarSlot, 0, 1, mc.thePlayer)
                            return@asyncListener
                        }

                        if (slot == null) {
                            slot = container.inventorySlots.find { it.hasStack && it.stack.displayName == item }
                            if (slot == null) {
                                UChat.chat("§cSkySkipped §f:: §4Error! Cannot find $item!")
                                hotbarSlot++
                                return@asyncListener
                            }
                            mc.playerController.windowClick(container.windowId, slot!!.slotNumber, 0, 0, mc.thePlayer)
                        } else {
                            mc.playerController.windowClick(container.windowId, hotbarSlot, 0, 0, mc.thePlayer)

                            slot = null
                            hotbarSlot++
                        }
                    }
                    // Reset
                    2 -> {
                        step = 0
                        called = false
                        startTimer.stop()
                        timer.stop()
                        BladeEventBus.unsubscribe(this)
                        UChat.chat("§cSkySkipped §f:: §eSuccessfully swapped hotbar!")
                    }
                }
            }
        }
    }

    data class HotbarPreset(val name: String, val items: List<String>)
}