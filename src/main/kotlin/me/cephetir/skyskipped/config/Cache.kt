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

package me.cephetir.skyskipped.config

import me.cephetir.bladecore.core.listeners.SkyblockIsland
import me.cephetir.bladecore.core.listeners.SkyblockListener

object Cache {
    val inDungeon
        get() = SkyblockListener.island == SkyblockIsland.Dungeon

    val onSkyblock
        get() = SkyblockListener.onSkyblock
    val inWorkshop
        get() = SkyblockListener.island == SkyblockIsland.JerryWorkshop
    val onIsland
        get() = SkyblockListener.island == SkyblockIsland.PrivateIsland || SkyblockListener.island == SkyblockIsland.Garden
    val isJacob
        get() = SkyblockListener.jacobEvent

    @JvmField
    var prevIP = ""

    @JvmField
    var prevName = ""

    @JvmField
    var prevIsLan = false
}