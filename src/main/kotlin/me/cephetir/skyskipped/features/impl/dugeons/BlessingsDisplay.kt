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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinGuiPlayerTabOverlay
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.render.RoundUtils
import me.cephetir.skyskipped.utils.romanToArabic
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color

class BlessingsDisplay : Feature() {
    companion object {
        var x: Double
            get() = Config.blessingsDisplayX.value
            set(value) {
                Config.blessingsDisplayX.value = value
            }
        var y: Double
            get() = Config.blessingsDisplayY.value
            set(value) {
                Config.blessingsDisplayY.value = value
            }
        val width: Float
            get() = mc.fontRendererObj.getStringWidth("✎ ✦ Blessing of Wisdom 1000") * Config.blessingsDisplayScale.value.toFloat() + 10f
        var height: Float = (mc.fontRendererObj.FONT_HEIGHT + 4) * 10 + 10f

        fun renderDummy() {
            if (Config.blessingsDisplay.value && player != null && Cache.inDungeon) return

            GlStateManager.pushMatrix()
            val scale = Config.blessingsDisplayScale.value.toFloat()
            GlStateManager.scale(scale, scale, scale)
            val blessings = listOf(
                "§6Dungeon Buffs",
                "§c❤ §4❣ §cBlessing of Life §e1",
                "§c❁ §9☠ §cBlessing of Power §e3",
                "§a❈ §4❁ §cBlessing of Stone §e69",
                "§b✎ §f✦ §cBlessing of Wisdom §e420",
                "§c❤ §b✎ §cBlessing of Time §e99"
            )
            val x = x.toFloat()
            val x1 = Companion.x.toFloat() + width
            val y = y.toFloat()
            height = (mc.fontRendererObj.FONT_HEIGHT * scale + 4) * blessings.size + 10f
            val y1 = Companion.y.toFloat() + height
            RoundUtils.drawRoundedRect(
                x / scale, y / scale, x1 / scale, y1 / scale,
                10f, Color(0, 0, 0, 100).rgb
            )
            for ((i, text) in blessings.withIndex())
                mc.fontRendererObj.drawString(text, (x + 5) / scale, (y + 5) / scale + (mc.fontRendererObj.FONT_HEIGHT + 4 / scale) * i, -1, false)
            GlStateManager.scale(1 / scale, 1 / scale, 1 / scale)
            GlStateManager.popMatrix()
        }
    }

    private val blessings = mutableListOf<String>()

    init {
        listener<ClientTickEvent> {
            if (player == null || !Cache.inDungeon || !Config.blessingsDisplay.value) return@listener
            val footer = (mc.ingameGUI.tabList as IMixinGuiPlayerTabOverlay).footer.unformattedText.stripColor()
            var lines = footer.split("\n")
            lines = lines.dropWhile { it != "Dungeon Buffs" }.dropLast(2)
            if (lines.size < 2 || lines[1].contains("No Buffs active")) {
                blessings.clear()
                blessings.addAll(listOf("§6Dungeon Buffs", "§4None"))
                return@listener
            }
            lines = lines.map { text ->
                if (text == "Dungeon Buffs")
                    "§6Dungeon Buffs"
                else {
                    val level = text.split(" ").also { if (it.size < 3) return@map text }[3]
                    val number = romanToArabic(level)
                    text.replaceAfterLast(" ", number.toString())
                    when {
                        text.startsWith("Blessing of Life") -> "§c❤ §4❣ §cBlessing of Life §e$number"
                        text.startsWith("Blessing of Power") -> "§c❁ §9☠ §cBlessing of Power §e$number"
                        text.startsWith("Blessing of Stone") -> "§a❈ §4❁ §cBlessing of Stone §e$number"
                        text.startsWith("Blessing of Wisdom") -> "§b✎ §f✦ §cBlessing of Wisdom §e$number"
                        text.startsWith("Blessing of Time") -> "§c❤ §b✎ §cBlessing of Time §e$number"
                        else -> text
                    }
                }
            }
            blessings.clear()
            blessings.addAll(lines)
        }

        listener<RenderGameOverlayEvent.Chat> {
            if (!Config.blessingsDisplay.value || player == null || !Cache.inDungeon) return@listener

            GlStateManager.pushMatrix()
            val scale = Config.blessingsDisplayScale.value.toFloat()
            GlStateManager.scale(scale, scale, scale)
            val x = x.toFloat()
            val x1 = Companion.x.toFloat() + width
            val y = y.toFloat()
            height = (mc.fontRendererObj.FONT_HEIGHT * scale + 4) * blessings.size + 10f
            val y1 = Companion.y.toFloat() + height
            RoundUtils.drawRoundedRect(
                x / scale, y / scale, x1 / scale, y1 / scale,
                10f, Color(0, 0, 0, 100).rgb
            )
            for ((i, text) in blessings.withIndex())
                mc.fontRendererObj.drawString(text, (x + 5) / scale, (y + 5) / scale + (mc.fontRendererObj.FONT_HEIGHT + 4 / scale) * i, -1, false)
            GlStateManager.scale(1 / scale, 1 / scale, 1 / scale)
            GlStateManager.popMatrix()
        }

        listener<WorldEvent.Load> {
            blessings.clear()
        }
    }
}