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