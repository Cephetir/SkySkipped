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

package me.cephetir.skyskipped.features.impl.macro

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
import kotlin.math.roundToInt

class FarmingHUD : Feature() {
    companion object {
        var x: Float
            get() = Config.farmingHudX
            set(value) {
                Config.farmingHudX = value
            }
        var y: Float
            get() = Config.farmingHudY
            set(value) {
                Config.farmingHudY = value
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
            is NetherwartMacro -> ((Config.netherWartBanWaveCheckerTimer * 60000).toLong() - macro.banwaveCheckIn()).formatTime()
            is SugarCaneMacro -> ((Config.sugarCaneBanWaveCheckerTimer * 60000).toLong() - macro.banwaveCheckIn()).formatTime()
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
        if (!Config.farmingHud || !enabled || mc.thePlayer == null || mc.theWorld == null) return

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
            Config.farmingHudColor.rgb
        )

        val lineX = x + 5
        var lineY = y + 5
        for (line in lines) {
            mc.fontRendererObj.drawString(line, lineX.roundToInt(), lineY.roundToInt(), Config.farmingHudColorText.rgb)
            lineY += 11
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.farmingHud || !enabled || mc.thePlayer == null || mc.theWorld == null || event.phase != TickEvent.Phase.START) return

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