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

package me.cephetir.skyskipped.features.impl.hacks;

import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blocker extends Feature {
    public Blocker() {
        super("Blocker", "Hacks", "Blocks ability for specific items");
    }

    @SubscribeEvent
    public void block(PlayerInteractEvent event) {
        if (!Cache.inSkyblock || !Config.gsBlock) return;
        if (event.entity != mc.thePlayer || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        String item = Cache.itemheld;
        if (!TextUtils.stripColor(item).toLowerCase().contains("giant's sword")) return;
        mc.thePlayer.playSound("random.orb", 0.8f, 1f);
        event.setCanceled(true);
    }
}
