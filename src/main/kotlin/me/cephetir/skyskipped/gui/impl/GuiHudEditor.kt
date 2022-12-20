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