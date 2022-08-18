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

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.KeybindUtils.isDown
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Slot
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.util.Timer

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
        if (event.phase != TickEvent.Phase.START || !Cache.inSkyblock || Listener.called) return
        if (mc.currentScreen !is GuiChest) return

        val down = SkySkipped.hotbarKey.isDown(true)
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        if (currentPreset == null)
            return UChat.chat("§cSkySkipped §f:: §4No hotbar preset selected!")

        Listener.startTimer = Timer()
        Listener.called = true
        MinecraftForge.EVENT_BUS.register(Listener())
    }

    private class Listener {
        companion object {
            var startTimer: Timer = Timer()
            var called = false
        }

        private var step = 0
        private var hotbarSlot = -1
        private lateinit var timer: Timer
        private var slot: Slot? = null

        @SubscribeEvent
        fun onRender(event: RenderWorldLastEvent) {
            Timer.tick()
            if (startTimer.time >= 10) return MinecraftForge.EVENT_BUS.unregister(this)
            if (currentPreset == null) return MinecraftForge.EVENT_BUS.unregister(this)

            when (step) {
                // Init
                0 -> {
                    hotbarSlot = 81
                    slot = null
                    timer = Timer()
                    step = 1
                }
                // Throw Items
                1 -> {
                    if (timer.time < 0.1f) return
                    if (mc.currentScreen == null || mc.currentScreen !is GuiChest) {
                        UChat.chat("§cSkySkipped §f:: §4Error! Chest was closed!")
                        return MinecraftForge.EVENT_BUS.unregister(this)
                    }
                    timer.reset()

                    val chest = mc.currentScreen as GuiChest
                    val container = chest.inventorySlots
                    mc.playerController.windowClick(container.windowId, hotbarSlot, 0, 1, mc.thePlayer)

                    hotbarSlot++
                    if (hotbarSlot >= 89) {
                        hotbarSlot = 81
                        step = 2
                    }
                }
                // Proccess
                2 -> {
                    if (timer.time < 0.1f) return
                    if (mc.currentScreen == null || mc.currentScreen !is GuiChest) {
                        UChat.chat("§cSkySkipped §f:: §4Error! Chest was closed!")
                        return MinecraftForge.EVENT_BUS.unregister(this)
                    }
                    if (hotbarSlot >= 89) {
                        step = 3
                        return
                    }

                    val chest = mc.currentScreen as GuiChest
                    val container = chest.inventorySlots
                    if (slot == null) {
                        val item = currentPreset!!.items[hotbarSlot - 81]
                        if (item == "EMPTY") {
                            hotbarSlot++
                            return
                        }
                        slot = container.inventorySlots.find { it.hasStack && it.stack.displayName == item }
                        if (slot == null) {
                            UChat.chat("§cSkySkipped §f:: §4Error! Cannot find $item!")
                            return MinecraftForge.EVENT_BUS.unregister(this)
                        }
                        mc.playerController.windowClick(container.windowId, slot!!.slotNumber, 0, 0, mc.thePlayer)
                    } else {
                        mc.playerController.windowClick(container.windowId, hotbarSlot, 0, 0, mc.thePlayer)

                        slot = null
                        hotbarSlot++
                    }
                    timer.reset()
                }
                // Reset
                3 -> {
                    timer.pause()
                    step = 0
                    called = false
                    MinecraftForge.EVENT_BUS.unregister(this)
                }
            }
        }
    }

    data class HotbarPreset(val name: String, val items: List<String>)
}