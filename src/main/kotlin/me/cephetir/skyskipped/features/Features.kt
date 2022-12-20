/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
