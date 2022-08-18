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
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils.findItemInHotbar
import me.cephetir.skyskipped.utils.KeybindUtils.isDown
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.skyblock.ScoreboardUtils
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Items
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


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
                try {
                    mode = DojoMode.valueOf(text.split("Challenge: ")[1])
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    onDisable()
                }
                break
            }
        }
        return if (!enabled) UChat.chat("§cSkySkipped §f:: §4Enter dojo area first!")
        else UChat.chat("§cSkySkipped §f:: §eAuto dojo §aenabled§e! Mode ${mode.name}.")
    }

    fun onDisable() {
        if (mode == DojoMode.Swiftness) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
        enabled = false
        mode = DojoMode.NONE
        target = null
        ignore.clear()
        return UChat.chat("§cSkySkipped §f:: §eAuto dojo §cdisabled§e!")
    }

    private var keybindLastState = false
    @SubscribeEvent
    protected fun onKey(event: ClientTickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || !Cache.inSkyblock) return

        val down = SkySkipped.autoDojo.isDown()
        if (down == keybindLastState) return
        keybindLastState = down
        if (!down) return

        if (enabled) onDisable()
        else onEnable()
    }

    @SubscribeEvent
    protected fun onChat(event: ClientChatReceivedEvent) {
        if (!enabled || mode == DojoMode.NONE) return
        val text = event.message.unformattedText.stripColor().keepScoreboardCharacters().trim()
        if (text.contains("CHALLENGE COMPLETED")) onDisable()
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
            else -> return
        }
    }

    // Swiftness mode
    @SubscribeEvent
    protected fun onTick(event: ClientTickEvent) {
        if (!enabled || mode != DojoMode.Swiftness) return

        val box = mc.thePlayer.collisionBoundingBox
        val adjustedBox = box.offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
        val sneak = mc.theWorld.checkBlockCollision(adjustedBox)

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, sneak)
    }

    enum class DojoMode {
        NONE,
        Discipline,
        Force,
        Swiftness
    }
}