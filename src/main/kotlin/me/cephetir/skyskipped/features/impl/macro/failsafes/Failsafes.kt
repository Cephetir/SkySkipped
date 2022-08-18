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

import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.Macro
import me.cephetir.skyskipped.utils.RandomUtils
import me.cephetir.skyskipped.utils.RotationClass
import me.cephetir.skyskipped.utils.TextUtils.containsAny
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.util.BlockPos
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object Failsafes : Feature() {

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
    var fullInvTicks = 0
    var clearInvTimer = 0L
    var openedInv = 0
    var toClearInv: LinkedList<Slot>? = null

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
            BackgroundScope.launch {
                delay(300L)
                mc.thePlayer.sendChatMessage("/play sb")
            }
            warpTimer += 2000L
        }
    }

    fun stuck(getBack: Runnable) {
        if (mc.currentScreen != null && mc.currentScreen !is GuiChat) return
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
                if (System.currentTimeMillis() - keyTimer >= 150) {
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
                if (System.currentTimeMillis() - keyTimer >= 150) {
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
                if (System.currentTimeMillis() - keyTimer >= 150) {
                    if (keyState) keyState = false
                    else {
                        keyState = true
                        stuckSteps = StuckSteps.RIGHT
                    }
                    keyTimer = System.currentTimeMillis()
                }
            }

            StuckSteps.RIGHT -> {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, keyState)
                if (System.currentTimeMillis() - keyTimer >= 150) {
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

    private var calledClean = false
    fun clearInv(getBack: Runnable) {
        unpressKeys()
        if (calledClean) return
        calledClean = true
        UChat.chat("§cSkySkipped §f:: §eInventory is full! Cleaning...")
        Macro.sendWebhook("Full Inventory failsafe", "Inventory is full! Cleaning...", false)

        if (!false) return clearInv2(getBack)

        BackgroundScope.launch {


            getBack.run()
            fullInvTicks = 0
            toClearInv = null
            openedInv = 0
            calledClean = false
        }
    }

    fun clearInv2(getBack: Runnable) {
        BackgroundScope.launch Thread@{
            mc.thePlayer.sendChatMessage("/sbmenu")
            delay(500L)

            val startTime = System.currentTimeMillis()
            var exit = false
            var trades: Slot? = null
            while (!exit) {
                if (System.currentTimeMillis() - startTime >= 5000L) {
                    printdev("Cant find trades button")
                    mc.thePlayer.closeScreen()
                    getBack.run()
                    fullInvTicks = 0
                    toClearInv = null
                    openedInv = 0
                    calledClean = false
                    return@Thread
                }
                delay(100L)
                exit = mc.currentScreen != null && mc.currentScreen is GuiChest
                if (exit) {
                    trades = (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.find {
                        it.hasStack && it.stack.displayName.stripColor() == "Trades"
                    }
                    exit = trades != null
                }
            }
            mc.playerController.windowClick(
                (mc.currentScreen as GuiChest).inventorySlots.windowId,
                trades!!.slotIndex, 0, 0, mc.thePlayer
            )
            printdev("clicked trades")
            delay(500L)

            val startTime2 = System.currentTimeMillis()
            var sell: Slot? = null
            while (sell == null) {
                if (System.currentTimeMillis() - startTime2 >= 5000L || mc.currentScreen !is GuiChest) {
                    printdev("Cant find sell button")
                    mc.thePlayer.closeScreen()
                    getBack.run()
                    fullInvTicks = 0
                    toClearInv = null
                    openedInv = 0
                    calledClean = false
                    return@Thread
                }
                delay(100L)
                sell = (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.find {
                    it.hasStack && it.stack.displayName.stripColor().startsWith("Coal")
                }
            }
            printdev("found sell")

            val inventory = ((mc.currentScreen as GuiChest).inventorySlots as ContainerChest).inventorySlots
            val cropss = inventory.filter {
                it.hasStack && it.stack.displayName.containsAny("wart", "sugar cane", "cocoa bean", "mushroom") && !it.stack.displayName.containsAny(
                    "hoe",
                    "axe"
                )
            }
            if (cropss.isNotEmpty()) {
                printdev("Crops found!")
                for (slot in cropss) {
                    if (mc.currentScreen !is GuiChest) {
                        printdev("Invenory closed")
                        mc.thePlayer.closeScreen()
                        getBack.run()
                        fullInvTicks = 0
                        toClearInv = null
                        openedInv = 0
                        calledClean = false
                        return@Thread
                    }
                    mc.playerController.windowClick(
                        (mc.currentScreen as GuiChest).inventorySlots.windowId,
                        slot.slotNumber, 0, 0, mc.thePlayer
                    )
                    delay(200L)
                }
            } else printdev("no crops")
            delay(500L)

            mc.thePlayer.closeScreen()
            printdev("Invenory closed")

            getBack.run()
        }
    }

    fun bedrockCage(getBack: Runnable) {
        if (System.currentTimeMillis() - bedrockTimer < 5000L) return
        unpressKeys()

        if (rotating == null) {
            val yaw = RandomUtils.getRandom(-180f, 180f)
            val pitch = RandomUtils.getRandom(-45f, 75f)
            rotating = RotationClass(RotationClass.Rotation(yaw, pitch), 1000L + RandomUtils.getRandom(500f, 1000f).roundToLong())
        } else if (rotating!!.done) {
            bedrockTimer = System.currentTimeMillis() + RandomUtils.getRandom(1000f, 3000f).roundToLong()
            rotating = null
        }

        if (RandomUtils.getRandom(1f, 50f) == 25f) mc.thePlayer.sendChatMessage(messages[RandomUtils.getRandom(0f, messages.size - 1f).roundToInt()])
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
}