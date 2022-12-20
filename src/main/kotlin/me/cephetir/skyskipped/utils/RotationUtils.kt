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

package me.cephetir.skyskipped.utils

import me.cephetir.skyskipped.mixins.accessors.IMixinEntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

object RotationUtils {

    fun getServerAngles(vec: Vec3): FloatArray {
        val diffX = vec.xCoord - mc.thePlayer.posX
        val diffY = vec.yCoord - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        val diffZ = vec.zCoord - mc.thePlayer.posZ
        val yaw = (atan2(diffZ, diffX) * 180.0 / PI).toFloat() - 90.0f
        val dist = sqrt(diffX * diffX + diffZ * diffZ)
        val pitch = (-(atan2(diffY, dist) * 180.0 / PI)).toFloat()
        val player = mc.thePlayer as IMixinEntityPlayerSP
        return floatArrayOf(
            player.lastReportedYaw + MathHelper.wrapAngleTo180_float(yaw - player.lastReportedYaw),
            player.lastReportedPitch + MathHelper.wrapAngleTo180_float(pitch - player.lastReportedPitch)
        )
    }

    fun getServerAngles(en: Entity): FloatArray {
        return getServerAngles(
            Vec3(
                en.posX,
                en.posY + (en.eyeHeight - en.height / 1.5) + 0.5,
                en.posZ
            )
        )
    }
}