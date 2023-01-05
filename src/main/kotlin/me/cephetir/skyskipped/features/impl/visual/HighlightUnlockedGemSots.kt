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

package me.cephetir.skyskipped.features.impl.visual

import gg.essential.elementa.utils.withAlpha
import me.cephetir.bladecore.utils.threading.safeListener
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

    init {
        safeListener<DrawSlotEvent.Post> {
            val stack = it.slot.stack ?: return@safeListener
            if (items.contains(stack.getUUID() ?: return@safeListener)) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(0f, 0f, 299f)
                it.slot.highlight()
                GlStateManager.popMatrix()
            }
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

    private fun ItemStack.getUUID(): String? = (if (!this.hasTagCompound()) null else this.getSubCompound("ExtraAttributes", false))?.getString("uuid")?.ifEmpty { null }
}