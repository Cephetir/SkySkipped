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

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import qolskyblockmod.pizzaclient.features.macros.builder.MacroBuilder
import xyz.apfelmus.cf4m.CF4M
import xyz.apfelmus.cheeto.client.modules.world.AutoFarm

class FailSafe : Feature() {
    private var ticks = 0
    private var last: BlockPos? = null
    private var called = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!Config.failSafe || event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return
        if (Loader.isModLoaded("pizzaclient") && MacroBuilder.toggled) {
            if (last != null) {
                if (checkPos(mc.thePlayer.position)) ticks++ else {
                    last = mc.thePlayer.position
                    ticks = 0
                }
            } else last = mc.thePlayer.position

            if (ticks >= 60 && !called) {
                called = true
                Thread {
                    try {
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
            if (last != null) {
                if (checkPos(mc.thePlayer.position)) ticks++ else {
                    last = mc.thePlayer.position
                    ticks = 0
                }
            } else last = mc.thePlayer.position

            if (ticks >= 60 && !called) {
                called = true
                Thread {
                    try {
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

    private fun checkPos(player: BlockPos): Boolean = last!!.x - player.x <= 1 && last!!.x - player.x >= -1 && last!!.y - player.y <= 1 && last!!.y - player.y >= -1 && last!!.z - player.z <= 1 && last!!.z - player.z >= -1
}