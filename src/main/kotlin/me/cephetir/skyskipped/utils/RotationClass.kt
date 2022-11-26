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

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.RenderWorldLastEvent
import kotlin.math.pow

class RotationClass(rotation: Rotation, time: Long) {
    private var startRot = Rotation(0f, 0f)
    private var endRot = Rotation(0f, 0f)
    private var startTime = 0L
    private var endTime = 0L
    @Volatile
    var done = true

    init {
        done = false
        startRot = Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        val neededChange = getNeededChange(startRot, rotation)
        endRot = Rotation(startRot.yaw + neededChange.yaw, startRot.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = System.currentTimeMillis() + time

        listener<RenderWorldLastEvent> {
            if (world == null || player == null) return@listener BladeEventBus.unsubscribe(this)

            if (System.currentTimeMillis() <= endTime) {
                mc.thePlayer.rotationYaw = interpolate(startRot.yaw, endRot.yaw)
                mc.thePlayer.rotationPitch = interpolate(startRot.pitch, endRot.pitch)
            } else if (!done) {
                mc.thePlayer.rotationYaw = endRot.yaw
                mc.thePlayer.rotationPitch = endRot.pitch
                done = true
                BladeEventBus.unsubscribe(this)
            }
        }

        BladeEventBus.subscribe(this)
    }

    private fun interpolate(start: Float, end: Float): Float {
        val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
        val relativeProgress = spentMillis / (endTime - startTime).toFloat()
        return (end - start) * easeOutCubic(relativeProgress.toDouble()) + start
    }

    private fun easeOutCubic(number: Double): Float {
        return (1.0 - (1.0 - number).pow(3.0)).toFloat()
    }

    private fun getNeededChange(startRot: Rotation, endRot: Rotation): Rotation {
        var yawChng = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(startRot.yaw)
        if (yawChng <= -180.0f) yawChng += 360.0f
        else if (yawChng > 180.0f) yawChng += -360.0f
        return Rotation(yawChng, endRot.pitch - startRot.pitch)
    }

    data class Rotation(var yaw: Float, var pitch: Float)
}