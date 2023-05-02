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
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.Feature
import net.minecraft.entity.monster.EntityCreeper

class HideWitherCloak : Feature() {
    init {
        listener<RenderEntityModelEvent> {
            if (!Config.hideWitherCloak.value || !Cache.onSkyblock || it.entity !is EntityCreeper || it.entity.onGround || it.entity.maxHealth != 20f || !it.entity.powered)
                return@listener

            it.cancel()
            it.entity.worldObj.removeEntity(it.entity)
        }
    }
}