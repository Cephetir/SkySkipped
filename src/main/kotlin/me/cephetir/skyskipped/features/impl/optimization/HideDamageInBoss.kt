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

package me.cephetir.skyskipped.features.impl.optimization

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent

class HideDamageInBoss : Feature() {
    init {
        listener<ClientChatReceivedEvent> {
            if (it.message.unformattedText.contains("Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! You are not allowed to use existing Potion Effects while in Dungeons.")) {
                inBoss = false
                return@listener
            }
            if (!Cache.inDungeon) return@listener
            val msg = it.message.unformattedText.stripColor()
            if (msg.startsWith("[BOSS]") && !msg.contains("The Watcher"))
                inBoss = true
        }

        listener<RenderEntityModelEvent> {
            if (!Config.hideDamageInBoss.value || !Cache.inDungeon || !inBoss || !isDamageText(it.entity)) return@listener

            it.cancel()
            it.entity.worldObj.removeEntity(it.entity)
        }

        listener<WorldEvent.Load> {
            inBoss = false
        }
    }

    companion object {
        private val damagePattern = Regex("[✧✯]?(\\d{1,3}(?:,\\d{3})*[⚔+✧❤♞☄✷ﬗ✯]*)")
        fun isDamageText(entity: Entity) =
            entity.ticksExisted < 300 && !entity.isDead && entity is EntityArmorStand && entity.hasCustomName() && damagePattern.matches(entity.customNameTag.stripColor())

        var inBoss = false
    }
}