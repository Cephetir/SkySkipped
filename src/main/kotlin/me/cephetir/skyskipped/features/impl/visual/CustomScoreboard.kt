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

import com.mojang.realmsclient.gui.ChatFormatting
import gg.essential.universal.ChatColor
import me.cephetir.skyskipped.SkySkipped.Companion.cosmetics
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.config.Config.Companion.coinsToggle
import me.cephetir.skyskipped.config.Config.Companion.customSbNumbers
import me.cephetir.skyskipped.event.events.ScoreboardRenderEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.BlurUtils
import me.cephetir.skyskipped.utils.RenderUtils
import me.cephetir.skyskipped.utils.RoundUtils
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.scoreboard.*
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.text.DecimalFormat
import java.util.regex.Pattern

class CustomScoreboard : Feature() {
    private var lastX = 0f
    private var lastY = 0f
    private var lastWidth = 0f
    private var lastHeight = 0f

    @SubscribeEvent
    fun onDraw(event: ScoreboardRenderEvent) {
        if (!Config.customSb) return
        event.isCanceled = true
        renderScoreboard(event.objective, event.resolution)
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
                    ) + (if (!customSbNumbers) ": " + EnumChatFormatting.RED + score.scorePoints else "")
                )
                width = width.coerceAtLeast(mc.fontRendererObj.getStringWidth(s))
            }
            val i1 = (collection.size * fontHeight).toFloat()
            val j1: Float = resolution.scaledHeight / 2.0f + i1 / 3.0f
            val k1 = 3.0f
            val l1: Float = resolution.scaledWidth - width - k1
            val m: Float = resolution.scaledWidth - k1 + 2.0f
            val blur = Config.customSbBlur

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
            if (Config.customSbBlurT) BlurUtils.renderBlurredBackground(
                blur,
                resolution.scaledWidth.toFloat(),
                resolution.scaledHeight.toFloat(),
                x, y, w, h
            )
            if (Config.customSbOutline) RoundUtils.drawRoundedOutline(
                x, y, x + w, y + h,
                5.0f,
                2.5f,
                Config.customSbOutlineColor.rgb,
            )

            var i2 = 0
            for (score2 in collection) {
                ++i2
                val scoreplayerteam2: ScorePlayerTeam = scoreboard.getPlayersTeam(score2.playerName) ?: continue
                var s2: String = ScorePlayerTeam.formatPlayerName(scoreplayerteam2 as Team, score2.playerName)
                    .replace("§ewww.hypixel.ne\ud83c\udf82§et", Config.customSbText.replace("&", "§"))
                val k2 = j1 - i2 * fontHeight
                val matcher = Pattern.compile("[0-9][0-9]/[0-9][0-9]/[0-9][0-9]").matcher(s2)
                if (Config.customSbLobby && matcher.find()) s2 = ChatFormatting.GRAY.toString() + matcher.group()
                val flag = s2 == Config.customSbText.replace("&", "§")
                if (flag) mc.fontRendererObj.drawStringWithShadow(
                    s2,
                    l1 + width / 2.0f - mc.fontRendererObj.getStringWidth(s2) / 2,
                    k2,
                    Config.customSbOutlineColor.rgb
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
        return if (coinsToggle && txt.startsWith("Purse: ")) {
            val coins = txt.substring(7).split(" ").toTypedArray()[0].replace(",", "").toDouble()
            val needed = coins + Config.coins
            val format = DecimalFormat("###,###.##")
            val s = format.format(needed).replace(" ", ",")
            "Purse: " + ChatColor.GOLD + s
        } else if (customSbNumbers && text.startsWith(EnumChatFormatting.RED.toString() + "") &&
            Pattern.compile("\\d+").matcher(txt).matches()
        ) ""
        else if (Minecraft.getMinecraft().thePlayer != null && text.contains(Minecraft.getMinecraft().thePlayer.name)) text.replace(
            Minecraft.getMinecraft().thePlayer.name, cosmetics[Minecraft.getMinecraft().thePlayer.name]!!
                .component1().replace("&", "§")
        ) else text
    }
}