/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped.features.impl.hacks

import gg.essential.universal.UChat
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.minecraft.KeybindUtils.isDown
import me.cephetir.bladecore.utils.minecraft.skyblock.ScoreboardUtils
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.event.events.PlayerAttackEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils.findItemInHotbar
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
        if (mc.thePlayer == null || mc.theWorld == null || !Cache.onSkyblock) return

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
    init {
        listener<PlayerAttackEvent> {
            if (!enabled || mode == DojoMode.NONE) return@listener
            when (mode) {
                DojoMode.Discipline -> {
                    if (it.target !is EntityZombie) return@listener
                    val helmet = it.target.getEquipmentInSlot(4) ?: return@listener printdev("helmet is null")

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
                    if (it.target !is EntityZombie) return@listener
                    val helmet = it.target.getEquipmentInSlot(4) ?: return@listener printdev("helmet is null")
                    if (helmet.item == Items.leather_helmet) it.cancel()
                }
                else -> return@listener
            }
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