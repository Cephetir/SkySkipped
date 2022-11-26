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

package me.cephetir.skyskipped.features.impl.misc

import gg.essential.elementa.utils.withAlpha
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.DrawSlotEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatAllowedCharacters
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import java.awt.Color

class SearchContainer : Feature() {
    private var capturingText = false
    private var text = ""

    @SubscribeEvent
    fun onKeyPre(event: GuiScreenEvent.KeyboardInputEvent.Pre) {
        if (mc.currentScreen !is GuiContainer || !Config.containerSearch) return
        if (!Keyboard.getEventKeyState()) return
        if (Keyboard.isRepeatEvent()) return
        val key = Keyboard.getEventKey()

        if (key == Keyboard.KEY_F && GuiScreen.isCtrlKeyDown()) {
            capturingText = !capturingText
            text = ""
            return
        }
        if (!capturingText) return

        event.isCanceled = true
        val char = Keyboard.getEventCharacter()
        when (key) {
            Keyboard.KEY_RETURN, Keyboard.KEY_ESCAPE -> capturingText = false
            Keyboard.KEY_BACK -> text = text.dropLast(1)
            else -> if (ChatAllowedCharacters.isAllowedCharacter(char))
                text += if (GuiScreen.isShiftKeyDown()) char.uppercase() else char.lowercase()
        }
    }

    @SubscribeEvent
    fun onGuiRender(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (!Config.containerSearch || !capturingText) return
        val gui = event.gui
        GlStateManager.pushMatrix()
        GlStateManager.scale(1.5f, 1.5f, 1.5f)
        mc.fontRendererObj.drawString(
            text + "_",
            gui.width / 2f / 1.5f - mc.fontRendererObj.getStringWidth(text + "_") / 2f,
            ((gui.height - 166) / 2f - mc.fontRendererObj.FONT_HEIGHT - 5) / 1.5f,
            Color.RED.rgb,
            false
        )
        GlStateManager.scale(1f / 1.5f, 1f / 1.5f, 1f / 1.5f)
        GlStateManager.popMatrix()
    }

    @SubscribeEvent
    fun onGuiClose(event: GuiOpenEvent) {
        capturingText = false
        text = ""
    }

    init {
        safeListener<DrawSlotEvent.Pre> {
            if (!Config.containerSearch || !capturingText) return@safeListener
            val slot = it.slot
            if (slot.hasStack && slot.stack.displayName.contains(text, true)) Gui.drawRect(
                slot.xDisplayPosition,
                slot.yDisplayPosition,
                slot.xDisplayPosition + 16,
                slot.yDisplayPosition + 16,
                Color.WHITE.withAlpha(169).rgb
            )
        }

        safeListener<DrawSlotEvent.Post> {
            if (!Config.containerSearch || !capturingText) return@safeListener
            val slot = it.slot
            if (!slot.hasStack || !slot.stack.displayName.contains(text, true)) Gui.drawRect(
                slot.xDisplayPosition,
                slot.yDisplayPosition,
                slot.xDisplayPosition + 16,
                slot.yDisplayPosition + 16,
                Color.BLACK.withAlpha(169).rgb
            )
        }
    }
}