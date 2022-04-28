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

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.event.events.PlayerAttackEvent
import me.cephetir.skyskipped.event.events.UpdateWalkingPlayerEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.IMixinEntityPlayerSP
import me.cephetir.skyskipped.utils.RotationUtils
import me.cephetir.skyskipped.utils.ScoreboardUtils
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard
import kotlin.math.abs

open class AutoDojo : Feature() {
    private var enabled = false
    private var mode = DojoMode.NONE

    private var target: Entity? = null
    private val ignore = mutableListOf<Entity>()

    fun onEnable() {
        for (score in ScoreboardUtils.sidebarLines) {
            val text = score.stripColor().keepScoreboardCharacters()
            if (text.stripColor().contains("Challenge: ")) {
                printdev(text)
                enabled = true
                when (text.split("Challenge: ")[1]) {
                    "Discipline" -> mode = DojoMode.Discipline
                    "Force" -> mode = DojoMode.Force
                    else -> enabled = false
                }
                break
            }
        }
        return if (!enabled) UChat.chat("§cSkySkipped §f:: §4Enter dojo area first!")
        else UChat.chat("§cSkySkipped §f:: §eAuto dojo §aenabled§e! Mode ${mode.name}.")
    }

    fun onDisable() {
        enabled = false
        mode = DojoMode.NONE
        target = null
        ignore.clear()
        return UChat.chat("§cSkySkipped §f:: §eAuto dojo §cdisabled§e!")
    }

    @SubscribeEvent
    protected fun onKey(event: InputEvent.KeyInputEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || !Cache.inSkyblock) return
        if (!Keyboard.getEventKeyState()) return
        if (Keyboard.getEventKey() != SkySkipped.autoDojo.keyCode) return

        if (enabled) onDisable()
        else onEnable()
    }

    @SubscribeEvent
    protected fun onChat(event: ClientChatReceivedEvent) {
        if (!enabled || mode == DojoMode.NONE) return
        val text = event.message.unformattedText.stripColor().keepScoreboardCharacters().trim()
        if(text.contains("CHALLENGE COMPLETED")) onDisable()
    }

    // Discipline Mode
    @SubscribeEvent
    protected fun onMovePre(event: UpdateWalkingPlayerEvent.Pre) {
        if (!enabled || mode != DojoMode.Discipline) return
        target = getTarget()
        if (target != null) {
            val angles = RotationUtils.getServerAngles(target!!)
            val yaw = (mc.thePlayer as IMixinEntityPlayerSP).lastReportedYaw -
                    MathHelper.wrapAngleTo180_float((mc.thePlayer as IMixinEntityPlayerSP).lastReportedYaw - angles[0])
            val pitch = (mc.thePlayer as IMixinEntityPlayerSP).lastReportedPitch -
                    MathHelper.wrapAngleTo180_float((mc.thePlayer as IMixinEntityPlayerSP).lastReportedPitch - angles[1])

            event.yaw = yaw
            event.pitch = pitch
        }
    }

    // Discipline Mode
    @SubscribeEvent
    protected fun onMovePost(event: UpdateWalkingPlayerEvent.Post) {
        if (!enabled || mode != DojoMode.Discipline) return
        if (target != null && mc.thePlayer.ticksExisted % 2 == 0) {
            updateItemNoEvent()
            if (mc.thePlayer.getDistanceToEntity(target) < 3.8) {
                if (mc.thePlayer.isUsingItem) mc.playerController.onStoppedUsingItem(mc.thePlayer)
                val angles = RotationUtils.getServerAngles(target!!)
                if (abs((mc.thePlayer as IMixinEntityPlayerSP).lastReportedPitch - angles[1]) < 25.0f && abs((mc.thePlayer as IMixinEntityPlayerSP).lastReportedYaw - angles[0]) < 15.0f) {
                    mc.thePlayer.swingItem()
                    mc.playerController.attackEntity(
                        mc.thePlayer,
                        target
                    )
                    printdev("Hitting zombie ${mc.thePlayer.getDistanceToEntity(target)}")
                    ignore.add(target!!)
                    target = null
                }
            }
        }
    }

    // Discipline and Force Mode
    @SubscribeEvent
    protected fun onAttack(event: PlayerAttackEvent) {
        if (!enabled || mode == DojoMode.NONE) return
        when (mode) {
            DojoMode.Discipline -> {
                if (event.target !is EntityZombie) return
                val helmet = event.target.getEquipmentInSlot(4) ?: return printdev("helmet is null")

                when (helmet.item) {
                    Items.leather_helmet -> {
                        var sword = findItemInHotbar("Wooden Sword")
                        if (sword == -1) sword = 0
                        mc.thePlayer.inventory.currentItem = sword
                    }
                    Items.iron_helmet -> {
                        var sword = findItemInHotbar("Iron Sword")
                        if (sword == -1) sword = 1
                        mc.thePlayer.inventory.currentItem = sword
                    }
                    Items.golden_helmet -> {
                        var sword = findItemInHotbar("Golden Sword")
                        if (sword == -1) sword = 2
                        mc.thePlayer.inventory.currentItem = sword
                    }
                    Items.diamond_helmet -> {
                        var sword = findItemInHotbar("Diamond Sword")
                        if (sword == -1) sword = 3
                        mc.thePlayer.inventory.currentItem = sword
                    }
                }
            }
            DojoMode.Force -> {
                if (event.target !is EntityZombie) return
                val helmet = event.target.getEquipmentInSlot(4) ?: return printdev("helmet is null")
                if (helmet.item == Items.leather_helmet) event.isCanceled = true
            }
            DojoMode.NONE -> return
        }
    }

    private fun findItemInHotbar(name: String): Int {
        val inv = mc.thePlayer.inventory
        for (i in 0..8) {
            val curStack = inv.getStackInSlot(i)
            if (curStack != null && curStack.displayName.contains(name))
                return i
        }
        return -1
    }

    private fun updateItemNoEvent() =
        mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem) as Packet<*>)

    private fun getTarget(): Entity? {
        val entities = mc.theWorld.loadedEntityList.filter { it is EntityZombie && !ignore.contains(it) }
            .sortedBy { mc.thePlayer.getDistanceToEntity(it) }
        if (entities.isEmpty()) return null
        return entities.first()
    }

    enum class DojoMode {
        NONE,
        Discipline,
        Force
    }
}