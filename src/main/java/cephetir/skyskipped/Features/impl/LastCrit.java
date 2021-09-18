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
import cephetir.skyskipped.utils.TextUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastCrit extends Feature {
    private final Pattern damagePattern = Pattern.compile("✧*(\\d+[⚔+✧❤♞☄✷]*)");

    public LastCrit() {
        super("Last Crit", "Dungeons", "Shows last critical hit");
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
        if (!Cache.inSkyblock) return;
        Entity entity = event.entity;
        if (!(entity instanceof EntityArmorStand)) return;
        if (!entity.hasCustomName()) return;
        if (entity.isDead) return;
        String strippedName = TextUtils.stripColor(entity.getCustomNameTag());
        Matcher damageMatcher = damagePattern.matcher(strippedName);
        if (damageMatcher.matches() && strippedName.contains("✧")) Cache.lastCrit = strippedName;
    }
}
