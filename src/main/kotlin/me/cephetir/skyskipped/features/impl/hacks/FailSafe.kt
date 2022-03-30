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
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.event.events.PacketReceive
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import qolskyblockmod.pizzaclient.features.macros.builder.MacroBuilder
import qolskyblockmod.pizzaclient.features.macros.builder.macros.FarmingMacro
import xyz.apfelmus.cf4m.CF4M
import xyz.apfelmus.cheeto.client.modules.world.AutoFarm

class FailSafe : Feature() {
    private var ticks = 0
    private var lastPos: BlockPos? = null
    private var called = false

    private var ticks2 = 0
    private var lastCount = 0
    private var called2 = false

    @SubscribeEvent
    fun unstuck(event: ClientTickEvent) {
        if(!Config.failSafe) ticks = 0
        if (!Config.failSafe || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return

        if (Loader.isModLoaded("pizzaclient") && MacroBuilder.toggled && MacroBuilder.currentMacro is FarmingMacro) {
            if (lastPos != null) {
                if (checkPos(mc.thePlayer.position)) ticks++ else {
                    lastPos = mc.thePlayer.position
                    ticks = 0
                }
            } else lastPos = mc.thePlayer.position

            if (ticks >= 60 && !called) {
                called = true
                Thread {
                    try {
                        UChat.chat("§cSkySkipped §f:: §eYou got stuck! Trying to prevent that...")
                        MacroBuilder.onKey()
                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            true
                        )
                        Thread.sleep(100)

                        // back
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            false
                        )
                        Thread.sleep(100)

                        // left
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            false
                        )
                        Thread.sleep(100)

                        // right
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            false
                        )
                        Thread.sleep(100)
                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            false
                        )
                        MacroBuilder.onKey()
                        called = false
                        ticks = 0
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        } else if (Loader.isModLoaded("ChromaHUD") && CF4M.INSTANCE.moduleManager.isEnabled("AutoFarm")) {
            if (lastPos != null) {
                if (checkPos(mc.thePlayer.position)) ticks++ else {
                    lastPos = mc.thePlayer.position
                    ticks = 0
                }
            } else lastPos = mc.thePlayer.position

            if (ticks >= 60 && !called) {
                called = true
                Thread {
                    try {
                        UChat.chat("§cSkySkipped §f:: §eYou got stuck! Trying to prevent that...")
                        (CF4M.INSTANCE.moduleManager.getModule("AutoFarm") as AutoFarm).onDisable()
                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            true
                        )
                        Thread.sleep(100)

                        // back
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindBack.keyCode,
                            false
                        )
                        Thread.sleep(100)

                        // left
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindLeft.keyCode,
                            false
                        )
                        Thread.sleep(100)

                        // right
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            true
                        )
                        Thread.sleep(300)
                        KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindRight.keyCode,
                            false
                        )
                        Thread.sleep(100)
                        if (Config.failsafeJump) KeyBinding.setKeyBindState(
                            mc.gameSettings.keyBindJump.keyCode,
                            false
                        )
                        (CF4M.INSTANCE.moduleManager.getModule("AutoFarm") as AutoFarm).onEnable()
                        called = false
                        ticks = 0
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
    }

    @SubscribeEvent
    fun jacob(event: PacketReceive) {
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

        if (line.startsWith("Jacob's Contest")) {
            if (Loader.isModLoaded("pizzaclient") && MacroBuilder.toggled && MacroBuilder.currentMacro is FarmingMacro) {
                UChat.chat("§cSkySkipped §f:: §eJacob event started! Stopping macro...")
                MacroBuilder.onKey()
            }
            else if (Loader.isModLoaded("ChromaHUD") && CF4M.INSTANCE.moduleManager.isEnabled("AutoFarm")) {
                UChat.chat("§cSkySkipped §f:: §eJacob event started! Stopping macro...")
                (CF4M.INSTANCE.moduleManager.getModule("AutoFarm") as AutoFarm).onDisable()
            }
        }
    }

    @SubscribeEvent
    fun desync(event: ClientTickEvent) {
        if(!Config.failSafeDesync) ticks2 = 0
        if (!Config.failSafeDesync || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return

        val ticksTimeout = Config.failSafeDesyncTime * 20
        if (Loader.isModLoaded("pizzaclient") && MacroBuilder.toggled && MacroBuilder.currentMacro is FarmingMacro) {
            val stack = Minecraft.getMinecraft().thePlayer.heldItem
            var newCount = -1
            if (stack != null && stack.hasTagCompound()) {
                val tag = stack.tagCompound
                if (tag.hasKey("ExtraAttributes", 10)) {
                    val ea = tag.getCompoundTag("ExtraAttributes")
                    if (ea.hasKey("mined_crops", 99))
                        newCount = ea.getInteger("mined_crops")
                    else if (ea.hasKey("farmed_cultivating", 99))
                        newCount = ea.getInteger("farmed_cultivating")
                }
            }
            if(newCount != -1 && newCount > lastCount) lastCount = newCount
            else ticks2++

            if (ticks2 >= ticksTimeout && !called2) {
                called2 = true
                Thread {
                    try {
                        UChat.chat("§cSkySkipped §f:: §eDesync detected! Swapping lobbies...")

                        MacroBuilder.onKey()
                        mc.thePlayer.sendChatMessage("/sethome")
                        Thread.sleep(100L)
                        mc.thePlayer.sendChatMessage("/hub")

                        Thread.sleep(1000L)
                        mc.thePlayer.sendChatMessage("/is")

                        Thread.sleep(1000L)
                        MacroBuilder.onKey()

                        called2 = false
                        ticks2 = 0
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        } else if (Loader.isModLoaded("ChromaHUD") && CF4M.INSTANCE.moduleManager.isEnabled("AutoFarm")) {
            val stack = Minecraft.getMinecraft().thePlayer.heldItem
            var newCount = -1
            if (stack != null && stack.hasTagCompound()) {
                val tag = stack.tagCompound
                if (tag.hasKey("ExtraAttributes", 10)) {
                    val ea = tag.getCompoundTag("ExtraAttributes")
                    if (ea.hasKey("mined_crops", 99))
                        newCount = ea.getInteger("mined_crops")
                    else if (ea.hasKey("farmed_cultivating", 99))
                        newCount = ea.getInteger("farmed_cultivating")
                }
            }
            if(newCount != -1 && newCount > lastCount) lastCount = newCount
            else ticks2++

            if (ticks2 >= ticksTimeout && !called2) {
                called2 = true
                Thread {
                    try {
                        UChat.chat("§cSkySkipped §f:: §eDesync detected! Swapping lobbies...")

                        (CF4M.INSTANCE.moduleManager.getModule("AutoFarm") as AutoFarm).onDisable()
                        mc.thePlayer.sendChatMessage("/sethome")
                        Thread.sleep(100L)
                        mc.thePlayer.sendChatMessage("/hub")

                        Thread.sleep(1000L)
                        mc.thePlayer.sendChatMessage("/is")

                        Thread.sleep(1000L)
                        (CF4M.INSTANCE.moduleManager.getModule("AutoFarm") as AutoFarm).onEnable()

                        called2 = false
                        ticks2 = 0
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
    }

    private fun checkPos(player: BlockPos): Boolean =
        lastPos!!.x - player.x <= 1 && lastPos!!.x - player.x >= -1 && lastPos!!.y - player.y <= 1 && lastPos!!.y - player.y >= -1 && lastPos!!.z - player.z <= 1 && lastPos!!.z - player.z >= -1
}