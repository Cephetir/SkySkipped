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
import net.minecraftforge.event.entity.player.ItemTooltipEvent

class HidePetCandies : Feature() {

    override fun onTooltip(event: ItemTooltipEvent) {
        for (i in 0 until event.toolTip.size - 1) {
            if (event.toolTip[i].contains("Pet Candy Used")) {
                event.toolTip.removeAt(i)
                event.toolTip.removeAt(i - 1)
                break
            }
        }
    }

    override fun isEnabled(): Boolean {
        return Config.hidePetCandies
    }
}