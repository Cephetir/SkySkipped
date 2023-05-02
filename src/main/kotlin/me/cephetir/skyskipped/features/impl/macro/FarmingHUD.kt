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

package me.cephetir.skyskipped.features.impl.macro

import gg.essential.elementa.utils.withAlpha
import me.cephetir.bladecore.utils.TextUtils.formatTime
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.macros.NetherwartMacro
import me.cephetir.skyskipped.features.impl.macro.macros.SugarCaneMacro
import me.cephetir.skyskipped.gui.impl.GuiHudEditor
import me.cephetir.skyskipped.utils.render.RoundUtils
import me.cephetir.skyskipped.utils.render.shaders.BlurUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import kotlin.math.roundToInt

class FarmingHUD : Feature() {
    companion object {
        var x: Float
            get() = Config.farmingHudX.value.toFloat()
            set(value) {
                Config.farmingHudX.value = value.toDouble()
            }
        var y: Float
            get() = Config.farmingHudY.value.toFloat()
            set(value) {
                Config.farmingHudY.value = value.toDouble()
            }

        var width = 0
        var height = 0
    }

    private val macro: Macro
        get() = MacroManager.current
    private val enabled: Boolean
        get() = macro.enabled || mc.currentScreen is GuiHudEditor

    private val timeElapsed: String
        get() = (System.currentTimeMillis() - MacroManager.startTime).formatTime()
    private val timeBanCheck: String
        get() = when (macro) {
            is NetherwartMacro -> ((Config.netherWartBanWaveCheckerTimer.value * 60000).toLong() - macro.banwaveCheckIn()).formatTime()
            is SugarCaneMacro -> ((Config.sugarCaneBanWaveCheckerTimer.value * 60000).toLong() - macro.banwaveCheckIn()).formatTime()
            else -> "0s"
        }
    private val profit: Long
        get() = when (macro) {
            is NetherwartMacro -> macro.cropsMined() * 2
            is SugarCaneMacro -> macro.cropsMined() * 2
            else -> 0L
        }

    var text: String = ""

    @SubscribeEvent
    fun onRender(event: TickEvent.RenderTickEvent) {
        if (!Config.farmingHud.value || !enabled || mc.thePlayer == null || mc.theWorld == null) return

        val lines = text.split("\n")
        width = 0
        height = lines.size * 11 + 10
        for (line in lines) {
            val lineWidth = mc.fontRendererObj.getStringWidth(line)
            if (lineWidth > width) width = lineWidth
        }
        width += 10

        BlurUtils.blurAreaRounded(
            x, y,
            x + width,
            y + height,
            5f,
            12f
        )
        RoundUtils.drawRoundedRect(
            x,
            y,
            x + width,
            y + height,
            5f,
            Color.BLACK.withAlpha(110).rgb
        )

        val lineX = x + 5
        var lineY = y + 5
        for (line in lines) {
            mc.fontRendererObj.drawString(line, lineX.roundToInt(), lineY.roundToInt(), Color.RED.darker().rgb)
            lineY += 11
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.farmingHud.value || !enabled || mc.thePlayer == null || null == mc.theWorld || event.phase != TickEvent.Phase.START) return

        text = """
               | Welcome Back, ${mc.thePlayer.displayNameString}!
               | Current Macro: ${macro.name}
               | Time Elapsed: $timeElapsed
               | Ban Wave: ${macro.isBanwave()}, Check in: $timeBanCheck
               | Crops Mined: ${macro.cropsMined()}
               | Profit: $profit
               """.trimIndent()
    }
}