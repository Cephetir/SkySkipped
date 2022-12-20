/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.gui.impl.GuiItemSwap
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Slot
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ItemSwap : Feature() {
    private val called = mutableMapOf<Pair<Slot, Slot>, Int>()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || !Cache.onSkyblock) return
        if (mc.currentScreen !is GuiInventory) return

        for (keybind in SkySkipped.keybinds) {
            val down = keybind.isDown(true)
            if (down == keybind.lastState) continue
            keybind.lastState = down
            if (!down) continue

            val screen = (mc.currentScreen as GuiInventory).inventorySlots

            val names = keybind.message.split(":")
            if (names.size < 2) {
                UChat.chat("§cSkySkipped §f:: §4Invalid item name format!")
                continue
            }
            var first: Slot? = null
            var second: Slot? = null

            for (slot in screen.inventorySlots) {
                val item = slot.stack ?: continue
                if (item.displayName.contains(names[0], true)) first = slot
                else if (item.displayName.contains(names[1], true)) second = slot
            }

            if (first != null && second != null) called[Pair(first, second)] = 0
            else UChat.chat("§cSkySkipped §f:: §4Can't find first or second item to swap!")
        }
    }

    fun GuiItemSwap.Keybind.isDown(inGui: Boolean = false) =
        if (this.keyCode == Keyboard.KEY_NONE || (!inGui && (me.cephetir.skyskipped.utils.mc.currentScreen != null || me.cephetir.skyskipped.utils.mc.currentScreen is GuiChat))) false
        else Keyboard.isKeyDown(this.keyCode)

    private var timer = System.currentTimeMillis()

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        for ((slots, step) in called) {
            if (mc.currentScreen !is GuiInventory) return called.clear()
            if (System.currentTimeMillis() - timer < Config.swapDelay) return
            timer = System.currentTimeMillis()

            val first = slots.first
            val second = slots.second
            val screen = (mc.currentScreen as GuiInventory).inventorySlots

            when (step) {
                0 -> {
                    mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
                    called[slots] = 1
                }
                1 -> {
                    mc.playerController.windowClick(screen.windowId, second.slotNumber, 0, 0, mc.thePlayer)
                    called[slots] = 2
                }
                2 -> {
                    mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
                    called.remove(slots)
                }
            }
        }
    }
}