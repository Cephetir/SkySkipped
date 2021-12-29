/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
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

package me.cephetir.skyskipped.features.impl;

import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HidePetCandies extends Feature {

    public HidePetCandies() {
        super("HidePetCandies", "Visual", "Hide pet's candies counter in tooltip.");
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!Config.hidePetCandies) return;
        for (int i = 0; i < event.toolTip.size(); i++) {
            if (event.toolTip.get(i).contains("Pet Candy Used")) {
                event.toolTip.remove(i);
                event.toolTip.remove(i - 1);
            }
        }
    }
}
