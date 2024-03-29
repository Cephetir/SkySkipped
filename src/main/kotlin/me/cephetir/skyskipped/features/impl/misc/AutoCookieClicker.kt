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

package me.cephetir.skyskipped.features.impl.misc

import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils.getName
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoCookieClicker : Feature() {
    init {
        safeListener<ClientTickEvent> {
            if (!Config.cookieClicker.value) return@safeListener
            val container = player.openContainer ?: return@safeListener
            if (container !is ContainerChest) return@safeListener
            if (container.getName().startsWith("Cookie Clicker"))
                playerController.windowClick(container.windowId, 13, 2, 3, player)
        }
    }
}