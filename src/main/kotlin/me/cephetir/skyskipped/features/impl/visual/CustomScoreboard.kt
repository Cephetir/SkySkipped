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
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.ScoreboardRenderEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.BlurUtils
import me.cephetir.skyskipped.utils.RoundUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.scoreboard.*
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.util.regex.Pattern

class CustomScoreboard : Feature() {

    @SubscribeEvent
    fun onDraw(event: ScoreboardRenderEvent) {
        if (!Config.customSb) return
        event.isCanceled = true
        renderScoreboard(event.objective, event.resolution)
    }

    private fun renderScoreboard(objective: ScoreObjective, resolution: ScaledResolution) {
        try {
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
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
                val scoreplayerteam = scoreboard.getPlayersTeam(score.playerName)
                val s = ScorePlayerTeam.formatPlayerName(
                    scoreplayerteam as Team,
                    score.playerName
                ) + ": " + EnumChatFormatting.RED + score.scorePoints
                width = width.coerceAtLeast(mc.fontRendererObj.getStringWidth(s))
            }
            val i1 = (collection.size * fontHeight).toFloat()
            val j1: Float = resolution.scaledHeight / 2.0f + i1 / 3.0f
            val k1 = 3.0f
            val l1: Float = resolution.scaledWidth - width - k1
            val m: Float = resolution.scaledWidth - k1 + 2.0f
            val blur = Config.customSbBlur
            if (Config.customSbBlurT) BlurUtils.renderBlurredBackground(
                blur,
                resolution.scaledWidth.toFloat(),
                resolution.scaledHeight.toFloat(),
                l1 - 2.0f,
                j1 - collection.size * fontHeight - fontHeight - 3.0f,
                m - (l1 - 2.0f),
                (fontHeight * (collection.size + 1) + 4).toFloat()
            )
            if (Config.customSbOutline) RoundUtils.drawRoundedOutline(
                l1 - 2.0f,
                j1 - collection.size * fontHeight - fontHeight - 3.0f,
                (l1 - 2.0f) + (m - (l1 - 2.0f)),
                (j1 - collection.size * fontHeight - fontHeight - 3.0f) + (fontHeight * (collection.size + 1) + 4).toFloat(),
                5.0f,
                2.5f,
                Config.customSbOutlineColor.rgb,
            )

            var i2 = 0
            for (score2 in collection) {
                ++i2
                val scoreplayerteam2: ScorePlayerTeam = scoreboard.getPlayersTeam(score2.playerName)
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
                else mc.fontRendererObj.drawStringWithShadow(s2, l1, k2, 553648127)
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
            GlStateManager.disableBlend()
            GlStateManager.enableTexture2D()
            GlStateManager.popMatrix()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}