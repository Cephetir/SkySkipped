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

package me.cephetir.skyskipped.features.impl.misc

import com.google.gson.JsonObject
import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinMinecraft
import me.cephetir.skyskipped.mixins.accessors.IMixinSession
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.Session
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object TokenAuth : Feature() {
    private val originalSession = mc.session

    class SessionGui(private val previousScreen: GuiScreen) : GuiScreen() {
        private var status = ""
        private var sessionField: GuiTextField? = null
        private var sr: ScaledResolution? = null

        override fun initGui() {
            Keyboard.enableRepeatEvents(true)
            sr = ScaledResolution(mc)
            sessionField = GuiTextField(1, mc.fontRendererObj, sr!!.scaledWidth / 2 - 100, sr!!.scaledHeight / 2, 200, 20)
            sessionField!!.maxStringLength = 32767
            sessionField!!.isFocused = true
            buttonList.add(GuiButton(998, sr!!.scaledWidth / 2 - 100, sr!!.scaledHeight / 2 + 30, 200, 20, "Login"))
            buttonList.add(GuiButton(999, sr!!.scaledWidth / 2 - 100, sr!!.scaledHeight / 2 + 60, 200, 20, "Restore"))
            super.initGui()
        }

        override fun onGuiClosed() {
            Keyboard.enableRepeatEvents(false)
            super.onGuiClosed()
        }

        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            drawDefaultBackground()
            val warning = "Click on your IGN to copy your token. §cWARNING: DO NOT SHARE IT WITH ANYONE!!!"
            mc.fontRendererObj.drawString(warning, sr!!.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(warning) / 2, sr!!.scaledHeight / 2 - 54, Color.WHITE.rgb)
            val info = String.format("User: §a%s §rUUID: §a%s", (mc as IMixinMinecraft).session.username, (mc as IMixinMinecraft).session.playerID)
            mc.fontRendererObj.drawString(info, sr!!.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(info) / 2, sr!!.scaledHeight / 2 - 42, Color.WHITE.rgb)
            mc.fontRendererObj.drawString(status, sr!!.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(status) / 2, sr!!.scaledHeight / 2 - 30, Color.WHITE.rgb)
            sessionField!!.drawTextBox()
            super.drawScreen(mouseX, mouseY, partialTicks)
        }

        override fun actionPerformed(button: GuiButton) {
            if (button.id == 998) {
                try {
                    val username: String
                    val uuid: String
                    val token: String
                    val session = sessionField!!.text
                    if (session.contains(":")) {
                        username = session.split(":")[0]
                        uuid = session.split(":")[1]
                        token = session.split(":")[2]
                    } else {
                        val json = SkySkipped.gson.fromJson(
                            HttpUtils.sendGet(
                                "https://api.minecraftservices.com/minecraft/profile/",
                                mapOf(
                                    "Content-type" to "application/json",
                                    "Authorization" to "Bearer ${sessionField!!.text}"
                                )
                            ), JsonObject::class.java
                        )

                        username = json.get("name").asString
                        uuid = json.get("id").asString
                        token = session
                    }

                    (mc as IMixinMinecraft).session = Session(username, uuid, token, "mojang")
                    mc.displayGuiScreen(previousScreen)
                } catch (e: Exception) {
                    status = "§cError: Couldn't set session (check mc logs)"
                    e.printStackTrace()
                }
            } else if (button.id == 999) {
                try {
                    (mc as IMixinMinecraft).session = originalSession
                    mc.displayGuiScreen(previousScreen)
                } catch (e: Exception) {
                    status = "§cError: Couldn't restore session (check mc logs)"
                    e.printStackTrace()
                }
            }

            super.actionPerformed(button)
        }

        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            val info = String.format("User: §a%s §rUUID: §a%s", (mc as IMixinMinecraft).session.username, (mc as IMixinMinecraft).session.playerID,)
            if (mouseX > sr!!.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(info) / 2 && mouseX < sr!!.scaledWidth / 2 + mc.fontRendererObj.getStringWidth(info) / 2 && mouseY > sr!!.scaledHeight / 2 - 42 && mouseY < sr!!.scaledHeight / 2 - 33) {
                val stringselection = StringSelection(((mc as IMixinMinecraft).session as IMixinSession).token)
                Toolkit.getDefaultToolkit().systemClipboard.setContents(stringselection, null)
            }
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }

        override fun keyTyped(typedChar: Char, keyCode: Int) {
            sessionField!!.textboxKeyTyped(typedChar, keyCode)
            if (Keyboard.KEY_ESCAPE == keyCode) mc.displayGuiScreen(previousScreen)
            else super.keyTyped(typedChar, keyCode)
        }
    }
}