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

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Trail : Feature() {
    private var timer = 0L
    private val particle: EnumParticleTypes
        get() = EnumParticleTypes.values().find { it.name.equals(Config.trailParticle, true) } ?: EnumParticleTypes.DRIP_LAVA

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (System.currentTimeMillis() - timer < Config.trailInterval) return
        timer = System.currentTimeMillis()
        if (event.phase != TickEvent.Phase.START || !Config.trail || mc.thePlayer == null) return
        if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0) return

        mc.theWorld.spawnParticle(
            particle,
            mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ,
            0.0, 0.0, 0.0
        )
    }
}