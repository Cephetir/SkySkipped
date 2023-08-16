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

import me.cephetir.skyskipped.features.impl.misc.Proxy
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard


class GuiProxyMenu(private val parent: GuiScreen) : GuiScreen() {
    private var txtProxyAddress: GuiTextField? = null
    private var btnProxyType: GuiButton? = null
    private var btnProxyEnabled: GuiButton? = null

    override fun initGui() {
        Proxy.loadProxy()
        Keyboard.enableRepeatEvents(true)
        txtProxyAddress = GuiTextField(3, fontRendererObj, width / 2 - 100, 60, 200, 20)
        txtProxyAddress!!.maxStringLength = 128
        txtProxyAddress!!.isFocused = true
        txtProxyAddress!!.text = Proxy.proxyAddress
        btnProxyType = GuiButton(1, width / 2 - 100, height / 4 + 96, "")
        btnProxyEnabled = GuiButton(2, width / 2 - 100, height / 4 + 120, "")
        updateButtons()
        buttonList.add(btnProxyType)
        buttonList.add(btnProxyEnabled)
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 144, "Done"))
    }

    private fun updateButtons() {
        btnProxyType!!.displayString = "Type: " + Proxy.proxyType.name
        btnProxyEnabled!!.displayString = if (Proxy.proxyEnabled) "§aEnabled" else "§cDisabled"
    }

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
        drawBackground(0)
        this.drawCenteredString(mc.fontRendererObj, "Proxy Manager", width / 2, 34, 0xFFFFFF)
        txtProxyAddress!!.drawTextBox()
        if (txtProxyAddress!!.text.isEmpty() && !txtProxyAddress!!.isFocused)
            this.drawCenteredString(mc.fontRendererObj, "Enter proxy address here...", width / 2 - 100, 60, 0xFFFFFF)
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)
    }

    override fun actionPerformed(p_actionPerformed_1_: GuiButton) {
        if (p_actionPerformed_1_.id == 0) {
            mc.displayGuiScreen(this.parent)
            return
        }
        if (p_actionPerformed_1_.id == 1)
            Proxy.proxyType = Proxy.ProxyType.values()[(Proxy.proxyType.ordinal + 1) % Proxy.ProxyType.values().size]
        else if (p_actionPerformed_1_.id == 2)
            Proxy.proxyEnabled = !Proxy.proxyEnabled
        updateButtons()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        Proxy.proxyAddress = txtProxyAddress!!.text
        Proxy.saveProxy()
    }

    override fun keyTyped(p_keyTyped_1_: Char, p_keyTyped_2_: Int) {
        if (p_keyTyped_2_ == 1) {
            mc.displayGuiScreen(this.parent)
        }
        txtProxyAddress!!.textboxKeyTyped(p_keyTyped_1_, p_keyTyped_2_)
        super.keyTyped(p_keyTyped_1_, p_keyTyped_2_)
    }

    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {
        txtProxyAddress!!.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
    }

    override fun updateScreen() {
        txtProxyAddress!!.updateCursorCounter()
        super.updateScreen()
    }
}

