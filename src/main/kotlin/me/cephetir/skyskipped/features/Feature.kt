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

import me.cephetir.skyskipped.event.events.PacketReceive
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

abstract class Feature {
    val mc: Minecraft = Minecraft.getMinecraft()
    abstract fun isEnabled(): Boolean

    open fun onTick(event: TickEvent.ClientTickEvent) {}
    open fun onDrawBackground(event: GuiOpenEvent) {}
    open fun onChat(event: ClientChatReceivedEvent) {}
    open fun onRenderEntityModel(event: RenderEntityModelEvent) {}
    open fun onEntityJoinWorld(event: EntityJoinWorldEvent) {}
    open fun onWorldLoad(event: WorldEvent.Load) {}
    open fun onPacket(event: PacketReceive) {}
    open fun onPlayerInteract(event: PlayerInteractEvent) {}
    open fun onTooltip(event: ItemTooltipEvent) {}
    open fun onEntityDeath(event: LivingDeathEvent) {}
    open fun onWorldRender(event: RenderWorldLastEvent) {}
}