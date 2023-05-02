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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.core.event.events.RunGameLoopEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature

class TerminatorClicker : Feature() {
    private var lastClick = 0L

    init {
        listener<RunGameLoopEvent.Start> {
            if (!Config.terminatorClicker.value || player == null || !mc.gameSettings.keyBindUseItem.isKeyDown) return@listener
            val item = player!!.heldItem ?: return@listener
            if (!item.hasDisplayName() || !item.displayName.contains("Terminator")) return@listener

            if (System.currentTimeMillis() - lastClick < Config.terminatorClickerDelay.value) return@listener
            if (mc.playerController.sendUseItem(player, world, item))
                mc.entityRenderer.itemRenderer.resetEquippedProgress2()
            lastClick = System.currentTimeMillis()
        }
    }
}