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

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.IMixinGuiContainer
import me.cephetir.skyskipped.utils.ItemRarity.Companion.byBaseColor
import me.cephetir.skyskipped.utils.RoundUtils.drawRoundedOutline
import me.cephetir.skyskipped.utils.RoundUtils.drawRoundedRect
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import org.lwjgl.input.Mouse
import java.awt.Color
import java.util.*
import java.util.regex.Pattern

class PetsOverlay : Feature() {
    private var petsOverlay: GuiPetsOverlay? = null
    var auto = -1

    override fun isEnabled(): Boolean {
        return Config.petsOverlay
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onGuiOpen(event: GuiOpenEvent) {
        petsOverlay = null
        if (!Config.petsOverlay) return
        if (event.gui !is GuiChest) return
        val chest = event.gui as GuiChest
        val container = chest.inventorySlots as ContainerChest
        val containerName = container.lowerChestInventory.displayName.unformattedText
        if (!containerName.endsWith("Pets")) return
        petsOverlay = GuiPetsOverlay(chest)
        Thread {
            try {
                Thread.sleep(50L)
                petsOverlay!!.setSize(chest)
                petsOverlay!!.getPets()
            } catch (ignored: Exception) {
            }
        }.start()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onDrawScreen(event: DrawScreenEvent.Pre) {
        if (!Config.petsOverlay) return
        if (petsOverlay == null) return
        event.isCanceled = true
        petsOverlay!!.setSize(event.gui as GuiChest)
        petsOverlay!!.onDrawScreen(event.mouseX, event.mouseY)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onActionPerformed(event: GuiScreenEvent.MouseInputEvent) {
        if (!Config.petsOverlay) return
        if (petsOverlay == null) return
        event.isCanceled = true
        val i: Int = Mouse.getEventX() * event.gui.width / this.mc.displayWidth
        val j: Int = event.gui.height - Mouse.getEventY() * event.gui.height / this.mc.displayHeight - 1
        petsOverlay!!.onMouseClicked(i.toDouble(), j.toDouble(), Mouse.getEventButton())
    }

    private var ticks = 0
    override fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (!Config.petsOverlay) return
        if (petsOverlay == null) return
        ticks++
        if (ticks == 10) {
            ticks = 0
            petsOverlay!!.getPets()
        }
    }

    inner class GuiPetsOverlay(private var chest: GuiChest) {
        private var pets: List<Pet> = ArrayList()
        private var width: Int
        private var height: Int
        private var rectWidth = 0
        private var rectWidth1 = 0
        private var rectHeight = 0
        private var rectHeight1 = 0
        private var bottom = 0
        private var j = 0
        private var autopet: Slot? = null
        private var close: Slot? = null
        private var convert: Slot? = null
        private var hide: Slot? = null
        private var nextPage: Slot? = null
        private var previousPage: Slot? = null
        private val PET_PATTERN = Pattern.compile("§7\\[Lvl \\d+] (?<color>§[0-9a-fk-or]).+")

        init {
            width = chest.width
            height = chest.height
        }

        fun getPets() {
            val pets1: MutableList<Pet> = ArrayList()
            var i = 0
            var j = 0
            var k = 0
            if (chest.inventorySlots.inventorySlots.isNotEmpty()) for (slot in chest.inventorySlots.inventorySlots) {
                if (slot.slotNumber > 53) break
                if (slot.hasStack) {
                    val compound = slot.stack.tagCompound.getCompoundTag("display")
                    val displayName = compound.getString("Name")
                    if (displayName.lowercase(Locale.getDefault()).contains("[lvl ")) {
                        if (rectWidth1 + 5 + k * 32 > width / 2 - rectWidth1) {
                            k = 0
                            j++
                        }
                        pets1.add(
                            Pet(
                                rectWidth1 + 5 + k * 32,
                                rectHeight1 + 5 + j * 32,
                                displayName,
                                slot,
                                i,
                                slot.stack
                            )
                        )
                        i++
                        k++
                        if (auto == i) {
                            val container = chest as IMixinGuiContainer
                            container.handleMouseClick(slot, slot.slotNumber, 0, 0)
                            auto = -1
                            mc.thePlayer.closeScreen()
                        }
                        for (text in slot.stack.getTooltip(mc.thePlayer, false)) {
                            if (text.contains("Click to summon")) {
                                pets1[i - 1].last = false
                                break
                            } else if (text.contains("Click to despawn")) {
                                pets1[i - 1].last = true
                                break
                            }
                        }
                        val m = PET_PATTERN.matcher(displayName)
                        if (m.matches()) pets1[i - 1].rarity =
                            byBaseColor(m.group("color"))?.color?.rgb ?: Color(255, 255, 255, 255).rgb
                    } else if (displayName.lowercase(Locale.getDefault()).contains("autopet")) autopet =
                        slot else if (displayName.lowercase(
                            Locale.getDefault()
                        ).contains("close")
                    ) close = slot else if (displayName.lowercase(Locale.getDefault())
                            .contains("convert pet")
                    ) convert = slot else if (displayName.lowercase(
                            Locale.getDefault()
                        ).contains("hide pets")
                    ) hide = slot else if (displayName.lowercase(
                            Locale.getDefault()
                        ).contains("next page")
                    ) nextPage = slot else if (displayName.lowercase(
                            Locale.getDefault()
                        ).contains("previous page")
                    ) previousPage = slot
                }
            }
            pets = pets1
            this.j = j
        }

        fun onDrawScreen(mouseX: Int, mouseY: Int) {
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            rectWidth = width / 5
            rectWidth1 = width / 2 / 5
            rectHeight = height / 5
            rectHeight1 = height / 2 / 5
            val d = height - height / 5
            val n = (rectHeight1 + 5 + (j + 1) * 32) * 2 + 20
            bottom = d.coerceAtLeast(n)
            Gui.drawRect(rectWidth - 20, rectHeight, width - rectWidth + 20, bottom, Color(0, 0, 0, 105).rgb)
            GlStateManager.scale(1.5f, 1.5f, 1.5f)
            mc.fontRendererObj.drawString("PETS", (rectWidth - 20 + 3) / 1.5f, (rectHeight - 15) / 1.5f, -1, false)
            GlStateManager.scale(2f / 1.5f, 2f / 1.5f, 2f / 1.5f)
            if (pets.isNotEmpty()) for (pet in pets) {
                renderItem(pet.itemStack, pet.x, pet.y)
                if (pet.last) {
                    drawRoundedRect(pet.x - 0.3f, pet.y - 0.3f, pet.x + 16.3f, pet.y + 16.3f, 6f, pet.rarity)
                    drawRoundedOutline(
                        pet.x - 1f,
                        pet.y - 1f,
                        pet.x + 17f,
                        pet.y + 17f,
                        6f,
                        3f,
                        Color(23, 217, 7, 255).rgb
                    )
                }
                drawRoundedOutline(pet.x - 0.5f, pet.y - 0.5f, pet.x + 16.5f, pet.y + 16.5f, 6f, 2.5f, pet.rarity)
                GlStateManager.scale(0.25f, 0.25f, 0.25f)
                mc.fontRendererObj.drawStringWithShadow(
                    pet.name,
                    (pet.x + 8) * 2f * 2f - mc.fontRendererObj.getStringWidth(pet.name) / 2f,
                    (pet.y + 18) * 2f * 2f,
                    pet.rarity
                )
                GlStateManager.scale(4f, 4f, 4f)
            }
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            GlStateManager.resetColor()
            val h = bottom
            if (nextPage != null) {
                Gui.drawRect(
                    width / 2 - 50 - 12 - 100 - 12 - 100,
                    h - 25,
                    width / 2 - 50 - 12 - 100 - 12,
                    h - 25 + 20,
                    Color(255, 255, 255, 150).rgb
                )
                mc.fontRendererObj.drawStringWithShadow(
                    "NEXT",
                    width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("NEXT") / 2f,
                    (h - 25 - 3).toFloat(),
                    -1
                )
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(nextPage!!.stack, (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
                if (previousPage != null) {
                    Gui.drawRect(
                        width / 2 - 50 - 12 - 100 - 12 - 100,
                        h - 25 - 30,
                        width / 2 - 50 - 12 - 100 - 12,
                        h - 25 - 30 + 20,
                        Color(255, 255, 255, 150).rgb
                    )
                    mc.fontRendererObj.drawStringWithShadow(
                        "PREVIOUS",
                        width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("PREVIOUS") / 2f,
                        (h - 25 - 30 - 3).toFloat(),
                        -1
                    )
                    GlStateManager.scale(2f, 2f, 2f)
                    renderItem(previousPage!!.stack, (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 30 - 6) / 2)
                    GlStateManager.scale(0.5f, 0.5f, 0.5f)
                }
            } else if (previousPage != null) {
                Gui.drawRect(
                    width / 2 - 50 - 12 - 100 - 12 - 100,
                    h - 25,
                    width / 2 - 50 - 12 - 100 - 12,
                    h - 25 + 20,
                    Color(255, 255, 255, 150).rgb
                )
                mc.fontRendererObj.drawStringWithShadow(
                    "PREVIOUS",
                    width / 2f - 50 - 12 - 100 - 12 - 50 - mc.fontRendererObj.getStringWidth("PREVIOUS") / 2f,
                    (h - 25 - 3).toFloat(),
                    -1
                )
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(previousPage!!.stack, (width / 2 - 50 - 12 - 100 - 12 - 66) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }
            if (autopet != null) {
                Gui.drawRect(
                    width / 2 - 50 - 12 - 100,
                    h - 25,
                    width / 2 - 50 - 12,
                    h - 25 + 20,
                    Color(255, 255, 255, 150).rgb
                )
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(autopet!!.stack, (width / 2 - 50 - 12 - 66) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }
            if (close != null) {
                Gui.drawRect(width / 2 - 50, h - 25, width / 2 + 50, h - 25 + 20, Color(255, 255, 255, 150).rgb)
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(close!!.stack, (width / 2 - 50 + 50 - 16) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }
            if (convert != null) {
                Gui.drawRect(
                    width / 2 + 50 + 12,
                    h - 25,
                    width / 2 + 50 + 12 + 100,
                    h - 25 + 20,
                    Color(255, 255, 255, 150).rgb
                )
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(convert!!.stack, (width / 2 + 50 + 12 + 50 - 16) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }
            if (hide != null) {
                Gui.drawRect(
                    width / 2 + 50 + 12 + 100 + 12,
                    h - 25,
                    width / 2 + 50 + 12 + 100 + 12 + 100,
                    h - 25 + 20,
                    Color(255, 255, 255, 150).rgb
                )
                GlStateManager.scale(2f, 2f, 2f)
                renderItem(hide!!.stack, (width / 2 + 50 + 12 + 100 + 12 + 50 - 16) / 2, (h - 25 - 6) / 2)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }
            GlStateManager.resetColor()
            onHover(mouseX, mouseY)
            GlStateManager.disableBlend()
            GlStateManager.enableTexture2D()
            GlStateManager.popMatrix()
        }

        fun onMouseClicked(mouseX: Double, mouseY: Double, button: Int) {
            var mouseX = mouseX
            var mouseY = mouseY
            if (button == -1 || !Mouse.isButtonDown(button)) return
            mouseX /= 2.0
            mouseY /= 2.0
            val container = chest as IMixinGuiContainer
            for (pet in pets) if (mouseX > pet.x && mouseY > pet.y && mouseX < pet.x + 16 && mouseY < pet.y + 16) {
                container.handleMouseClick(pet.slot, pet.slot.slotNumber, button, 0)
                mc.thePlayer.closeScreen()
                return
            }
            mouseX *= 2.0
            mouseY *= 2.0
            val h = height - rectHeight
            if (mouseX > width / 2f - 50 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 && mouseY < h - 25 + 20) container.handleMouseClick(
                autopet,
                autopet!!.slotNumber,
                button,
                0
            ) else if (mouseX > width / 2f - 50 && mouseY > h - 25 && mouseX < width / 2f + 50 && mouseY < h - 25 + 20) container.handleMouseClick(
                close,
                close!!.slotNumber,
                button,
                0
            ) else if (mouseX > width / 2f + 50 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 && mouseY < h - 25 + 20) container.handleMouseClick(
                convert,
                convert!!.slotNumber,
                button,
                0
            ) else if (mouseX > width / 2f + 50 + 12 + 100 + 12 && mouseY > h - 25 && mouseX < width / 2f + 50 + 12 + 100 + 12 + 100 && mouseY < h - 25 + 20) container.handleMouseClick(
                hide,
                hide!!.slotNumber,
                button,
                0
            ) else if (nextPage != null) {
                if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 + 20) container.handleMouseClick(
                    nextPage,
                    nextPage!!.slotNumber,
                    button,
                    0
                )
                if (previousPage != null) {
                    if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 - 30 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 - 30 + 20) container.handleMouseClick(
                        previousPage,
                        previousPage!!.slotNumber,
                        button,
                        0
                    )
                }
            } else if (previousPage != null) {
                if (mouseX > width / 2f - 50 - 12 - 100 - 12 - 100 && mouseY > h - 25 && mouseX < width / 2f - 50 - 12 - 100 - 12 && mouseY < h - 25 + 20) container.handleMouseClick(
                    previousPage,
                    previousPage!!.slotNumber,
                    button,
                    0
                )
            }
        }

        private fun onHover(mouseX: Int, mouseY: Int) {
            for (pet in pets) if (mouseX / 2 > pet.x && mouseY / 2 > pet.y && mouseX / 2 < pet.x + 16 && mouseY / 2 < pet.y + 16) {
                var max = 0
                val tooltip = pet.itemStack.getTooltip(mc.thePlayer, false)
                GlStateManager.translate(0f, 0f, 150f)
                for ((i, text) in tooltip.withIndex()) {
                    if (mc.fontRendererObj.getStringWidth(text) > max) max = mc.fontRendererObj.getStringWidth(text)
                    mc.fontRendererObj.drawStringWithShadow(text, (mouseX + 5 + 5).toFloat(),
                        (mouseY + 5 + i * 10 - 8).toFloat(), -1)
                }
                GlStateManager.translate(0f, 0f, -5f)
                Gui.drawRect(
                    mouseX + 5,
                    mouseY - 8,
                    mouseX + 5 + max + 10,
                    mouseY + tooltip.size * 10 + 10 - 8,
                    Color(0, 0, 0, 150).rgb
                )
                GlStateManager.translate(0f, 0f, -145f)
            }
        }

        private fun renderItem(itemStack: ItemStack?, x: Int, y: Int) {
            val itemRender: RenderItem = mc.renderItem
            RenderHelper.enableGUIStandardItemLighting()
            itemRender.zLevel = -145f //Negates the z-offset of the below method.
            itemRender.renderItemAndEffectIntoGUI(itemStack, x, y)
            itemRender.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, x, y, null)
            itemRender.zLevel = 0f
            RenderHelper.disableStandardItemLighting()
        }

        fun setSize(chest: GuiChest) {
            width = chest.width
            height = chest.height
            this.chest = chest
        }

        inner class Pet(
            var x: Int,
            var y: Int,
            var name: String,
            var slot: Slot,
            var id: Int,
            var itemStack: ItemStack,
            var rarity: Int = -1,
            var last: Boolean = false
        )
    }

    companion object {
        fun getPet(index: Int, chest: GuiChest): Slot? {
            var i = 0
            if (chest.inventorySlots.inventorySlots.isNotEmpty()) {
                for (slot in chest.inventorySlots.inventorySlots) {
                    if (slot.slotNumber > 53) break
                    if (slot.hasStack) {
                        val compound = slot.stack.tagCompound.getCompoundTag("display")
                        val displayName = compound.getString("Name")
                        if (displayName.lowercase(Locale.getDefault()).contains("[lvl ")) {
                            i++
                            if (i == index) return slot
                        }
                    }
                }
            }
            return null
        }
    }
}
