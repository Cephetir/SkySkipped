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

package me.cephetir.skyskipped.features.impl.visual

import gg.essential.elementa.utils.withAlpha
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.DrawSlotEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import qolskyblockmod.pizzaclient.util.ItemUtil
import java.awt.Color

class HighlightUnlockedGemSots : Feature() {
    private val items = mutableListOf<String>()

    private val regex = Regex("§8\\[§\\d.§8]")
    private val regex2 = Regex("§8\\[§8.§8]")

    @SubscribeEvent
    fun onDraw(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        if (!Config.highlightSlots || event.gui !is GuiChest) return
        val chest = ((event.gui as GuiChest).inventorySlots as ContainerChest).lowerChestInventory
        val chestName = chest.displayName.unformattedText
        if (!chestName.contains("Auctions Browser")) return

        items.clear()
        var found = false
        var stack: ItemStack? = null
        chest@ for (i in 0 until chest.sizeInventory - 1) {
            val item = chest.getStackInSlot(i) ?: break@chest
            item@ for (text in item.getTooltip(mc.thePlayer, false) ?: break@chest) {
                if (regex.find(text) != null) {
                    if (regex2.find(text) != null) {
                        found = true
                        stack = item
                        break@chest
                    }
                    break@item
                }
            }
        }

        if (found) items.add(stack!!.getUUID() ?: return)
    }

    @SubscribeEvent
    fun onSlotDraw(event: DrawSlotEvent.Post) {
        val stack = event.slot.stack ?: return
        if (items.contains(stack.getUUID() ?: return)) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(0f, 0f, 299f)
            event.slot.highlight()
            GlStateManager.popMatrix()
        }
    }

    private fun Slot.highlight() {
        Gui.drawRect(
            this.xDisplayPosition,
            this.yDisplayPosition,
            this.xDisplayPosition + 16,
            this.yDisplayPosition + 16,
            Color.BLACK.withAlpha(169).rgb
        )
    }

    private fun ItemStack.getUUID(): String? = ItemUtil.getExtraAttributes(this)?.getString("uuid")?.ifEmpty { null }
}