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

package me.cephetir.skyskipped.features.impl.visual

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.init.Items
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.EntityInteractEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.PlayerUseItemEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/*
*   Huge Thanks to Sylvezar!
*/
class PresentHighlight : Feature() {
    private var clicked: MutableList<EntityArmorStand> = ArrayList()
    private var lastArmorStand: EntityArmorStand? = null

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (Minecraft.getMinecraft().theWorld == null || !Cache.inWorkshop || !Config.presents) return
        val entities: List<Entity> = Minecraft.getMinecraft().theWorld.loadedEntityList
        for (entity in entities) {
            if (entity is EntityArmorStand && shouldDraw(entity) && !clicked.contains(entity)) RenderUtils.drawBox(
                entity.positionVector,
                Config.presentsColor,
                event.partialTicks
            )
        }
    }

    private fun shouldDraw(armorstand: EntityArmorStand): Boolean {
        if (!armorstand.isSmall && armorstand.isInvisible) {
            val helmet = armorstand.getEquipmentInSlot(4)
            if (helmet == null || helmet.item != Items.skull) return false
            if (armorstand.hasCustomName() && armorstand.customNameTag.contains("CLICK TO OPEN")) return true
            else if (helmet.hasTagCompound()) {
                if (helmet.tagCompound.toString().contains(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBmNTM5ODUxMGIxYTA1YWZjNWIyMDFlYWQ4YmZjNTgzZTU3ZDcyMDJmNTE5M2IwYjc2MWZjYmQwYWUyIn19fQ"
                    )
                ) return true
            }
        }
        return false
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        clicked = ArrayList()
        lastArmorStand = null
    }

    @SubscribeEvent
    fun onAttackEntityEvent(e: AttackEntityEvent) = interact(e.target)

    @SubscribeEvent
    fun onPlayerEvent(e: PlayerInteractEvent) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) interact(mc.objectMouseOver.entityHit)
    }

    @SubscribeEvent
    fun onEntityInteractEvent(e: EntityInteractEvent) = interact(e.target)

    @SubscribeEvent
    fun onPlayerUseItemEvent(e: PlayerUseItemEvent) {
        if (mc.objectMouseOver.entityHit != null) interact(mc.objectMouseOver.entityHit)
    }

    private fun interact(e: Entity) {
        if (e is EntityArmorStand && shouldDraw(e)) lastArmorStand = e
    }

    @SubscribeEvent
    fun onClientChatReceivedEvent(e: ClientChatReceivedEvent) {
        if (e.message.unformattedText.contains("GIFT! You found a White Gift!") || e.message.unformattedText.contains("You have already found this Gift this year!")) {
            if (lastArmorStand != null) {
                clicked.add(lastArmorStand!!)
                lastArmorStand = null
            }
        }
    }
}