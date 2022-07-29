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
import me.cephetir.skyskipped.mixins.IMixinRendererLivingEntity
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityBat
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Team
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class ESP : Feature() {
    private val starredmobs = hashMapOf<Entity, EntityType>()

    @SubscribeEvent
    fun onRender(event: RenderEntityModelEvent) {
        if (!Config.esp || !Cache.inDungeon) return

        var maxHP: Float
        when (event.entity) {
            is EntityArmorStand -> {
                if (!event.entity.hasCustomName()) return
                val name = event.entity.customNameTag.stripColor()
                if (Config.starredmobsesp && name.startsWith("✯ ")) {
                    val mob = starredmobs[event.entity]
                    if (mob != null) {
                        if (mob.isDead()) {
                            starredmobs.remove(event.entity)
                            return
                        }
                        printdev("rendering $name in coords ${mob.entity.posX} ${mob.entity.posY}")
                        val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob.entity) as IMixinRendererLivingEntity).mainModel
                        drawEsp(
                            mob.entity,
                            model,
                            if (Config.starredmobsespChroma) RenderUtils.getChroma(3000f, 0) else Config.mobsespColor.rgb,
                            event.partialTicks,
                            event
                        )
                    } else getStarredMobsWithinAABB(event.entity)
                } else if (Config.keyesp && (name == "Wither Key" || name == "Blood Key")) {
                    printdev("rendering key")
                    RenderUtils.renderKeyBoundingBox(event.entity, if (Config.keyespChroma) RenderUtils.getChroma(3000f, 0) else Config.keyespColor.rgb)
                }
            }

            is EntityOtherPlayerMP ->
                if (Config.starredmobsesp && event.entity.name.trim() == "Shadow Assassin")
                    drawEsp(
                        event.entity,
                        event.model,
                        if (Config.starredmobsespChroma) RenderUtils.getChroma(3000f, 0) else Config.mobsespColor.rgb,
                        event.partialTicks,
                        event
                    )
                else if (Config.playeresp && !event.entity.isDead && event.entity != mc.thePlayer && event.entity.team != null && (event.entity.team as ScorePlayerTeam).nameTagVisibility != Team.EnumVisible.NEVER)
                    drawEsp(
                        event.entity,
                        event.model,
                        if (Config.playerespChroma) RenderUtils.getChroma(3000f, 0) else Config.playersespColor.rgb,
                        event.partialTicks,
                        event
                    )

            is EntityEnderman ->
                if (event.entity.isInvisible)
                    event.entity.isInvisible = false

            is EntityBat -> if (Config.batsesp && (event.entity.maxHealth.also { maxHP = it } == 100.0f || maxHP == 200.0f)) {
                RenderUtils.drawChamsEsp(
                    event.entity,
                    event.model,
                    if (Config.batsespChroma) RenderUtils.getChroma(3000f, 0) else Config.batsespColor.rgb,
                    event.partialTicks
                )
                event.isCanceled = true
            }
        }
    }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float, event: RenderEntityModelEvent) {
        when (Config.espMode) {
            0 -> RenderUtils.drawOutlinedEsp(entity, model, color, partialTicks)
            1 -> RenderUtils.renderBoundingBox(entity, color)
            2 -> {
                RenderUtils.drawChamsEsp(entity, model, color, partialTicks)
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onEntityJoin(event: EntityJoinWorldEvent) {
        if (!Cache.inDungeon || !Config.esp) return
        if (event.entity is EntityOtherPlayerMP && event.entity.name.trim() == "Shadow Assassin")
            event.entity.isInvisible = false
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        starredmobs.clear()
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (!Config.esp || !Cache.inDungeon) return
        starredmobs.remove(event.entity)
    }

    private fun getStarredMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double((aabb.minX - 1.0)) shr 4
        val j = MathHelper.floor_double((aabb.maxX + 1.0)) shr 4
        val k = MathHelper.floor_double((aabb.minZ - 1.0)) shr 4
        val l = MathHelper.floor_double((aabb.maxZ + 1.0)) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                this.getStarredMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
    }

    private fun getStarredMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double(((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0))
        var j = MathHelper.floor_double(((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0))
        i = MathHelper.clamp_int(i, 0, (entityLists.size - 1))
        j = MathHelper.clamp_int(j, 0, (entityLists.size - 1))
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                when (e) {
                    is EntityOtherPlayerMP -> {
                        if (e.health <= 0.0f) continue@entity
                        printdev("ADDING ${e.getName().trim()}")
                        when (e.getName().trim()) {
                            "Lost Adventurer", "Diamond Guy" -> starredmobs[entityIn] = EntityType((e as EntityLivingBase), 2)
                            else -> {
                                if (e.isInvisible() || e.getUniqueID().version() != 2) continue@entity
                                starredmobs[entityIn] = EntityType((e as EntityLivingBase), 0)
                            }
                        }
                    }

                    is EntitySkeleton, is EntityZombie -> {
                        if ((e as EntityMob).health <= 0.0f || e.isInvisible) continue@entity
                        printdev("ADDING STARRED MOB")
                        starredmobs[entityIn] = EntityType((e as EntityLivingBase), 0)
                    }

                    is EntityEnderman -> {
                        if (e.health <= 0.0f) continue@entity
                        printdev("ADDING ENDERMAN")
                        starredmobs[entityIn] = EntityType((e as EntityLivingBase), 1)
                    }
                }
            }
        }
    }

    private data class EntityType(val entity: EntityLivingBase, val type: Int) {
        fun isDead() = entity.isDead || entity.maxHealth <= 0f
    }
}