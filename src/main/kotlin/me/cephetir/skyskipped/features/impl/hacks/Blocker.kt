/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
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

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.skyblock.ItemUtils.getSkyBlockID
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class Blocker : Feature() {

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!Cache.onSkyblock || !Config.block.value) return
        if (event.entity != mc.thePlayer || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return

        if (Config.blockZombieSword.value &&
            mc.thePlayer.heldItem?.getSkyBlockID()?.contains("ZOMBIE_SWORD") == true &&
            mc.thePlayer.health >= mc.thePlayer.maxHealth
        ) {
            event.isCanceled = true
            return
        }

        val item = mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
        if (!item.containsAny(Config.blockList.value.split(", "))) return
        mc.thePlayer.playSound("random.orb", 0.8f, 1f)
        event.isCanceled = true
    }
}