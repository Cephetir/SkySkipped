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
import me.cephetir.skyskipped.mixins.IMixinMinecraft
import me.cephetir.skyskipped.mixins.IMixinRenderManager
import me.cephetir.skyskipped.utils.render.RenderUtils
import me.cephetir.skyskipped.utils.render.RenderUtils.getChroma
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

        for (e in starredmobs) {
            val color = if (Config.chromaMode) getChroma(3000.0F, 0) else Config.mobsespColor.rgb
            RenderUtils.renderStarredMobBoundingBox(e, event.partialTicks, color)
            drawTracer(e, color)
        }

        for (e in players) {
            val color = if (Config.chromaMode) getChroma(3000.0F, 0) else Config.playersespColor.rgb
            RenderUtils.renderMiniBoundingBox(e, event.partialTicks, color)
            drawTracer(e, color)
        }

        for (e in bats) {
            val color = if (Config.chromaMode) getChroma(3000.0F, 0) else Config.batsespColor.rgb
            RenderUtils.renderBoundingBox(e, event.partialTicks, color)
            drawTracer(e, color)
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.esp || !Cache.isInDungeon) return
        starredmobs.clear()
        players.clear()
        bats.clear()
        for (e in mc.theWorld.loadedEntityList) {
            if (Config.starredmobsesp && e is EntityArmorStand && !e.isDead && e.getCustomNameTag().contains("✯"))
                starredmobs.add(e)
            else if (Config.playeresp && e is EntityPlayer && !e.isDead && e != mc.thePlayer && e.team != null && (e.team as ScorePlayerTeam).nameTagVisibility != Team.EnumVisible.NEVER)
                players.add(e)
            else if (Config.batsesp && e is EntityBat && !e.isDead)
                bats.add(e)
        }
    }

    private fun drawTracer(entity: Entity, color: Int) {
        if (!Config.espTracers) return
        val xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * (mc as IMixinMinecraft).timer.renderPartialTicks - (mc.renderManager as IMixinRenderManager).renderPosX)
        val yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * (mc as IMixinMinecraft).timer.renderPartialTicks - (mc.renderManager as IMixinRenderManager).renderPosY)
        val zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * (mc as IMixinMinecraft).timer.renderPartialTicks - (mc.renderManager as IMixinRenderManager).renderPosZ)

        RenderUtils.drawTracerLine(xPos, yPos, zPos, color, 1.2f)
    }
}