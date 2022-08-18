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

package me.cephetir.skyskipped.features

import me.cephetir.skyskipped.commands.dungeonCommands.FragRun
import me.cephetir.skyskipped.commands.dungeonCommands.LeaveCommand
import me.cephetir.skyskipped.commands.dungeonCommands.PartyCommand
import me.cephetir.skyskipped.features.impl.chat.AutoMaddoxPhone
import me.cephetir.skyskipped.features.impl.chat.ChatSearch
import me.cephetir.skyskipped.features.impl.chat.ChatSwapper
import me.cephetir.skyskipped.features.impl.chat.Ping
import me.cephetir.skyskipped.features.impl.discordrpc.RPC
import me.cephetir.skyskipped.features.impl.dugeons.*
import me.cephetir.skyskipped.features.impl.hacks.*
import me.cephetir.skyskipped.features.impl.macro.FarmingHUD
import me.cephetir.skyskipped.features.impl.macro.MacroManager
import me.cephetir.skyskipped.features.impl.misc.*
import me.cephetir.skyskipped.features.impl.movement.AutoStopFlying
import me.cephetir.skyskipped.features.impl.visual.*
import net.minecraftforge.common.MinecraftForge

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
        FailSafe(),
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
        AdminRoomDetection(),
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
    )

    fun register() = features.forEach { MinecraftForge.EVENT_BUS.register(it) }
}
