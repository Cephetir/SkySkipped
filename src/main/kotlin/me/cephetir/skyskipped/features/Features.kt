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

package me.cephetir.skyskipped.features

import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.skyskipped.commands.dungeonCommands.FragRun
import me.cephetir.skyskipped.commands.dungeonCommands.LeaveCommand
import me.cephetir.skyskipped.commands.dungeonCommands.PartyCommand
import me.cephetir.skyskipped.features.impl.chat.*
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.dugeons.*
import me.cephetir.skyskipped.features.impl.hacks.*
import me.cephetir.skyskipped.features.impl.macro.FarmingHUD
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.features.impl.misc.*
import me.cephetir.skyskipped.features.impl.movement.AutoStopFlying
import me.cephetir.skyskipped.features.impl.visual.*

class Features {
    companion object {
        val leaveCommand = LeaveCommand()
        val partyCommand = PartyCommand()
        val petsOverlay = PetsOverlay()
    }

    private val features = listOf(
        ChestCloser(),
        ChatSwapper(),
        RPC,
        ESP(),
        Blocker(),
        Ping(),
        FragRun(),
        Pings(),
        HidePetCandies(),
        petsOverlay,
        PresentHighlight(),
        AutoGhostBlock(),
        PerspectiveToggle(),
        AutoMaddoxPhone(),
        CustomScoreboard(),
        HighlightUnlockedGemSots(),
        ItemSwap(),
        AutoStopFlying(),
        Metrics(),
        AutoCookieClicker(),
        AutoDojo(),
        Trail(),
        RoomDetection(),
        MacroManager,
        FarmingHUD(),
        HotbarSaver,
        LavaFishingSpots(),
        ItemRadius(),
        ZeroPingGui(),
        TerminalEsp(),
        AntiEscrow(),
        AutoSalvage(),
        ChatSearch(),
        SearchContainer(),
        AutoReply(),
        WitherDoorEsp(),
        AutoRedirectClick(),
        ShinyBlocks(),
        StopFallingBlocks(),
    )

    fun register() = features.forEach { BladeEventBus.subscribe(it, true) }
}
