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
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Slot
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ArmorSwap : Feature() {
    var lastState = false

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if(event.phase != TickEvent.Phase.START || !Config.armorSwap || !Cache.inSkyblock) return
        if(mc.currentScreen !is GuiInventory) return

        val down = Keyboard.isKeyDown(SkySkipped.armorSwap.keyCode)
        if(down == lastState) return
        lastState = down
        if(!down) return

        val screen = (mc.currentScreen as GuiInventory).inventorySlots
        var first: Slot? = null
        var second: Slot? = null
        for(slot in screen.inventorySlots) {
            val item = slot.stack ?: continue
            if(item.displayName.contains(Config.armorFirst, true))
                first = slot
            else if(item.displayName.contains(Config.armorSecond, true))
                second = slot
        }

        if(first != null && second != null) {
            mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
            mc.playerController.windowClick(screen.windowId, second.slotNumber, 0, 0, mc.thePlayer)
            mc.playerController.windowClick(screen.windowId, first.slotNumber, 0, 0, mc.thePlayer)
        } else UChat.chat("§cSkySkipped §f:: §4Can't find first or second item to swap!")
    }
}