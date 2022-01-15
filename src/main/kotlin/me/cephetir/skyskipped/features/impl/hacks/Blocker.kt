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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraftforge.event.entity.player.PlayerInteractEvent

class Blocker : Feature() {

    override fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!Cache.inSkyblock) return
        if (event.entity !== mc.thePlayer || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return
        val item = Cache.itemheld
        if (!item.stripColor().lowercase().contains("giant's sword")) return
        mc.thePlayer.playSound("random.orb", 0.8f, 1f)
        event.isCanceled = true
    }

    override fun isEnabled(): Boolean {
        return Config.gsBlock
    }
}