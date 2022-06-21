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

package me.cephetir.skyskipped.gui.impl

import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen
import me.cephetir.skyskipped.features.impl.macro.FarmingHUD
import me.cephetir.skyskipped.utils.render.RenderUtils
import java.awt.Color

class GuiHudEditor : UScreen() {
    private var dragging = false

    private var lastMouseX = 0.0
    private var lastMouseY = 0.0

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        RenderUtils.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Color.BLACK.withAlpha(110).rgb)
        if (!dragging) {
            lastMouseX = mouseX.toDouble()
            lastMouseY = mouseY.toDouble()
        }

        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        if (mouseX >= FarmingHUD.x && mouseY >= FarmingHUD.y && mouseX <= FarmingHUD.x + FarmingHUD.width && mouseY <= FarmingHUD.y + FarmingHUD.height)
            dragging = true

        super.onMouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun onMouseReleased(mouseX: Double, mouseY: Double, state: Int) {
        dragging = false

        super.onMouseReleased(mouseX, mouseY, state)
    }

    override fun onMouseDragged(x: Double, y: Double, clickedButton: Int, timeSinceLastClick: Long) {
        if (dragging) {
            FarmingHUD.x += (x - lastMouseX).toFloat()
            FarmingHUD.y += (y - lastMouseY).toFloat()
        }
        lastMouseX = x
        lastMouseY = y

        super.onMouseDragged(x, y, clickedButton, timeSinceLastClick)
    }
}