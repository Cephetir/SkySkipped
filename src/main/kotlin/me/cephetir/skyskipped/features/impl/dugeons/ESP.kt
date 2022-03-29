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
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.RenderUtils
import me.cephetir.skyskipped.utils.RenderUtils.getChroma
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Team
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ESP : Feature() {
    private val starredmobs = mutableListOf<Entity>()
    private val players = mutableListOf<Entity>()
    private val bats = mutableListOf<Entity>()

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Config.esp || !Cache.isInDungeon) return

        for (e in starredmobs) RenderUtils.renderStarredMobBoundingBox(e, event.partialTicks)

        for (e in players) RenderUtils.renderMiniBoundingBox(
            e,
            event.partialTicks,
            if (Config.chromaMode) getChroma(3000.0F, 0) else Config.playersespColor.rgb
        )

        for (e in bats) RenderUtils.renderBoundingBox(
            e,
            event.partialTicks,
            if (Config.chromaMode) getChroma(3000.0F, 0) else Config.batsespColor.rgb
        )
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.esp || !Cache.isInDungeon) return
        starredmobs.clear()
        players.clear()
        bats.clear()
        val iterator = mc.theWorld.loadedEntityList.iterator()
        while (iterator.hasNext()) {
            val e = iterator.next() as Entity
            if (Config.starredmobsesp && e is EntityArmorStand && !e.isDead && e.getCustomNameTag().contains("✯"))
                starredmobs.add(e)
            else if (Config.playeresp && e is EntityPlayer && !e.isDead && e != mc.thePlayer && e.team != null && (e.team as ScorePlayerTeam).nameTagVisibility != Team.EnumVisible.NEVER)
                players.add(e)
            else if (Config.batsesp && e is EntityBat && !e.isDead)
                bats.add(e)
        }
    }
}