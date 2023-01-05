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

package me.cephetir.skyskipped.features.impl.chat

import gg.essential.universal.UMouse
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinGuiNewChat
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.util.stream.Collectors
import kotlin.math.floor
import kotlin.math.roundToInt


class ChatSearch : Feature() {
    companion object {
        var search = false
    }

    @SubscribeEvent
    fun onKeyPre(event: KeyboardInputEvent.Pre) {
        if (!Config.chatSearch || mc.currentScreen !is GuiChat) return

        if (!Keyboard.getEventKeyState()) return
        if (Keyboard.isRepeatEvent()) return
        val chat = mc.ingameGUI.chatGUI as IMixinGuiNewChat
        if (Keyboard.getEventKey() == Keyboard.KEY_F && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            event.isCanceled = true

            search = !search
            if (search)
                chat.invokeSetChatLine(
                    ChatComponentText("§e§lSEARCH MODE ON"),
                    "skyskippedsearchmode".hashCode(),
                    mc.ingameGUI.updateCounter,
                    true
                )
            else {
                mc.ingameGUI.chatGUI.deleteChatLine("skyskippedsearchmode".hashCode())
                mc.ingameGUI.chatGUI.refreshChat()
            }
        } else if (Keyboard.getEventKey() == 1 && search) {
            search = false
            mc.ingameGUI.chatGUI.deleteChatLine("skyskippedsearchmode".hashCode())
            mc.ingameGUI.chatGUI.refreshChat()
        }
    }

    @SubscribeEvent
    fun onKeyTypedPost(event: KeyboardInputEvent.Post) {
        if (event.gui !is GuiChat) return
        if (!Keyboard.getEventKeyState() && event.gui is GuiChat) return
        if (search) {
            mc.ingameGUI.chatGUI.refreshChat()
            val chat = (mc.ingameGUI.chatGUI as IMixinGuiNewChat)
            if (chat.drawnChatLines.size == 0) chat.invokeSetChatLine(
                ChatComponentText("§e§lSEARCH MODE ON"),
                "skyskippedsearchmode".hashCode(),
                mc.ingameGUI.updateCounter,
                true
            )
        }
    }

    @SubscribeEvent
    fun onMouseClicked(event: GuiScreenEvent.MouseInputEvent.Pre) {
        if (!search || !Config.chatSearch || Mouse.getEventButton() != 1 || !Mouse.getEventButtonState()) return
        val index = getChatLineIndex(UMouse.Raw.x.roundToInt(), UMouse.Raw.y.roundToInt())
        if (index == -1) return

        var chatLines = (mc.ingameGUI.chatGUI as IMixinGuiNewChat).drawnChatLines
        val cl = (mc.ingameGUI.chatGUI as IMixinGuiNewChat).drawnChatLines[index]

        var newIndex = 0
        if (chatLines.map { chatLine: ChatLine -> chatLine.chatComponent == cl.chatComponent }.count() > 1)
            for (i in 0 until index)
                if (chatLines[i].chatComponent == cl.chatComponent)
                    newIndex++

        search = false
        mc.ingameGUI.chatGUI.deleteChatLine("synthesissearchmode".hashCode())
        mc.ingameGUI.chatGUI.refreshChat()
        mc.ingameGUI.chatGUI.resetScroll()

        chatLines = (mc.ingameGUI.chatGUI as IMixinGuiNewChat).drawnChatLines
        var scrollAmount = -1
        if (newIndex == 0)
            scrollAmount = chatLines.stream().map { obj: ChatLine -> obj.chatComponent }.collect(Collectors.toList()).indexOf(cl.chatComponent)

        if (chatLines.count { chatLine: ChatLine -> chatLine.chatComponent == cl.chatComponent } > 1)
            for (i in chatLines.indices)
                if (chatLines[i].chatComponent == cl.chatComponent)
                    if (newIndex == 0) {
                        scrollAmount = i
                        break
                    } else newIndex--

        mc.ingameGUI.chatGUI.scroll(scrollAmount)
    }

    private fun getChatLineIndex(mouseX: Int, mouseY: Int): Int {
        return if (!mc.ingameGUI.chatGUI.chatOpen) -1
        else {
            val scaledresolution = ScaledResolution(mc)
            val i = scaledresolution.scaleFactor
            val f = mc.ingameGUI.chatGUI.chatScale
            var j = mouseX / i - 3
            var k = mouseY / i - 27
            j = floor(j / f).toInt()
            k = floor(k / f).toInt()
            if (j >= 0 && k >= 0) {
                val l = mc.ingameGUI.chatGUI.lineCount.coerceAtMost((mc.ingameGUI.chatGUI as IMixinGuiNewChat).drawnChatLines.size)
                if (j <= floor(mc.ingameGUI.chatGUI.chatWidth / mc.ingameGUI.chatGUI.chatScale) && k < mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    val i1 = k / mc.fontRendererObj.FONT_HEIGHT + (mc.ingameGUI.chatGUI as IMixinGuiNewChat).scrollPos
                    if (i1 >= 0 && i1 < (mc.ingameGUI.chatGUI as IMixinGuiNewChat).drawnChatLines.size) i1
                    else -1
                } else -1
            } else -1
        }
    }
}