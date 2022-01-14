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
import me.cephetir.skyskipped.event.events.PacketReceive
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.impl.LastCrit
import me.cephetir.skyskipped.features.impl.chat.ChatSwapper
import me.cephetir.skyskipped.features.impl.chat.Ping
import me.cephetir.skyskipped.features.impl.dugeons.ChestCloser
import me.cephetir.skyskipped.features.impl.dugeons.PlayerESP
import me.cephetir.skyskipped.features.impl.dugeons.ScoreCalculation
import me.cephetir.skyskipped.features.impl.hacks.Blocker
import me.cephetir.skyskipped.features.impl.hacks.PizzaFailSafe
import me.cephetir.skyskipped.features.impl.visual.HidePetCandies
import me.cephetir.skyskipped.features.impl.visual.PetsOverlay
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.function.Consumer

class Features {
    val leaveCommand = LeaveCommand()
    val partyCommand = PartyCommand()
    val scoreCalculation = ScoreCalculation()
    val petsOverlay = PetsOverlay()

    var features: MutableList<Feature> = mutableListOf(
        ChestCloser(),
        ChatSwapper(),
        PlayerESP(),
        LastCrit(),
        Blocker(),
        Ping(),
        FragRun(),
        scoreCalculation,
        PizzaFailSafe(),
        HidePetCandies(),
        petsOverlay
    )

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onTick(event) })

    @SubscribeEvent
    fun onDrawBackground(event: GuiOpenEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onDrawBackground(event) })

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onChat(event) })

    @SubscribeEvent
    fun onRenderEntityModel(event: RenderEntityModelEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onRenderEntityModel(event) })

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onEntityJoinWorld(event) })

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onWorldLoad(event) })

    @SubscribeEvent
    fun onPacket(event: PacketReceive) = features.forEach(Consumer { f -> if (f.isEnabled()) { f.onScoreboardChange(event); f.onTabChange(event) }  })

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onPlayerInteract(event) })

    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) = features.forEach(Consumer { f -> if (f.isEnabled()) f.onTooltip(event) })
}
