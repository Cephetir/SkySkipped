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