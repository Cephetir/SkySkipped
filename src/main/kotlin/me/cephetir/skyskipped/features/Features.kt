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
import me.cephetir.skyskipped.features.impl.LastCrit
import me.cephetir.skyskipped.features.impl.chat.ChatSwapper
import me.cephetir.skyskipped.features.impl.chat.Ping
import me.cephetir.skyskipped.features.impl.dugeons.*
import me.cephetir.skyskipped.features.impl.hacks.Blocker
import me.cephetir.skyskipped.features.impl.hacks.PizzaFailSafe
import me.cephetir.skyskipped.features.impl.visual.HidePetCandies
import me.cephetir.skyskipped.features.impl.visual.PerspectiveToggle
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay
import me.cephetir.skyskipped.features.impl.visual.PresentHighlight
import net.minecraftforge.common.MinecraftForge

class Features {

    companion object {
        val leaveCommand = LeaveCommand()
        val partyCommand = PartyCommand()
        val petsOverlay = PetsOverlay()
        val termsDisplay = TermsDisplay()
    }

    var features: MutableList<Feature> = mutableListOf(
        ChestCloser(),
        ChatSwapper(),
        PlayerESP(),
        LastCrit(),
        Blocker(),
        Ping(),
        FragRun(),
        ScoreCalculation(),
        PizzaFailSafe(),
        HidePetCandies(),
        petsOverlay,
        PresentHighlight(),
        termsDisplay,
        AutoGhostBlock(),
        PerspectiveToggle()
    )

    fun register() = features.forEach { MinecraftForge.EVENT_BUS.register(it) }
}
