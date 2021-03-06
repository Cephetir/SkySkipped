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

import gg.essential.api.utils.Multithreading
import gg.essential.universal.UChat
import kotlinx.coroutines.*
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.PacketEvent
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.IMixinSugarCaneMacro
import me.cephetir.skyskipped.utils.HttpUtils
import me.cephetir.skyskipped.utils.InventoryUtils
import me.cephetir.skyskipped.utils.RotationClass
import me.cephetir.skyskipped.utils.TextUtils.keepScoreboardCharacters
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.ContainerPlayer
import net.minecraft.inventory.Slot
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import qolskyblockmod.pizzaclient.features.macros.builder.MacroBuilder
import qolskyblockmod.pizzaclient.features.macros.builder.macros.FarmingMacro
import qolskyblockmod.pizzaclient.features.macros.farming.SugarCaneMacro
import xyz.apfelmus.cf4m.CF4M
import xyz.apfelmus.cheeto.client.modules.world.AutoFarm
import java.lang.reflect.Field
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs


open class FailSafe : Feature() {
    companion object {
        var stuck = false
        var desynced = false

        var timer = System.currentTimeMillis()

        var called4 = false
    }

    private var pizza = false
    private var cheeto = false
    private var updateTicks = 0
    private val random = Random()

    private var ticksWarpStuck = 0
    private var ticks = 0
    private var lastPos: BlockPos? = null
    private var called = false

    private var ticksWarpDesync = 0
    private var ticks2 = 0
    private var lastCount = 0L
    private var called2 = false

    private var lastState = false
    private var lastDirection: Any? = null

    private var called3 = false

    private var lastMacro = true

    private var ticks5 = 0
    private var called5 = false

    private var lastY = -1f
    private var called6 = false

    private var ticks7 = 0
    private var called7 = false
    private var lastMacro2 = true

    private var ticks8 = 0
    private var called8 = false

    @SubscribeEvent
    protected fun onTick(event: ClientTickEvent) {
        if (updateTicks++ >= 20) {
            update()
            updateTicks = 0
        }
    }

    @SubscribeEvent
    protected fun unstuck(event: ClientTickEvent) {
        if (!Config.failSafe) ticks = 0
        if (!Config.failSafe || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (called2 || called3 || called5 || called6 || called8) return

        if (pizza || cheeto) {
            if (ticksWarpStuck >= 0) {
                ticksWarpStuck--
                return
            }

            if (lastPos != null) {
                if (checkPos(mc.thePlayer.position)) {
                    ticks++
                    if (ticks >= 10) stuck = true
                } else {
                    lastPos = mc.thePlayer.position
                    ticks = 0
                    stuck = false
                }
            } else lastPos = mc.thePlayer.position

            if (ticks >= 60 && !called) {
                called = true
                BackgroundScope.launch {
                    try {
                        val pizza = pizza
                        val cheeto = cheeto
                        UChat.chat("??cSkySkipped ??f:: ??eYou got stuck! Trying to prevent that...")
                        if (pizza) MacroBuilder.onKey()
                        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            true
                        )
                        delay(100)

                        // back
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            true
                        )
                        delay(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            false
                        )
                        delay(100)

                        // left
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            true
                        )
                        delay(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            false
                        )
                        delay(100)

                        // right
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            true
                        )
                        delay(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            false
                        )
                        delay(100)
                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            false
                        )

                        if (pizza) MacroBuilder.onKey()
                        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

                        called = false
                        ticks = 0
                        ticksWarpStuck = 60
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    protected fun jacob(event: PacketEvent.ReceiveEvent) {
        if (
            !Cache.inSkyblock ||
            event.packet !is S3EPacketTeams ||
            !Config.failSafeJacob
        ) return
        if (event.packet.action != 2) return
        val line = event.packet.players.joinToString(
            " ",
            prefix = event.packet.prefix,
            postfix = event.packet.suffix
        ).stripColor().trim()

        if (!Cache.isJacob || !(pizza || cheeto)) return
        if (!line.contains("with")) return
        val split = line.split(" ")
        if (split.size != 3) return
        val number = split[2].replace(",", "").toInt()
        printdev("Jacob crop amount $number")
        if (number >= Config.failSafeJacobNumber) {
            printdev("Jacob detected!")
            UChat.chat("??cSkySkipped ??f:: ??eJacob event failsafe triggered! Stopping macro...")
            if (pizza) {
                MacroBuilder.onKey()
                lastMacro = true
            } else if (cheeto) {
                CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                lastMacro = false
            }
            called4 = true
        }
    }

    @SubscribeEvent
    protected fun onChat(event: ClientChatReceivedEvent) {
        if (!called4) return
        if (!event.message.unformattedText.stripColor().keepScoreboardCharacters().contains("Come see me in the Hub", true)) return
        printdev("Detected jacob msg in chat")
        UChat.chat("??cSkySkipped ??f:: ??eJacob event ended! Starting macro again...")
        if (lastMacro) MacroBuilder.onKey()
        else CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
        called4 = false
    }

    @SubscribeEvent
    protected fun desync(event: ClientTickEvent) {
        if (called || called2 || called3 || called5 || called6 || called8) return
        if (!Config.failSafeDesync) ticks2 = 0
        if (!Config.failSafeDesync || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return

        if (pizza || cheeto) {
            if (ticksWarpDesync >= 0) {
                ticksWarpDesync--
                return
            }

            val ticksTimeout = Config.failSafeDesyncTime * 20
            val stack = Minecraft.getMinecraft().thePlayer.heldItem
            if (stack == null ||
                !stack.hasTagCompound() ||
                !stack.tagCompound.hasKey("ExtraAttributes", 10)
            ) return
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
            if (newCount != -1L && newCount > lastCount) {
                lastCount = newCount
                ticks2 = 0
                desynced = false
            } else {
                ticks2++
                if (ticks2 >= ticksTimeout / 3) desynced = true
            }

            if (ticks2 >= ticksTimeout && !called2) {
                printdev("Triggered desync failsafe!")
                called2 = true
                BackgroundScope.launch {
                    try {
                        val extraDelay = Config.failSafeGlobalTime.toLong()
                        val pizza = pizza
                        val cheeto = cheeto
                        UChat.chat("??cSkySkipped ??f:: ??eDesync detected! Swapping lobbies...")
                        if (pizza) MacroBuilder.onKey()
                        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

                        val yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw)
                        val pitch = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch)

                        delay(extraDelay)
                        mc.thePlayer.sendChatMessage("/hub")
                        delay(5000L + extraDelay)
                        mc.thePlayer.sendChatMessage("/is")
                        delay(2500L + extraDelay)
                        mc.thePlayer.rotationYaw += yaw - MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw)
                        mc.thePlayer.rotationPitch = pitch

                        delay(100L + extraDelay)
                        if (pizza) MacroBuilder.onKey()
                        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

                        lastY = mc.thePlayer.posY.toFloat()
                        called2 = false
                        ticks2 = 0
                        ticksWarpDesync = 100
                        printdev("Ended resync process!")
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    protected fun autoSetSpawn(event: ClientTickEvent) {
        if (stuck || desynced) return
        if (!Config.failSafeSpawn || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return

        if (pizza && MacroBuilder.currentMacro is SugarCaneMacro) {
            var switch = false
            if (lastState != (MacroBuilder.currentMacro as IMixinSugarCaneMacro).state) {
                lastState = (MacroBuilder.currentMacro as IMixinSugarCaneMacro).state
                switch = true
            }

            if (switch && System.currentTimeMillis() - timer > 500) {
                UChat.chat("??cSkySkipped ??f:: ??eSetting spawnpoint...")
                mc.thePlayer.sendChatMessage("/sethome")
                timer = System.currentTimeMillis()
            }
        } else if (cheeto) {
            var switch = false
            val f: Field = AutoFarm::class.java.getDeclaredField("farmingDirection")
            f.isAccessible = true
            val value = f.get(CF4M.INSTANCE.moduleManager.getModule("AutoFarm"))
            if (lastDirection == null) lastDirection = value
            else if (lastDirection != value) {
                lastDirection = value
                switch = true
            }

            if (switch && System.currentTimeMillis() - timer > 500) {
                UChat.chat("??cSkySkipped ??f:: ??eSetting spawnpoint...")
                mc.thePlayer.sendChatMessage("/sethome")
                timer = System.currentTimeMillis()
            }
        }
    }

    @SubscribeEvent
    protected fun autoWarpBack(event: ClientTickEvent) {
        if (called2 || called3 || called5 || called8) return
        if (!Config.failSafeIsland || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (Cache.onIsland) return

        if (pizza || cheeto) {
            called3 = true
            BackgroundScope.launch Thread@{
                try {
                    val pizza = pizza
                    val cheeto = cheeto
                    if (pizza) MacroBuilder.onKey()
                    else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                    UChat.chat("??cSkySkipped ??f:: ??eDetected not on island! Warping back...")

                    val delay = Config.failSafeIslandDelay.toLong() * 1000L
                    val extraDelay = Config.failSafeGlobalTime.toLong()
                    delay(delay + extraDelay)

                    if (Cache.onIsland) {
                        if (pizza) MacroBuilder.onKey()
                        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                        called3 = false
                        return@Thread
                    }

                    goBack(delay, extraDelay, currentCoroutineContext())
                    delay(delay + extraDelay)
                    if (!Cache.onIsland) goBack(delay, extraDelay, currentCoroutineContext())

                    delay(delay + extraDelay)
                    if (pizza) MacroBuilder.onKey()
                    else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

                    lastY = mc.thePlayer.posY.toFloat()
                    called3 = false
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun goBack(delay: Long, extraDelay: Long, ctx: CoroutineContext) = withContext(ctx) {
        if (Cache.inSkyblock) mc.thePlayer.sendChatMessage("/is")
        else {
            mc.thePlayer.sendChatMessage("/l")
            delay(delay + extraDelay)
            mc.thePlayer.sendChatMessage("/play sb")
            delay(delay + extraDelay)
            mc.thePlayer.sendChatMessage("/is")
        }
    }

    @SubscribeEvent
    protected fun fullInv(event: ClientTickEvent) {
        if (called || called2 || called3 || called5 || called6 || called8 || desynced) return
        if (!Config.failSafeInv) ticks5 = 0
        if (!Config.failSafeInv || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (!pizza && !cheeto) return

        if (InventoryUtils.isFull()) {
            printdev("Inventory is full!")
            ticks5++
        } else ticks5 = 0

        if (!called5 && ticks5 >= 50) {
            printdev("Triggering full invenory failsafe!")
            called5 = true
            clearInventory({
                UChat.chat("??cSkySkipped ??f:: ??eInventory is full! Cleanning...")
                val pizza = pizza
                val cheeto = cheeto
                if (pizza) MacroBuilder.onKey()
                else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
            },
                {
                    val pizza = pizza
                    val cheeto = cheeto
                    if (pizza) MacroBuilder.onKey()
                    else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                    called5 = false
                    ticks5 = 0
                })
        }
    }

    @SubscribeEvent
    protected fun changeYaw(event: ClientTickEvent) {
        if (called6 || called3 || called2) return
        if (!Config.failSafeChangeYaw || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return

        if (!pizza && !cheeto) return
        val y = mc.thePlayer.posY.toFloat()
        if (lastY == -1f) {
            lastY = y
            return
        }

        if (abs(lastY - y) < 1.5f || called6) return
        called6 = true
        printdev("Detected Y change: old Y $lastY new Y $y")
        val p = pizza
        val c = cheeto
        UChat.chat("??cSkySkipped ??f:: ??eChanging yaw...")
        if (pizza) MacroBuilder.onKey()
        else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")

        var yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw)
        printdev("Last yaw: $yaw")
        yaw -= yaw % 10
        printdev("Round yaw: $yaw")
        val yaww = MathHelper.wrapAngleTo180_float(yaw + 180)
        printdev("New yaw: $yaww")

        val newYaw = if (Config.failSafeChangeYawRandom) getRandom(yaww - 2.5f, yaww + 2.5f) else yaww
        val newPitch = if (Config.failSafeChangeYawRandom) getRandom(-2.5f, 2.5f) else 0f
        printdev("Apply random on yaw and pitch: $newYaw $newPitch")
        val time = getRandom(Config.failSafeChangeYawSpeed * 60 + 250f, Config.failSafeChangeYawSpeed * 60 - 250f).toLong()
        RotationClass(RotationClass.Rotation(newYaw, newPitch), time)

        BackgroundScope.launch {
            try {
                delay(time + 500L + Config.failSafeGlobalTime)
                if (p) MacroBuilder.onKey()
                else if (c) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                lastY = mc.thePlayer.posY.toFloat()
                called6 = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SubscribeEvent
    protected fun checkBanWave(event: ClientTickEvent) {
        if (!Config.failSafeBanWave || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (!pizza && !cheeto && !called7) return
        if (ticks7++ < Config.failSafeBanWaveTimer * 60 * 20) return
        ticks7 = 0

        Multithreading.runAsync {
            val status = HttpUtils.sendGet(
                "https://api.snipes.wtf/bancheck",
                mapOf("Content-Type" to "application/json")
            )
            if (status == "Nah") {
                UChat.chat("??cSkySkipped ??f:: ??eBanwave: ??cFalse")
                if (Config.failSafeBanWaveDisable && called7) {
                    UChat.chat("??cSkySkipped ??f:: ??eReenbabling macro...")
                    if (lastMacro2) MacroBuilder.onKey()
                    else CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                    called7 = false
                }
            } else if (status == "disconnect:all") {
                UChat.chat("??cSkySkipped ??f:: ??eBanwave: ??aTrue")
                if (Config.failSafeBanWaveDisable && !called7) {
                    UChat.chat("??cSkySkipped ??f:: ??eDisabling macro...")
                    lastMacro2 = pizza
                    if (pizza) MacroBuilder.onKey()
                    else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                    called7 = true
                }
            } else UChat.chat("??cSkySkipped ??f:: ??cCoudn't check current banwave status!")
        }
    }

    @SubscribeEvent
    protected fun invCleaner(event: ClientTickEvent) {
        if (called || called2 || called3 || called5 || called6 || called8 || desynced) return
        if (!Config.failSafeInvConfig || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (!pizza && !cheeto) return

        if (ticks8++ >= Config.failSafeInvConfigTime * 60 * 20 && !called8) {
            called8 = true
            printdev("Clearing inv...")
            clearInventory({
                UChat.chat("??cSkySkipped ??f:: ??eCleanning inventory...")
                val pizza = pizza
                val cheeto = cheeto
                if (pizza) MacroBuilder.onKey()
                else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
            },
                {
                    val pizza = pizza
                    val cheeto = cheeto
                    if (pizza) MacroBuilder.onKey()
                    else if (cheeto) CF4M.INSTANCE.moduleManager.toggle("AutoFarm")
                    called8 = false
                    ticks8 = 0
                })
        }
    }

    private fun clearInventory(onStart: Runnable, onEnd: Runnable) {
        BackgroundScope.launch Thread@{
            try {
                onStart.run()
                val extraDelay = Config.failSafeGlobalTime.toLong() / 2
                delay(1000L)

                mc.displayGuiScreen(GuiInventory(mc.thePlayer))
                delay(1000L + extraDelay)

                if (mc.currentScreen !is GuiInventory) {
                    printdev("Invenory closed")
                    mc.thePlayer.closeScreen()
                    onEnd.run()
                    return@Thread
                }
                val inv = ((mc.currentScreen as GuiInventory).inventorySlots as ContainerPlayer).inventorySlots
                val stoneSlots = inv.filter {
                    it.hasStack && it.stack.displayName.stripColor().keepScoreboardCharacters()
                        .contains("Stone", true)
                }
                if (stoneSlots.isNotEmpty()) {
                    printdev("Stone detected!")
                    for (slot in stoneSlots) {
                        if (mc.currentScreen !is GuiInventory) {
                            printdev("Invenory closed")
                            mc.thePlayer.closeScreen()
                            onEnd.run()
                            return@Thread
                        }
                        mc.playerController.windowClick(
                            (mc.currentScreen as GuiInventory).inventorySlots.windowId,
                            slot.slotNumber, 0, 0, mc.thePlayer
                        )
                        mc.playerController.windowClick(
                            (mc.currentScreen as GuiInventory).inventorySlots.windowId,
                            -999, 0, 0, mc.thePlayer
                        )
                        delay(500L + extraDelay)
                    }
                } else printdev("no stone")
                val crops = inv.filter {
                    it.hasStack && when (it.stack.item) {
                        Items.nether_wart -> true
                        Items.reeds -> true
                        Items.potato -> true
                        Items.carrot -> true
                        Items.melon -> true
                        else -> it.stack.displayName.contains("mushroom") || it.stack.displayName.contains("wart")
                    }
                }
                mc.thePlayer.closeScreen()
                if (crops.isEmpty()) {
                    printdev("no crops")
                    mc.thePlayer.closeScreen()
                    onEnd.run()
                    return@Thread
                }

                delay(1000L + extraDelay)
                mc.thePlayer.sendChatMessage("/sbmenu")
                delay(1000L + extraDelay)

                val startTime = System.currentTimeMillis()
                var exit = false
                var trades: Slot? = null
                while (!exit) {
                    if (System.currentTimeMillis() - startTime >= 5000L) {
                        printdev("Cant find trades button")
                        mc.thePlayer.closeScreen()
                        onEnd.run()
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
                delay(500L + extraDelay)
                printdev("clicked trades")

                val startTime2 = System.currentTimeMillis()
                var sell: Slot? = null
                while (sell == null) {
                    if (System.currentTimeMillis() - startTime2 >= 5000L || mc.currentScreen !is GuiChest) {
                        printdev("Cant find sell button")
                        mc.thePlayer.closeScreen()
                        onEnd.run()
                        return@Thread
                    }
                    delay(100L)
                    sell = (mc.currentScreen as GuiChest).inventorySlots.inventorySlots.find {
                        it.hasStack && it.stack.item.unlocalizedName.contains("hopper")
                    }
                }
                printdev("found sell")

                val inventory = ((mc.currentScreen as GuiChest).inventorySlots as ContainerChest).inventorySlots
                val cropss = inventory.filter {
                    it.hasStack && when (it.stack.item) {
                        Items.nether_wart -> true
                        Items.reeds -> true
                        Items.potato -> true
                        Items.carrot -> true
                        Items.melon -> true
                        else -> it.stack.displayName.contains("mushroom")
                    }
                }
                if (cropss.isNotEmpty()) {
                    printdev("Crops found!")
                    for (slot in cropss) {
                        if (mc.currentScreen !is GuiChest) {
                            printdev("Invenory closed")
                            mc.thePlayer.closeScreen()
                            onEnd.run()
                            return@Thread
                        }
                        mc.playerController.windowClick(
                            (mc.currentScreen as GuiChest).inventorySlots.windowId,
                            slot.slotNumber, 0, 0, mc.thePlayer
                        )
                        delay(500L + extraDelay)
                    }
                } else printdev("no crops")

                onEnd.run()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun getRandom(a: Float, b: Float): Float = a + (b - a) * random.nextFloat()

    private fun update() {
        if (Config.failSafeForce) {
            pizza = true
            cheeto = true
            return
        }
        pizza = Loader.isModLoaded("pizzaclient") && MacroBuilder.toggled && MacroBuilder.currentMacro is FarmingMacro
        cheeto = Loader.isModLoaded("ChromaHUD") && CF4M.INSTANCE.moduleManager.isEnabled("AutoFarm")
    }

    private fun checkPos(player: BlockPos): Boolean =
        lastPos!!.x - player.x <= 1 && lastPos!!.x - player.x >= -1 && lastPos!!.y - player.y <= 1 && lastPos!!.y - player.y >= -1 && lastPos!!.z - player.z <= 1 && lastPos!!.z - player.z >= -1
}