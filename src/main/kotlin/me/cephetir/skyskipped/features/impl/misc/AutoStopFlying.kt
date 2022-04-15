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

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoStopFlying : Feature() {

    @SubscribeEvent
    fun onWorld(event: WorldEvent.Load) {
        if(!Config.stopFly || !Cache.inSkyblock) return
        Thread {
            try {
                val last = System.currentTimeMillis()
                var state = false
                while(!state) {
                    if(System.currentTimeMillis() - last >= 3000) return@Thread
                    state = mc.thePlayer.capabilities.isFlying
                    Thread.sleep(10)
                }
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true)
                Thread.sleep(100)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }
}