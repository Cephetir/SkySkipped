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

package cephetir.skyskipped.Features.impl;

import cephetir.skyskipped.Features.Feature;
import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import cephetir.skyskipped.utils.TextUtils;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blocker extends Feature {
    public Blocker() {
        super("Blocker", "Slayers", "Blocks ability for specific items");
    }

    @SubscribeEvent
    public void block(PlayerInteractEvent event) {
        if (!Cache.inSkyblock || !Config.gsBlock) return;
        if (event.entity != mc.thePlayer || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        String item = "";
        try {
            item = TextUtils.stripColor(mc.thePlayer.getHeldItem().getDisplayName());
        } catch (NullPointerException ignored) {
            return;
        }
        if (!item.contains("Giant's Sword")) return;
        mc.thePlayer.playSound("random.orb", 0.8f, 1f);
        event.setCanceled(true);
    }
}
