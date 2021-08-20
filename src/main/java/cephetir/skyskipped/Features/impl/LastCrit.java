/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
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
        if (damageMatcher.matches() && strippedName.contains("✧")) {
            Cache.lastCrit = strippedName;
        }
    }
}
