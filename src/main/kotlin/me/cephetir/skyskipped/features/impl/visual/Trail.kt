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

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Trail : Feature() {
    private var timer = 0L
    private val particle: EnumParticleTypes
        get() = EnumParticleTypes.values().find { it.name.equals(Config.trailParticle.value, true) } ?: EnumParticleTypes.DRIP_LAVA

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (System.currentTimeMillis() - timer < Config.trailInterval.value) return
        timer = System.currentTimeMillis()
        if (event.phase != TickEvent.Phase.START || !Config.trail.value || mc.thePlayer == null) return
        if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0) return

        mc.theWorld.spawnParticle(
            particle,
            mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ,
            0.0, 0.0, 0.0
        )
    }
}