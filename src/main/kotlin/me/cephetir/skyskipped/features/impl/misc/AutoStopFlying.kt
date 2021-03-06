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

package me.cephetir.skyskipped.features.impl.misc

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoStopFlying : Feature() {

    // #HypixelBestDevs
    @SubscribeEvent
    fun onWorld(event: WorldEvent.Load) {
        if (!Config.stopFly) return
        BackgroundScope.launch {
            val last = System.currentTimeMillis()
            var state = false
            while (!state) {
                if (System.currentTimeMillis() - last >= 6000) return@launch
                state = mc.thePlayer?.capabilities?.isFlying ?: continue && Cache.onIsland
                delay(100)
            }
            mc.thePlayer.capabilities.isFlying = false
        }
    }
}