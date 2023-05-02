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

import com.mojang.realmsclient.gui.ChatFormatting
import gg.essential.universal.ChatColor
import me.cephetir.bladecore.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.ScoreboardRenderEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.render.RenderUtils
import me.cephetir.skyskipped.utils.render.RoundUtils
import me.cephetir.skyskipped.utils.render.shaders.BlurUtils
import me.cephetir.skyskipped.utils.render.shaders.ShadowUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.scoreboard.*
import net.minecraft.util.EnumChatFormatting
import java.awt.Color
import java.text.DecimalFormat
import java.util.regex.Pattern

class CustomScoreboard : Feature() {
    private var lastX = 0f
    private var lastY = 0f
    private var lastWidth = 0f
    private var lastHeight = 0f

    init {
        safeListener<ScoreboardRenderEvent> {
            if (!Config.customSb.value) return@safeListener
            it.cancel()
            renderScoreboard(it.objective, it.resolution)
        }
    }

    private fun renderScoreboard(objective: ScoreObjective, resolution: ScaledResolution) {
        try {
            GlStateManager.pushMatrix()
            GlStateManager.resetColor()
            val scoreboard: Scoreboard = objective.scoreboard
            var collection = scoreboard.getSortedScores(objective) as Collection<Score>
            val list = collection.filter { it.playerName != null && !it.playerName.startsWith("#") }
            collection = if (list.size > 15) {
                list.drop(15)
            } else {
                list
            }

            var width = mc.fontRendererObj.getStringWidth(objective.displayName)
            val fontHeight = mc.fontRendererObj.FONT_HEIGHT
            for (score in collection) {
                val scoreplayerteam = scoreboard.getPlayersTeam(score.playerName) ?: continue
                val s = a(
                    ScorePlayerTeam.formatPlayerName(
                        scoreplayerteam as Team,
                        score.playerName
                    ) + if (!Config.customSbNumbers.value) ": " + EnumChatFormatting.RED + score.scorePoints else ""
                )
                width = width.coerceAtLeast(mc.fontRendererObj.getStringWidth(s))
            }
            val i1 = (collection.size * fontHeight).toFloat()
            val j1: Float = resolution.scaledHeight / 2.0f + i1 / 3.0f
            val l1: Float = resolution.scaledWidth - width - 5.0f
            val m: Float = resolution.scaledWidth - 5.0f + 2.0f

            val x: Float
            val y: Float
            val w: Float
            val h: Float
            if (lastX == 0f && lastY == 0f) {
                x = l1 - 2.0f
                lastX = x
                y = j1 - collection.size * fontHeight - fontHeight - 3.0f
                lastY = y
                w = m - (l1 - 2.0f)
                lastWidth = w
                h = (fontHeight * (collection.size + 1) + 4).toFloat()
                lastHeight = h
            } else {
                x = RenderUtils.animate(l1 - 2.0f, lastX, 0.2f)
                lastX = x
                y = RenderUtils.animate(j1 - collection.size * fontHeight - fontHeight - 3.0f, lastY, 0.2f)
                lastY = y
                w = RenderUtils.animate(m - (l1 - 2.0f), lastWidth, 0.2f)
                lastWidth = w
                h = RenderUtils.animate((fontHeight * (collection.size + 1) + 4).toFloat(), lastHeight, 0.2f)
                lastHeight = h
            }

            if (Config.customSbBlurT.value) BlurUtils.blurAreaRounded(
                x, y,
                x + w, y + h,
                8f,
                Config.customSbBlur.value.toFloat()
            )
            if (Config.customSbBg.value) RoundUtils.drawRoundedRect(
                x - 0.5f,
                y - 0.5f,
                x + w + 0.5f,
                y + h + 0.5f,
                5f,
                Color(Config.customSbBgColorR.value.toInt(), Config.customSbBgColorG.value.toInt(), Config.customSbBgColorB.value.toInt(), Config.customSbBgColorA.value.toInt()).rgb
            )
            if (Config.customSbShadow.value) ShadowUtils.shadow(Config.customSbShadowStr.value.toFloat(),
                { RoundUtils.drawRoundedRect(x, y, x + w, y + h, 5f, Color(0, 0, 0, 210).rgb) },
                { RoundUtils.drawRoundedRect(x, y, x + w, y + h, 5f) }
            )
            if (Config.customSbOutline.value) RoundUtils.drawRoundedOutline(
                x - 1, y - 1, x + w + 1, y + h + 1,
                5f,
                2.5f,
                if (!Config.customSbOutlineColorRainbow.value) Color(
                    Config.customSbOutlineColorR.value.toInt(),
                    Config.customSbOutlineColorG.value.toInt(),
                    Config.customSbOutlineColorB.value.toInt()
                ).rgb
                else RenderUtils.getChroma(3000F, 0),
            )

            var i2 = 0
            for (score2 in collection) {
                ++i2
                val scoreplayerteam2: ScorePlayerTeam = scoreboard.getPlayersTeam(score2.playerName) ?: continue
                var s2: String = ScorePlayerTeam.formatPlayerName(scoreplayerteam2 as Team, score2.playerName)
                    .replace("§ewww.hypixel.ne\ud83c\udf82§et", Config.customSbText.value.replace("&", "§"))
                val k2 = j1 - i2 * fontHeight
                val matcher = Pattern.compile("\\d\\d/\\d\\d/\\d\\d").matcher(s2)
                if (Config.customSbLobby.value && matcher.find()) s2 = ChatFormatting.GRAY.toString() + matcher.group()
                val flag = s2 == Config.customSbText.value.replace("&", "§")
                if (flag) mc.fontRendererObj.drawStringWithShadow(
                    s2,
                    l1 + width / 2.0f - mc.fontRendererObj.getStringWidth(s2) / 2,
                    k2,
                    Color(Config.customSbOutlineColorR.value.toInt(), Config.customSbOutlineColorG.value.toInt(), Config.customSbOutlineColorB.value.toInt()).rgb
                )
                else mc.fontRendererObj.drawStringWithShadow(a(s2), l1, k2, 553648127)
                if (i2 == collection.size) {
                    val s3: String = objective.displayName
                    mc.fontRendererObj.drawStringWithShadow(
                        s3,
                        l1 + width / 2.0f - mc.fontRendererObj.getStringWidth(s3) / 2.0f,
                        k2 - fontHeight,
                        Color.white.rgb
                    )
                }
            }
            GlStateManager.resetColor()
            GlStateManager.popMatrix()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun a(text: String): String {
        val txt = text.stripColor().keepScoreboardCharacters().trim()
        return if (Config.coinsToggle.value && txt.startsWith("Purse: ")) {
            val coins = txt.substring(7).split(" ")[0].replace(",", "").toDouble()
            val needed = coins + Config.coins.value.toLong()
            val format = DecimalFormat("###,###.##")
            val s = format.format(needed).replace(" ", ",")
            "Purse: " + ChatColor.GOLD + s
        } else SkySkipped.getCosmetics(text)!!
    }
}