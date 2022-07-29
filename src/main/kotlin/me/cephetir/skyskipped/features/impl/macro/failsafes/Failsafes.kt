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

package me.cephetir.skyskipped.features.impl.macro.failsafes

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.impl.macro.Macro
import me.cephetir.skyskipped.utils.RotationClass
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.BlockPos
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object Failsafes {
    private val mc = Minecraft.getMinecraft()

    // Warp Back
    var warpTimer = 0L

    // Stuck
    var stuckSteps = StuckSteps.SETUP
    var lastPos: BlockPos? = null
    var ticksStuck = 0
    var keyState = true
    var keyTimer = 0L

    // Desynced
    var desyncedSteps = DesyncedSteps.SETUP
    var ticksDesync = 0
    var ticksWarpDesync = 0
    var startCount = -1L
    var lastCount = -1L
    var desyncWaitTimer = 0L
    var desyncF = true

    // Full Inv
    var clearInvSteps = ClearInvSteps.SETUP
    var fullInvTicks = 0
    var clearInvTimer = 0L
    var openedInv = 0
    var toClearInv: MutableList<Slot>? = null

    // Bedrock cage
    var bedrockTimer = 0L
    var rotating: RotationClass? = null
    private val messages =
        listOf("huh?", "what is it", "what happened?", "wtf", "wtf???", "lmao", "LMAO", "lamo", "wft", "hello?", "help", "wth is that", "lol")

    fun warpBack(getBack: Runnable) {
        unpressKeys()
        if (System.currentTimeMillis() - warpTimer < 3000L) return
        warpTimer = System.currentTimeMillis()

        if (Cache.onIsland) getBack.run()
        else if (Cache.inSkyblock) mc.thePlayer.sendChatMessage("/is")
        else if (!Cache.inSkyblock) {
            mc.thePlayer.sendChatMessage("/l")
            Multithreading.schedule({ mc.thePlayer.sendChatMessage("/play sb") }, 1500L, TimeUnit.MILLISECONDS)
        }
    }

    fun stuck(getBack: Runnable) {
        when (stuckSteps) {
            StuckSteps.SETUP -> {
                UChat.chat("§cSkySkipped §f:: §eYou got stuck! Trying to prevent that...")
                Macro.sendWebhook("Unstuck failsafe", "You got stuck! Trying to prevent that...", false)
                unpressKeys()
                stuckSteps = StuckSteps.BACK
                keyTimer = System.currentTimeMillis()
                keyState = true
            }
            StuckSteps.BACK -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, keyState)
                if (System.currentTimeMillis() - keyTimer >= 300) {
                    if (keyState) keyState = false
                    else {
                        keyState = true
                        stuckSteps = StuckSteps.FORWARD
                    }
                    keyTimer = System.currentTimeMillis()
                }
            }
            StuckSteps.FORWARD -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, keyState)
                if (System.currentTimeMillis() - keyTimer >= 300) {
                    if (keyState) keyState = false
                    else {
                        keyState = true
                        stuckSteps = StuckSteps.LEFT
                    }
                    keyTimer = System.currentTimeMillis()
                }
            }
            StuckSteps.LEFT -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, keyState)
                if (System.currentTimeMillis() - keyTimer >= 300) {
                    if (keyState) keyState = false
                    else {
                        keyState = true
                        stuckSteps = StuckSteps.RIGHT
                    }
                    keyTimer = System.currentTimeMillis()
                }
            }
            StuckSteps.RIGHT -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, keyState)
                if (System.currentTimeMillis() - keyTimer >= 300) {
                    if (keyState) keyState = false
                    else {
                        keyState = true
                        stuckSteps = StuckSteps.END
                    }
                    keyTimer = System.currentTimeMillis()
                }
            }
            StuckSteps.END -> {
                unpressKeys()
                getBack.run()
                ticksStuck = 0
                stuckSteps = StuckSteps.SETUP
            }
        }
    }

    fun desynced(setSpawn: Boolean, getBack: Runnable) {
        when (desyncedSteps) {
            DesyncedSteps.SETUP -> {
                if (desyncF) {
                    UChat.chat("§cSkySkipped §f:: §eDesync detected! Swapping lobbies...")
                    Macro.sendWebhook("Desync failsafe", "Desync detected! Swapping lobbies...", false)
                    unpressKeys()
                    if (setSpawn) mc.thePlayer.sendChatMessage("/sethome")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 2000L) {
                    desyncedSteps = DesyncedSteps.WARP_TO_LOBBY
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.WARP_TO_LOBBY -> {
                if (desyncF) {
                    unpressKeys()
                    mc.thePlayer.sendChatMessage("/hub")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 10000L) {
                    desyncedSteps = DesyncedSteps.WARP_BACK
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.WARP_BACK -> {
                if (desyncF) {
                    unpressKeys()
                    mc.thePlayer.sendChatMessage("/is")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 5000L) {
                    desyncedSteps = DesyncedSteps.END
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.END -> {
                getBack.run()
                ticksDesync = 0
                ticksWarpDesync = 100
            }
        }
    }

    fun clearInv(getBack: Runnable) {
        unpressKeys()
        when (clearInvSteps) {
            ClearInvSteps.SETUP -> {
                UChat.chat("§cSkySkipped §f:: §eInventory is full! Cleaning...")
                Macro.sendWebhook("Full Inventory failsafe", "Inventory is full! Cleaning...", false)
                clearInvSteps = ClearInvSteps.OPEN_INV
            }
            ClearInvSteps.OPEN_INV -> {
                if (openedInv == 0) {
                    mc.displayGuiScreen(GuiInventory(mc.thePlayer))
                    clearInvTimer = System.currentTimeMillis()
                    openedInv = 1
                }

                if (System.currentTimeMillis() - clearInvTimer >= 1000L) {
                    if (checkInv()) {
                        clearInvSteps = ClearInvSteps.END
                        return
                    }
                    clearInvSteps = ClearInvSteps.CLEAR_STONE
                    clearInvTimer = System.currentTimeMillis()
                }
            }
            ClearInvSteps.CLEAR_STONE -> {
                if (toClearInv == null) {
                    val inv = ((mc.currentScreen as GuiInventory).inventorySlots as ContainerPlayer).inventorySlots
                    val stoneSlots = inv.filter {
                        it.hasStack && it.stack.displayName.stripColor().keepScoreboardCharacters().contains("Stone", true)
                    }
                    toClearInv = stoneSlots.toMutableList()
                    return
                } else {
                    if (toClearInv!!.isEmpty()) {
                        toClearInv = null
                        clearInvSteps = ClearInvSteps.CLEAR_RES
                        return
                    }
                    if (checkInv()) {
                        clearInvSteps = ClearInvSteps.END
                        return
                    }
                    if (System.currentTimeMillis() - clearInvTimer < 500L) return
                    clearInvTimer = System.currentTimeMillis()
                    val slot = toClearInv!![0]
                    toClearInv!!.removeAt(0)

                    mc.playerController.windowClick(
                        (mc.currentScreen as GuiInventory).inventorySlots.windowId,
                        slot.slotNumber, 0, 0, mc.thePlayer
                    )
                    mc.playerController.windowClick(
                        (mc.currentScreen as GuiInventory).inventorySlots.windowId,
                        -999, 0, 0, mc.thePlayer
                    )
                }
            }
            ClearInvSteps.CLEAR_RES -> {
                if (toClearInv == null) {
                    val inv = ((mc.currentScreen as GuiInventory).inventorySlots as ContainerPlayer).inventorySlots
                    val crops = inv.filter {
                        it.hasStack && when (it.stack.item) {
                            Items.nether_wart -> true
                            Items.reeds -> true
                            Items.potato -> true
                            Items.carrot -> true
                            Items.melon -> true
                            else -> it.stack.displayName.contains("mushroom", true) ||
                                    it.stack.displayName.contains("wart", true) ||
                                    it.stack.displayName.contains("enchanted", true)
                        }
                    }
                    toClearInv = crops.toMutableList()
                    openedInv = 0
                    return
                } else {
                    if (toClearInv!!.isEmpty()) {
                        clearInvSteps = ClearInvSteps.END
                        return
                    }

                    when (openedInv) {
                        0 -> {
                            mc.thePlayer.closeScreen()
                            mc.thePlayer.sendChatMessage("/sbmenu")
                            openedInv = 1
                            clearInvTimer = System.currentTimeMillis()
                            return
                        }
                        1 -> {
                            if (System.currentTimeMillis() - clearInvTimer >= 2000L) {
                                clearInvSteps = ClearInvSteps.END
                                return
                            }

                            if (mc.currentScreen !is GuiChest) return
                            val trades = (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.find {
                                it.hasStack && it.stack.displayName.stripColor() == "Trades"
                            } ?: return

                            mc.playerController.windowClick(
                                (mc.currentScreen as GuiChest).inventorySlots.windowId,
                                trades.slotIndex, 0, 0, mc.thePlayer
                            )
                            openedInv = 2
                            clearInvTimer = System.currentTimeMillis()
                            return
                        }
                        2 -> {
                            if (System.currentTimeMillis() - clearInvTimer >= 2000L) {
                                clearInvSteps = ClearInvSteps.END
                                return
                            }

                            if (mc.currentScreen !is GuiChest) return
                            (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.find {
                                it.hasStack && it.stack.item.unlocalizedName.contains("hopper")
                            } ?: return
                            clearInvTimer = System.currentTimeMillis()
                        }
                    }

                    if (mc.currentScreen !is GuiChest) {
                        clearInvSteps = ClearInvSteps.END
                        return
                    }
                    if (System.currentTimeMillis() - clearInvTimer < 500L) return
                    clearInvTimer = System.currentTimeMillis()
                    val slot = toClearInv!![0]
                    toClearInv!!.removeAt(0)

                    mc.playerController.windowClick(
                        (mc.currentScreen as GuiChest).inventorySlots.windowId,
                        slot.slotNumber, 0, 0, mc.thePlayer
                    )
                }
            }
            ClearInvSteps.END -> {
                getBack.run()
                clearInvSteps = ClearInvSteps.SETUP
                fullInvTicks = 0
                toClearInv = null
                openedInv = 0
            }
        }
    }

    private fun checkInv(): Boolean {
        if (mc.currentScreen !is GuiInventory) {
            mc.thePlayer.closeScreen()
            return true
        }
        return false
    }

    fun bedrockCage(getBack: Runnable) {
        if (System.currentTimeMillis() - bedrockTimer < 5000L) return
        unpressKeys()

        if (rotating == null) {
            val yaw = getRandom(-180f, 180f)
            val pitch = getRandom(-45f, 75f)
            rotating = RotationClass(RotationClass.Rotation(yaw, pitch), 1000L + getRandom(500f, 1000f).roundToLong())
        } else if (rotating!!.done) {
            bedrockTimer = System.currentTimeMillis() + getRandom(1000f, 3000f).roundToLong()
            rotating = null
        }

        if (getRandom(1f, 50f) == 25f) mc.thePlayer.sendChatMessage(messages[getRandom(0f, messages.size - 1f).roundToInt()])
        if (!checkBedrock()) {
            Macro.sendWebhook("Lucky Escape!", "You successfully left bedrock cage!", true)
            getBack.run()
        }
    }

    private fun checkBedrock(): Boolean {
        val pos1 = mc.thePlayer.position.add(-2, -2, -2)
        val pos2 = mc.thePlayer.position.add(2, 2, 2)

        for (pos in BlockPos.getAllInBox(pos1, pos2))
            if (mc.theWorld.getBlockState(pos).block == Blocks.bedrock)
                return true

        return false
    }

    private val random = Random()
    private fun getRandom(a: Float, b: Float): Float = a + (b - a) * random.nextFloat()

    private fun unpressKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
    }

    fun reset() {
        ticksDesync = 0
        ticksWarpDesync = 0
        startCount = -1L
        lastCount = -1L
        desyncWaitTimer = 0L
        desyncF = true

        warpTimer = 0L

        fullInvTicks = 0
        clearInvTimer = 0L
        openedInv = 0
        toClearInv = null

        bedrockTimer = 0L
    }

    enum class StuckSteps {
        SETUP,
        BACK,
        FORWARD,
        LEFT,
        RIGHT,
        END
    }

    enum class DesyncedSteps {
        SETUP,
        WARP_TO_LOBBY,
        WARP_BACK,
        END
    }

    enum class ClearInvSteps {
        SETUP,
        OPEN_INV,
        CLEAR_STONE,
        CLEAR_RES,
        END
    }
}