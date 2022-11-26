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

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.KeybindUtils.isDown
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.RotationClass
import me.cephetir.skyskipped.utils.RotationUtils
import me.cephetir.skyskipped.utils.render.RenderUtils
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
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.concurrent.TimeUnit

/*
*   Huge Thanks to Sylvezar!
*/
class PresentHighlight : Feature() {
    private var clicked = mutableListOf<EntityArmorStand>()
    private var lastArmorStand: EntityArmorStand? = null

    private var auraToggle = false

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (mc.theWorld == null || !Config.presents || !Cache.inWorkshop) return
        val entities = mc.theWorld.loadedEntityList
        for (entity in entities)
            if (entity is EntityArmorStand && shouldDraw(entity) && !clicked.contains(entity))
                RenderUtils.drawBox(
                    entity.positionVector,
                    Config.presentsColor,
                    event.partialTicks
                )
    }

    private var timer = 0

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!auraToggle || mc.thePlayer == null || mc.theWorld == null || event.phase != TickEvent.Phase.START) return
        if (timer++ < 20) return
        timer = 0

        val entities = mc.theWorld.loadedEntityList
        entities.filterIsInstance<EntityArmorStand>().sortedBy { mc.thePlayer.getDistanceToEntity(it) }.find {
            shouldDraw(it) && !clicked.contains(it) && mc.thePlayer.getDistanceToEntity(it) <= 3.5f
        }?.let {
            printdev("Rotating...")
            val rotation = RotationUtils.getServerAngles(it)
            RotationClass(RotationClass.Rotation(rotation[0], rotation[1]), 300L)

            Multithreading.schedule({
                mc.playerController.interactWithEntitySendPacket(
                    mc.thePlayer,
                    it
                )
                printdev("CLICKED GIFT")
            }, 300L, TimeUnit.MILLISECONDS)

            clicked.add(it)
        } ?: printdev("cant find any gift")
    }

    private fun shouldDraw(armorstand: EntityArmorStand): Boolean {
        val helmet = armorstand.getEquipmentInSlot(4)
        if (helmet == null || helmet.item != Items.skull) return false

        if (armorstand.hasCustomName() &&
            armorstand.customNameTag.stripColor().contains("CLICK TO OPEN") ||
            armorstand.customNameTag.stripColor().contains("From: ")
        ) return true
        else if (helmet.hasTagCompound() &&
            helmet.tagCompound.toString()
                .contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBmNTM5ODUxMGIxYTA1YWZjNWIyMDFlYWQ4YmZjNTgzZTU3ZDcyMDJmNTE5M2IwYjc2MWZjYmQwYWUyIn19fQ")
        ) return true

        return false
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        clicked.clear()
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
    fun onPlayerUseItemEvent(e: PlayerUseItemEvent.Start) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) interact(mc.objectMouseOver.entityHit)
    }

    private fun interact(e: Entity) {
        if (e is EntityArmorStand && shouldDraw(e)) lastArmorStand = e
    }

    @SubscribeEvent
    fun onClientChatReceivedEvent(e: ClientChatReceivedEvent) {
        val msg = e.message.unformattedText.stripColor()
        if (msg.contains("GIFT! You found a White Gift!") || msg.contains("You have already found this Gift this year!"))
            if (lastArmorStand != null) {
                clicked.add(lastArmorStand!!)
                lastArmorStand = null
            }
    }

    private var keybindLastState = false

    @SubscribeEvent
    fun onInput(event: ClientTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val down = SkySkipped.giftAura.isDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        clicked.clear()
        auraToggle = !auraToggle
        UChat.chat("§cSkySkipped §f:: §eGift Aura ${if (auraToggle) "§aEnabled" else "§cDisabled"}!")
    }
}