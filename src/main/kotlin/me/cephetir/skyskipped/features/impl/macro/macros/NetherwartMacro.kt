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

package me.cephetir.skyskipped.features.impl.macro.macros

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.impl.macro.Macro
import me.cephetir.skyskipped.utils.HttpUtils
import me.cephetir.skyskipped.utils.InventoryUtils
import me.cephetir.skyskipped.utils.RotationClass
import me.cephetir.skyskipped.utils.ScoreboardUtils
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.BlockPos
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class NetherwartMacro : Macro("NetherWart") {
    private val events = Events(this)
    private val random = Random()
    private val messages =
        listOf("huh?", "what is it", "what happened?", "wtf", "wtf???", "lmao", "LMAO", "lamo", "wft", "hello?", "help", "wth is that", "lol")

    // States
    private var farmDirection = FarmDirection.NORTH
    private var farmType = FarmType.HORIZONTAL

    private var movementDirection = MovementDirection.LEFT
    private var farmingState = FarmingState.SETUP

    private var lastFps = 60
    private var lastDist = 8

    // Temp data
    private var rotating: RotationClass? = null
    private var rotated = false
    private var forward = false
    private var lastyaw = -1f
    private var lastpitch = -1f
    private var spawnTimer = 0L
    private var lastY = -1
    private var dced = false
    private var banwave = false

    //
    // Failsafes
    //

    // States
    private var stuckSteps = StuckSteps.SETUP
    private var desyncedSteps = DesyncedSteps.SETUP
    private var warpedSteps = WarpBackSteps.SETUP
    private var clearInvSteps = ClearInvSteps.SETUP

    // Unstuck
    private var lastPos: BlockPos? = null
    private var ticksStuck = 0
    private var keyState = true
    private var keyTimer = 0L

    // Desync
    private var ticksDesync = 0
    private var ticksWarpDesync = 0
    private var startCount = -1L
    private var lastCount = -1L
    private var desyncWaitTimer = 0L
    private var desyncF = true

    // Warp Back
    private var warpTimer = 0L
    private var warpLobbySteps = 0

    // Jacob Event
    private var stoppedForEvent = false

    // Full Inv
    private var fullInvTicks = 0
    private var clearInvTimer = 0L
    private var openedInv = 0
    private var toClearInv: MutableList<Slot>? = null

    // Ban wave checker
    private var checkerTicks = 0
    private var checkerStopped = false

    // Bedrock cage
    private var bedrockTimer = 0L

    open class Events(private val macro: NetherwartMacro) {
        @SubscribeEvent
        protected fun onTick(event: ClientTickEvent) = macro.onTick(event)

        @SubscribeEvent
        protected fun onChat(event: ClientChatReceivedEvent) = macro.onChat(event.message.unformattedText.stripColor().keepScoreboardCharacters())
    }

    override fun info() = "Macro: Nether Wart Macro, Settings: ${farmDirection.name}, ${farmType.name}, State: ${farmingState.name}"

    override fun isBanwave(): String = if (banwave) "§aFalse" else "§4True"
    override fun banwaveCheckIn(): Long = checkerTicks / 20 * 1000L

    override fun cropsMined(): Long = lastCount - startCount

    override fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    private fun onEnable() {
        reset()
        unpressKeys()
        MinecraftForge.EVENT_BUS.register(events)
        UChat.chat("§cSkySkipped §f:: §eNether Wart Macro §aEnabled§e! Settings: ${farmDirection.name}, ${farmType.name}")
    }

    private fun onDisable() {
        if (Config.netherWartCpuSaver) {
            mc.gameSettings.limitFramerate = lastFps
            mc.gameSettings.renderDistanceChunks = lastDist
        }

        MinecraftForge.EVENT_BUS.unregister(events)
        unpressKeys()
        reset()
        UChat.chat("§cSkySkipped §f:: §eNether Wart Macro §cDisabled§e!")
    }

    private fun reset() {
        farmDirection = FarmDirection.values()[Config.netherWartDirection]
        farmType = FarmType.values()[Config.netherWartType]
        movementDirection = MovementDirection.LEFT
        farmingState = FarmingState.SETUP

        rotating = null
        rotated = false
        forward = false
        lastyaw = -1f
        lastpitch = -1f
        spawnTimer = 0L
        lastY = -1
        dced = false
        banwave = false

        stuckSteps = StuckSteps.SETUP
        desyncedSteps = DesyncedSteps.SETUP
        warpedSteps = WarpBackSteps.SETUP
        clearInvSteps = ClearInvSteps.SETUP

        lastPos = null
        ticksStuck = 0
        keyState = true
        keyTimer = 0L

        ticksDesync = 0
        ticksWarpDesync = 0
        startCount = -1L
        lastCount = -1L
        desyncWaitTimer = 0L
        desyncF = true

        warpTimer = 0L
        warpLobbySteps = 0

        stoppedForEvent = false

        fullInvTicks = 0
        clearInvTimer = 0L
        openedInv = 0
        toClearInv = null

        checkerTicks = 0
        checkerStopped = false

        bedrockTimer = 0L
        banwave = false
    }

    fun onTick(event: ClientTickEvent) {
        if ((mc.thePlayer == null || mc.theWorld == null) && !dced) return checkBan()
        when (event.phase) {
            Phase.START -> onTickPre()
            Phase.END -> onTickPost()
            null -> return
        }
    }

    fun onChat(message: String) {
        if (farmingState == FarmingState.FARM) {
            if (message.startsWith("From"))
                sendWebhook("Received Message", message, true)
            else if (message.contains("is visiting Your Island"))
                sendWebhook("Somebody is visiting you", message, true)
            else if (message.contains("has invited you to join their party!"))
                sendWebhook("Received Party Request", message, true)

            if (message.startsWith("[Important] This server will restart soon:")) {
                unpressKeys()
                mc.thePlayer.sendChatMessage("/setspawn")
                printdev("Detected server reboot")
                sendWebhook("Server Reboot", message, false)
                farmingState = FarmingState.DESYNED
                desyncedSteps = DesyncedSteps.WARP_TO_LOBBY
                desyncWaitTimer = System.currentTimeMillis()
                desyncF = true
            }
            return
        }

        if (!stoppedForEvent) return
        if (!message.contains("Come see me in the Hub", true)) return
        printdev("Detected jacob msg in chat")
        UChat.chat("§cSkySkipped §f:: §eJacob event ended! Starting macro again...")
        farmingState = FarmingState.SETUP
        stoppedForEvent = false
    }

    private fun onTickPre() {
        when (farmingState) {
            FarmingState.SETUP -> setup()
            FarmingState.FARM -> {
                if (applyFailsafes()) return
                checkRotation()
                checkDirection()
            }
            FarmingState.CLIMB -> climb()
            FarmingState.STUCK -> stuck()
            FarmingState.DESYNED -> desynced()
            FarmingState.WARPED -> warped()
            FarmingState.CLEAR_INV -> clearInv()
            FarmingState.BEDROCK_CAGE -> bedrockCage()
            FarmingState.IDLE -> {
                unpressKeys()
                if (dced) {
                    if (mc.thePlayer == null || mc.theWorld == null) return
                    farmingState = FarmingState.SETUP
                }
            }
        }
    }

    private fun setup() {
        unpressKeys()
        mc.displayGuiScreen(null)
        if (!Cache.onIsland) {
            farmingState = FarmingState.WARPED
            return
        }
        if (rotating == null) {
            val yaw = when (farmDirection) {
                FarmDirection.NORTH -> 180f
                FarmDirection.SOUTH -> 0f
                FarmDirection.WEST -> 90f
                FarmDirection.EAST -> -90f
            }
            val pitch = 0f
            printdev("Rotate yaw and pitch: $yaw $pitch")
            rotating = RotationClass(RotationClass.Rotation(yaw, 0f), 1500L)
        }
        if (rotating!!.done) {
            printdev("Finished rotating")

            if (Config.netherWartCpuSaver) {
                lastFps = mc.gameSettings.limitFramerate
                mc.gameSettings.limitFramerate = 30
                lastDist = mc.gameSettings.renderDistanceChunks
                mc.gameSettings.renderDistanceChunks = 2
            }

            farmingState = FarmingState.FARM
            rotating = null
            rotated = true
            lastY = ceil(mc.thePlayer.posY).roundToInt()
        }
    }

    private fun climb() {
        unpressKeys()
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)

        val ladderBlock = mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
        if (ladderBlock.block != Blocks.ladder) {
            printdev("Finished climbing")
            farmDirection = when (farmDirection) {
                FarmDirection.SOUTH -> FarmDirection.NORTH
                FarmDirection.WEST -> FarmDirection.EAST
                FarmDirection.NORTH -> FarmDirection.SOUTH
                FarmDirection.EAST -> FarmDirection.WEST
            }
            farmingState = FarmingState.SETUP
            rotating = null
        }
    }

    private fun stuck() {
        when (stuckSteps) {
            StuckSteps.SETUP -> {
                UChat.chat("§cSkySkipped §f:: §eYou got stuck! Trying to prevent that...")
                sendWebhook("Unstuck failsafe", "You got stuck! Trying to prevent that...", false)
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
                farmingState = FarmingState.SETUP
                ticksStuck = 0
            }
        }
    }

    private fun desynced() {
        when (desyncedSteps) {
            DesyncedSteps.SETUP -> {
                if (desyncF) {
                    UChat.chat("§cSkySkipped §f:: §eDesync detected! Swapping lobbies...")
                    sendWebhook("Desync failsafe", "Desync detected! Swapping lobbies...", false)
                    unpressKeys()
                    if (!Config.netherWartSetSpawn) mc.thePlayer.sendChatMessage("/sethome")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 2000L) {
                    printdev("next stage warp to hub")
                    desyncedSteps = DesyncedSteps.WARP_TO_LOBBY
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.WARP_TO_LOBBY -> {
                if (desyncF) {
                    printdev("Warp to hub")
                    unpressKeys()
                    mc.thePlayer.sendChatMessage("/hub")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 10000L) {
                    printdev("next stage warp back")
                    desyncedSteps = DesyncedSteps.WARP_BACK
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.WARP_BACK -> {
                if (desyncF) {
                    printdev("Warp to is")
                    unpressKeys()
                    mc.thePlayer.sendChatMessage("/is")
                    desyncF = false
                }
                if (System.currentTimeMillis() - desyncWaitTimer >= 5000L) {
                    printdev("next stage end")
                    desyncedSteps = DesyncedSteps.END
                    desyncWaitTimer = System.currentTimeMillis()
                    desyncF = true
                }
            }
            DesyncedSteps.END -> {
                farmingState = FarmingState.SETUP
                ticksDesync = 0
                ticksWarpDesync = 100
                printdev("Ended resync process!")
            }
        }
    }

    private fun warped() {
        unpressKeys()
        when (warpedSteps) {
            WarpBackSteps.SETUP -> {
                warpTimer = System.currentTimeMillis()
                if (Cache.onIsland) {
                    farmingState = FarmingState.SETUP
                    warpLobbySteps = 0
                } else if (Cache.inSkyblock) warpedSteps = WarpBackSteps.WARP_BACK
                else if (!Cache.inSkyblock) warpedSteps = WarpBackSteps.WARP_TO_LOBBY
            }
            WarpBackSteps.WARP_TO_LOBBY -> {
                if (Cache.onIsland) warpedSteps = WarpBackSteps.SETUP
                if (System.currentTimeMillis() - warpTimer < 3000L) return
                warpTimer = System.currentTimeMillis()
                when (warpLobbySteps) {
                    0 -> mc.thePlayer.sendChatMessage("/l")
                    1 -> mc.thePlayer.sendChatMessage("/play sb")
                    2 -> mc.thePlayer.sendChatMessage("/is")
                }
                warpLobbySteps++
            }
            WarpBackSteps.WARP_BACK -> {
                if (System.currentTimeMillis() - warpTimer < 1500L) return
                warpTimer = System.currentTimeMillis()
                mc.thePlayer.sendChatMessage("/is")
                warpedSteps = WarpBackSteps.SETUP
            }
        }
    }

    private fun clearInv() {
        unpressKeys()
        when (clearInvSteps) {
            ClearInvSteps.SETUP -> {
                UChat.chat("§cSkySkipped §f:: §eInventory is full! Cleaning...")
                sendWebhook("Full Inventory failsafe", "Inventory is full! Cleaning...", false)
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
                printdev("Finished clearing")
                farmingState = FarmingState.SETUP
                clearInvSteps = ClearInvSteps.SETUP
                fullInvTicks = 0
                toClearInv = null
                openedInv = 0
            }
        }
    }

    private fun bedrockCage() {
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
        if (!bedrockFailsafe()) {
            sendWebhook("Lucky Escape!", "You successfully left bedrock cage!", true)
            farmingState = FarmingState.IDLE
        }
    }

    private fun onTickPost() {
        if (farmingState != FarmingState.FARM) return
        unpressKeys()
        if (mc.currentScreen != null && mc.currentScreen !is GuiChat) return

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, forward)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, false)
        val flag = movementDirection == MovementDirection.LEFT
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, flag && !forward)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, !flag && !forward)
    }

    override fun stopAndOpenInv() {
        unpressKeys()
        farmingState = FarmingState.IDLE
        mc.displayGuiScreen(GuiInventory(mc.thePlayer))
    }

    override fun closeInvAndReturn() {
        unpressKeys()
        mc.displayGuiScreen(null)
        farmingState = FarmingState.SETUP
    }

    private fun unpressKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
    }

    private val ignoreBlocks = listOf<Block>(
        Blocks.air,
        Blocks.water,
        Blocks.wall_sign,
        Blocks.ladder
    )

    private fun checkDirection() {
        var x = 0
        var x2 = 0
        var z = 0
        var z2 = 0
        when (farmDirection) {
            FarmDirection.SOUTH -> {
                x = 1
                z2 = 1
            }
            FarmDirection.WEST -> {
                z = 1
                x2 = -1
            }
            FarmDirection.NORTH -> {
                x = -1
                z2 = -1
            }
            FarmDirection.EAST -> {
                z = -1
                x2 = 1
            }
        }
        val y = ceil(mc.thePlayer.posY)

        val side =
            if (movementDirection == MovementDirection.LEFT) BlockPos(
                mc.thePlayer.posX + x,
                y,
                mc.thePlayer.posZ + z
            )
            else BlockPos(
                mc.thePlayer.posX + x * -1,
                y,
                mc.thePlayer.posZ + z * -1
            )

        val frwrd = BlockPos(
            mc.thePlayer.posX + x2,
            y,
            mc.thePlayer.posZ + z2
        )

        val sideBlock = mc.theWorld.getBlockState(side)
        val frwrdBlock = mc.theWorld.getBlockState(frwrd)

        if (farmType == FarmType.LADDERS) {
            val ladderBlock = mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ))
            if (ladderBlock.block == Blocks.ladder) {
                printdev("Detected ladder")
                farmingState = FarmingState.CLIMB
                return
            }
        }

        forward = false
        if (frwrdBlock.block == Blocks.air) {
            forward = true
            printdev("Going forward")
        }

        if (farmType == FarmType.DROPDOWN) {
            if (lastY == -1) lastY = y.roundToInt()
            else if (abs(y - lastY) >= 2) {
                printdev("Detected Y change!")
                farmDirection = when (farmDirection) {
                    FarmDirection.SOUTH -> FarmDirection.NORTH
                    FarmDirection.WEST -> FarmDirection.EAST
                    FarmDirection.NORTH -> FarmDirection.SOUTH
                    FarmDirection.EAST -> FarmDirection.WEST
                }
                farmingState = FarmingState.SETUP
                rotating = null
                return
            }
        }

        if (!ignoreBlocks.contains(sideBlock.block)) {
            movementDirection =
                if (movementDirection == MovementDirection.LEFT) MovementDirection.RIGHT
                else MovementDirection.LEFT
            printdev("Changing direction to ${movementDirection.name}")

            if (Config.netherWartSetSpawn && System.currentTimeMillis() - spawnTimer >= 1000) {
                UChat.chat("§cSkySkipped §f:: §eSetting spawnpoint...")
                mc.thePlayer.sendChatMessage("/sethome")
                spawnTimer = System.currentTimeMillis()
            }
        }
    }

    private fun checkRotation() {
        if (lastyaw == -1f || lastpitch == -1f || rotated) {
            lastyaw = mc.thePlayer.rotationYaw
            lastpitch = mc.thePlayer.rotationPitch
            rotated = false
            return
        }

        val yaw = mc.thePlayer.rotationYaw
        val pitch = mc.thePlayer.rotationPitch
        if (lastyaw != yaw || lastpitch != pitch) {
            printdev("Detected rotation change")
            farmingState = FarmingState.SETUP
            rotating = null
        }
    }

    private fun applyFailsafes(): Boolean {
        if (bedrockFailsafe()) {
            UChat.chat("§cSkySkipped §f:: §eBedrock detected! Applying cage failsafe...")
            sendWebhook("Bedrock Cage", "Found bedrock around player! Applying failsafe...", true)
            farmingState = FarmingState.BEDROCK_CAGE
            bedrockTimer = System.currentTimeMillis() + 3500L
            return true
        }
        if (warpBackFailsafe()) return true
        if (captchaFailsafe()) return true
        if (fullInvFailsafe()) return true
        if (unstuckFailsafe()) return true
        if (desyncFailsafe()) return true
        if (jacobFailsafe()) return true
        banwaveChecker()
        return false
    }

    private fun bedrockFailsafe(): Boolean {
        val pos1 = mc.thePlayer.position.add(-2, -2, -2)
        val pos2 = mc.thePlayer.position.add(2, 2, 2)

        for (pos in BlockPos.getAllInBox(pos1, pos2))
            if (mc.theWorld.getBlockState(pos).block == Blocks.bedrock)
                return true

        return false
    }

    private fun unstuckFailsafe(): Boolean {
        var stuck = false
        if (!Config.netherWartStuck) return false
        if (lastPos == null) lastPos = mc.thePlayer.position
        else {
            if (checkPos(mc.thePlayer.position)) {
                ticksStuck++
                if (ticksStuck >= 10) stuck = true
            } else {
                lastPos = mc.thePlayer.position
                ticksStuck = 0
            }
        }

        if (ticksStuck >= 60) {
            printdev("Detected stuck")
            farmingState = FarmingState.STUCK
            stuckSteps = StuckSteps.SETUP
        }
        return stuck
    }

    private fun desyncFailsafe(): Boolean {
        var desynced = false
        if (!Config.netherWartDesync) return false
        if (ticksWarpDesync >= 0) {
            ticksWarpDesync--
            return false
        }

        val ticksTimeout = Config.netherWartDesyncTime * 20
        val stack = Minecraft.getMinecraft().thePlayer.heldItem
        if (stack == null ||
            !stack.hasTagCompound() ||
            !stack.tagCompound.hasKey("ExtraAttributes", 10)
        ) return false
        var newCount = -1L
        val tag = stack.tagCompound
        if (tag.hasKey("ExtraAttributes", 10)) {
            val ea = tag.getCompoundTag("ExtraAttributes")
            if (ea.hasKey("mined_crops", 99))
                newCount = ea.getLong("mined_crops")
            else if (ea.hasKey("farmed_cultivating", 99))
                newCount = ea.getLong("farmed_cultivating")
        }
        printdev("Current counter: $newCount")
        if (newCount == -1L) return false
        if (newCount > lastCount) {
            if (startCount == -1L) startCount = newCount
            lastCount = newCount
            ticksDesync = 0
        } else {
            ticksDesync++
            if (ticksDesync >= ticksTimeout / 3) desynced = true
        }

        if (ticksDesync >= ticksTimeout) {
            printdev("Detected desync")
            farmingState = FarmingState.DESYNED
            desyncedSteps = DesyncedSteps.SETUP
        }
        return desynced
    }

    private fun warpBackFailsafe(): Boolean {
        if (Cache.onIsland) return false
        printdev("Detected not on island")
        farmingState = FarmingState.WARPED
        warpedSteps = WarpBackSteps.SETUP

        UChat.chat("§cSkySkipped §f:: §eDetected not on island! Warping back...")
        sendWebhook("Warp failsafe", "Detected not on island! Warping back...", false)
        return true
    }

    private fun jacobFailsafe(): Boolean {
        if (!Config.netherWartJacob) return false
        if (!Cache.isJacob) return false
        printdev("Jacob event is on!")

        val lines = ScoreboardUtils.sidebarLines
        for (line in lines) {
            if (!line.contains("with")) continue
            val split = line.split(" ")
            if (split.size != 3) return false
            val number = split[2].replace(",", "").toInt()
            printdev("Jacob crop amount $number")
            if (number >= Config.netherWartJacobNumber) {
                printdev("Jacob detected!")
                UChat.chat("§cSkySkipped §f:: §eJacob event started! Stopping macro...")
                sendWebhook("Jacob event", "Jacob event started! Stopping macro...", false)
                farmingState = FarmingState.IDLE
                stoppedForEvent = true
                return true
            }
        }
        printdev("Cant find funny numbers line :crying:")
        return false
    }

    private fun fullInvFailsafe(): Boolean {
        if (!Config.netherWartFullInv) return false

        if (InventoryUtils.isFull()) {
            printdev("Inventory is full!")
            fullInvTicks++
        } else fullInvTicks = 0

        if (fullInvTicks >= 50) {
            printdev("Triggering full invenory failsafe!")
            farmingState = FarmingState.CLEAR_INV
            clearInvSteps = ClearInvSteps.SETUP
            return true
        }

        return false
    }

    private fun banwaveChecker() {
        if (!Config.netherWartBanWaveChecker) return
        if (checkerTicks++ < Config.netherWartBanWaveCheckerTimer * 60 * 20) return

        Multithreading.runAsync {
            val status = HttpUtils.sendGet(
                "https://api.snipes.wtf/bancheck",
                mapOf("Content-Type" to "application/json")
            )
            if (status == "Nah") {
                banwave = false
                UChat.chat("§cSkySkipped §f:: §eBanwave: §aFalse")
                if (Config.netherWartBanWaveCheckerDisable && checkerStopped) {
                    UChat.chat("§cSkySkipped §f:: §eReenbabling macro...")
                    sendWebhook("Ban Wave Checker", "Ban Wave ended, reenabling macro...", false)
                    farmingState = FarmingState.IDLE
                    checkerStopped = false
                }
            } else if (status == "disconnect:all") {
                banwave = true
                UChat.chat("§cSkySkipped §f:: §eBanwave: §cTrue")
                if (Config.netherWartBanWaveCheckerDisable && !checkerStopped) {
                    UChat.chat("§cSkySkipped §f:: §eDisabling macro...")
                    sendWebhook("Ban Wave Checker", "Ban Wave started, disabling macro...", false)
                    farmingState = FarmingState.IDLE
                    checkerStopped = true
                }
            } else UChat.chat("§cSkySkipped §f:: §cCouldn't check current banwave status!")
        }
        checkerTicks = 0
    }

    private fun captchaFailsafe(): Boolean {
        val item = mc.thePlayer.heldItem?.item ?: return false
        if (item == Items.map || item == Items.filled_map) {
            farmingState = FarmingState.IDLE
            sendWebhook("Captcha failsafe", "Detected map in hands! Recommend solving it asap", true)
            return true
        }
        return false
    }

    private fun checkBan() {
        if (mc.currentScreen is GuiDisconnected) {
            if (Config.webhook) {
                val message = ObfuscationReflectionHelper.getPrivateValue<IChatComponent, GuiDisconnected>(
                    GuiDisconnected::class.java, mc.currentScreen as GuiDisconnected,
                    "message", "field_146304_f"
                )
                val reason = StringBuilder()
                for (line in message.siblings) reason.append(line.unformattedText)
                val rsn = reason.toString().replace("\r", "\\r").replace("\n", "\\n")
                sendWebhook("Disconnected", "You got disconnected with reason:\\n$rsn", true)
            }

            if (Config.netherWartReconnect) {
                farmingState = FarmingState.IDLE
                dced = true
                mc.displayGuiScreen(
                    GuiConnecting(
                        GuiMultiplayer(GuiMainMenu()),
                        Minecraft.getMinecraft(),
                        ServerData(Cache.prevName, Cache.prevIP, Cache.prevIsLan)
                    )
                )
            } else toggle()
        }
    }

    private fun checkInv(): Boolean {
        if (mc.currentScreen !is GuiInventory) {
            printdev("Invenory closed")
            mc.thePlayer.closeScreen()
            return true
        }
        return false
    }

    private fun checkPos(player: BlockPos): Boolean =
        abs(lastPos!!.x - player.x) <= 2 && abs(lastPos!!.y - player.y) <= 2 && abs(lastPos!!.z - player.z) <= 2

    private fun getRandom(a: Float, b: Float): Float = a + (b - a) * random.nextFloat()

    private enum class MovementDirection {
        LEFT,
        RIGHT
    }

    private enum class FarmingState {
        SETUP,
        FARM,
        CLIMB,
        STUCK,
        DESYNED,
        WARPED,
        CLEAR_INV,
        BEDROCK_CAGE,
        IDLE
    }

    private enum class StuckSteps {
        SETUP,
        BACK,
        FORWARD,
        LEFT,
        RIGHT,
        END
    }

    private enum class DesyncedSteps {
        SETUP,
        WARP_TO_LOBBY,
        WARP_BACK,
        END
    }

    private enum class WarpBackSteps {
        SETUP,
        WARP_TO_LOBBY,
        WARP_BACK
    }

    private enum class ClearInvSteps {
        SETUP,
        OPEN_INV,
        CLEAR_STONE,
        CLEAR_RES,
        END
    }

    private enum class FarmDirection {
        NORTH,
        EAST,
        WEST,
        SOUTH
    }

    private enum class FarmType {
        HORIZONTAL,
        VERTICAL,
        LADDERS,
        DROPDOWN
    }
}