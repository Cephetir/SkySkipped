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

package me.cephetir.skyskipped.utils

import me.cephetir.skyskipped.mixins.IMixinEntityPlayerSP
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

object RotationUtils {

    fun getServerAngles(vec: Vec3): FloatArray {
        val diffX = vec.xCoord - Minecraft.getMinecraft().thePlayer.posX
        val diffY = vec.yCoord - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight())
        val diffZ = vec.zCoord - Minecraft.getMinecraft().thePlayer.posZ
        val yaw = (atan2(diffZ, diffX) * 180.0 / PI).toFloat() - 90.0f
        val dist = sqrt(diffX * diffX + diffZ * diffZ)
        val pitch = (-(atan2(diffY, dist) * 180.0 / PI)).toFloat()
        return floatArrayOf(
            (Minecraft.getMinecraft().thePlayer as IMixinEntityPlayerSP).lastReportedYaw + MathHelper.wrapAngleTo180_float(yaw - (Minecraft.getMinecraft().thePlayer as IMixinEntityPlayerSP).lastReportedYaw),
            (Minecraft.getMinecraft().thePlayer as IMixinEntityPlayerSP).lastReportedPitch + MathHelper.wrapAngleTo180_float(pitch - (Minecraft.getMinecraft().thePlayer as IMixinEntityPlayerSP).lastReportedPitch)
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