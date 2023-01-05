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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.safeListener
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinRendererLivingEntity
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
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent


class ESP : Feature() {
    private val starredMobs = hashMapOf<Entity, EntityLivingBase>()
    private val customMobs = hashMapOf<Entity, EntityLivingBase>()
    private val drawBox = hashMapOf<Entity, Int>()

    init {
        listener<RenderWorldLastEvent> {
            if (!Config.esp || drawBox.isEmpty()) return@listener

            for ((entity, color) in drawBox)
                RenderUtils.renderBoundingBox(entity, color)

            drawBox.clear()
        }

        safeListener<RenderEntityModelEvent> {
            if (!Config.esp) return@safeListener

            if (Config.customesp)
                if (it.entity is EntityArmorStand) {
                    if (!it.entity.hasCustomName()) return@safeListener
                    val name = it.entity.customNameTag.stripColor()
                    for (cname in Config.customespText.split(", ")) {
                        if (!name.contains(cname, true)) continue

                        val mob = customMobs[it.entity]
                        if (mob != null) {
                            if (mob.isDead()) {
                                customMobs.remove(it.entity)
                                break
                            }
                            val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                            drawEsp(
                                mob,
                                model,
                                if (Config.customespChroma) RenderUtils.getChroma(3000f, 0) else Config.customespColor.rgb,
                                it.partialTicks,
                                it
                            )
                            break
                        } else getMobsWithinAABB(it.entity)
                        break
                    }
                } else for (cname in Config.customespText.split(", ")) {
                    if (it.entity.name?.contains(cname, true) == false) continue
                    drawEsp(
                        it.entity,
                        it.model,
                        if (Config.customespChroma) RenderUtils.getChroma(3000f, 0) else Config.customespColor.rgb,
                        it.partialTicks,
                        it
                    )
                    break
                }

            if (!Cache.inDungeon) return@safeListener
            var maxHP: Float
            when (it.entity) {
                is EntityArmorStand -> {
                    if (!it.entity.hasCustomName()) return@safeListener
                    val name = it.entity.customNameTag.stripColor()
                    if (Config.starredmobsesp && name.startsWith("✯ ")) {
                        val mob = starredMobs[it.entity]
                        if (mob != null) {
                            if (mob.isDead()) {
                                starredMobs.remove(it.entity)
                                return@safeListener
                            }
                            val model = (mc.renderManager.getEntityRenderObject<EntityLivingBase>(mob) as IMixinRendererLivingEntity).mainModel
                            drawEsp(
                                mob,
                                model,
                                if (Config.starredmobsespChroma) RenderUtils.getChroma(3000f, 0) else Config.mobsespColor.rgb,
                                it.partialTicks,
                                it
                            )
                        } else getStarredMobsWithinAABB(it.entity)
                    } else if (Config.keyesp && (name == "Wither Key" || name == "Blood Key"))
                        RenderUtils.drawBeaconBeam(it.entity, if (Config.keyespChroma) RenderUtils.getChroma(3000f, 0) else Config.keyespColor.rgb)
                }

                is EntityOtherPlayerMP ->
                    if (Config.starredmobsesp && it.entity.name?.trim() == "Shadow Assassin")
                        drawEsp(
                            it.entity,
                            it.model,
                            if (Config.starredmobsespChroma) RenderUtils.getChroma(3000f, 0) else Config.mobsespColor.rgb,
                            it.partialTicks,
                            it
                        )
                    else if (Config.playeresp && !it.entity.isDead && it.entity.team != null && (it.entity.team as ScorePlayerTeam).nameTagVisibility != Team.EnumVisible.NEVER)
                        drawEsp(
                            it.entity,
                            it.model,
                            if (Config.playerespChroma) RenderUtils.getChroma(3000f, 0) else Config.playersespColor.rgb,
                            it.partialTicks,
                            it
                        )

                is EntityEnderman ->
                    if (it.entity.isInvisible)
                        it.entity.isInvisible = false

                is EntityBat -> if (Config.batsesp && (it.entity.maxHealth.also { hp -> maxHP = hp } == 100.0f || maxHP == 200.0f)) {
                    RenderUtils.drawChamsEsp(
                        it.entity,
                        it.model,
                        if (Config.batsespChroma) RenderUtils.getChroma(3000f, 0) else Config.batsespColor.rgb,
                        it.partialTicks
                    )
                    it.cancel()
                }
            }
        }

        listener<EntityJoinWorldEvent>(-1, receiveCanceled = true) {
            if (!Cache.inDungeon || !Config.esp) return@listener
            if ((it.entity is EntityOtherPlayerMP && it.entity.name?.trim() == "Shadow Assassin") || it.entity is EntityEnderman)
                it.entity.isInvisible = false
        }

        listener<WorldEvent.Load> {
            starredMobs.clear()
            customMobs.clear()
            drawBox.clear()
        }

        listener<LivingDeathEvent> {
            if (!Config.esp) return@listener
            customMobs.remove(it.entity)
            if (!Cache.inDungeon) return@listener
            starredMobs.remove(it.entity)
        }
    }

    private fun drawEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float, event: RenderEntityModelEvent) {
        when (Config.espMode) {
            0 -> RenderUtils.drawOutlinedEsp(entity, model, color, partialTicks)
            1 -> drawBox[entity] = color
            2 -> {
                event.cancel()
                RenderUtils.drawChamsEsp(entity, model, color, partialTicks)
            }
        }
    }

    private fun getMobsWithinAABB(entity: Entity) {
        val aabb = AxisAlignedBB(entity.posX + 0.4, entity.posY - 2.0, entity.posZ + 0.4, entity.posX - 0.4, entity.posY + 0.2, entity.posZ - 0.4)
        val i = MathHelper.floor_double((aabb.minX - 1.0)) shr 4
        val j = MathHelper.floor_double((aabb.maxX + 1.0)) shr 4
        val k = MathHelper.floor_double((aabb.minZ - 1.0)) shr 4
        val l = MathHelper.floor_double((aabb.maxZ + 1.0)) shr 4
        for (i1 in i..j)
            for (j1 in k..l)
                this.getMobsWithinAABBForEntity(mc.theWorld.getChunkFromChunkCoords(i1, j1), entity, aabb)
    }

    private fun getMobsWithinAABBForEntity(chunk: Chunk, entityIn: Entity, aabb: AxisAlignedBB) {
        val entityLists = chunk.entityLists
        var i = MathHelper.floor_double(((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0))
        var j = MathHelper.floor_double(((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0))
        i = MathHelper.clamp_int(i, 0, (entityLists.size - 1))
        j = MathHelper.clamp_int(j, 0, (entityLists.size - 1))
        for (k in i..j) {
            if (entityLists[k].isEmpty()) continue
            entity@ for (e in entityLists[k]) {
                if (!e.entityBoundingBox.intersectsWith(aabb)) continue@entity
                if (e !is EntityMob || e.health <= 0.0f || e.isInvisible) continue@entity
                customMobs[entityIn] = (e as EntityLivingBase)
            }
        }
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
                        if (e.health <= 0.0f || e.getName() == null) continue@entity
                        when (e.getName().trim()) {
                            "Lost Adventurer", "Diamond Guy" -> starredMobs[entityIn] = (e as EntityLivingBase)
                            else -> {
                                if (e.isInvisible() || e.getUniqueID().version() != 2) continue@entity
                                starredMobs[entityIn] = (e as EntityLivingBase)
                            }
                        }
                    }

                    is EntitySkeleton, is EntityZombie -> {
                        if ((e as EntityMob).health <= 0.0f || e.isInvisible) continue@entity
                        starredMobs[entityIn] = (e as EntityLivingBase)
                    }

                    is EntityEnderman -> {
                        if (e.health <= 0.0f) continue@entity
                        starredMobs[entityIn] = (e as EntityLivingBase)
                    }
                }
            }
        }
    }

    private fun EntityLivingBase.isDead() = this.isDead || this.maxHealth <= 0f
}