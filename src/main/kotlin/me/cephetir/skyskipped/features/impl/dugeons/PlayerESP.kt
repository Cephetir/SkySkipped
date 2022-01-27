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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.OutlineUtils.outlineEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Team
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PlayerESP : Feature() {
    private val highlightedEntities = ArrayList<Entity>()

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        if (!Cache.isInDungeon || event.entity !is EntityPlayer || !Config.playerESP) return
        val player = event.entity as EntityPlayer
        if (player.team == null) return
        val scorePlayerTeam = player.team as ScorePlayerTeam
        if (scorePlayerTeam.nameTagVisibility == Team.EnumVisible.NEVER) return
        event.entity.isInvisible = false
        highlightedEntities.add(event.entity)
    }

    @SubscribeEvent
    fun onRenderEntityModel(event: RenderEntityModelEvent) {
        if (!Cache.isInDungeon || highlightedEntities.isEmpty() || !highlightedEntities.contains(event.entity) || !Config.playerESP)
            return
        outlineEntity(event)
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        highlightedEntities.clear()
    }
}