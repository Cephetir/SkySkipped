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

package me.cephetir.skyskipped.features.impl

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand

class LastCrit : Feature() {
    private val damagePattern = Regex("✧*(\\d+[⚔+✧❤♞☄✷]*)")

    override fun onRenderEntityModel(event: RenderEntityModelEvent) {
        if (!Cache.inSkyblock) return
        val entity: EntityLivingBase = event.entity as? EntityArmorStand ?: return
        if (!entity.hasCustomName()) return
        if (entity.isDead) return
        val strippedName: String = entity.customNameTag.stripColor()
        if (damagePattern.matches(strippedName) && strippedName.contains("✧")) Cache.lastCrit = strippedName
    }

    override fun isEnabled(): Boolean {
        return true
    }
}