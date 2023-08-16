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

package me.cephetir.skyskipped.features.impl.movement

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoStopFlying : Feature() {

    // #HypixelBestDevs
    @SubscribeEvent
    fun onWorld(event: WorldEvent.Load) {
        if (!Config.stopFly.value) return
        BackgroundScope.launch {
            val last = System.currentTimeMillis()
            var state = false
            while (!state) {
                if (System.currentTimeMillis() - last >= 6000) return@launch
                state = mc.thePlayer?.capabilities?.isFlying ?: continue && Cache.onIsland
                delay(100)
            }
            mc.thePlayer.capabilities.isFlying = false
            mc.thePlayer.sendPlayerAbilities()
        }
    }
}