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

package me.cephetir.skyskipped.features.impl.discordrpc

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature

object RPC : Feature() {
    private val discordRPCManager = DiscordRPCManager()

    fun init() = discordRPCManager.connect()

    fun shutdown() = discordRPCManager.disconnect()

    fun reset(enabled: Boolean = Config.DRPC) {
        if (enabled && !discordRPCManager.isActive) discordRPCManager.connect()
        else if (!enabled && discordRPCManager.isActive) discordRPCManager.disconnect()
    }
}
