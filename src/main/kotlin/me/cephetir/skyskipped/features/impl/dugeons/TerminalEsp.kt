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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.TextUtils.stripColor
import me.cephetir.skyskipped.utils.render.RenderUtils
import me.cephetir.skyskipped.utils.threading.BackgroundJob
import me.cephetir.skyskipped.utils.threading.BackgroundScope
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.tileentity.TileEntityCommandBlock
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.util.*

class TerminalEsp : Feature() {
    private var inBossFight = false
    private lateinit var job: BackgroundJob
    private var renderGreen = LinkedList<TileEntityCommandBlock>()
    private var renderRed = LinkedList<TileEntityCommandBlock>()

    private val greenColor = Color(0, 255, 20).rgb
    private val redColor = Color(255, 20, 0).rgb

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        inBossFight = false
        renderGreen.clear()
        renderRed.clear()
        stopScan()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.inDungeon || !Config.terminalEsp) return

        val msg = event.message.unformattedText.stripColor()
        if (msg.contains("[BOSS] Goldor: Little ants, plotting and scheming, thinking they are invincible…")) {
            inBossFight = true
            startScan()
        } else if (msg.contains("[BOSS] Necron: Goodbye.") && inBossFight) {
            inBossFight = false
            stopScan()
        }
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Cache.inDungeon || !Config.terminalEsp || !inBossFight) return

        for (block in renderGreen)
            RenderUtils.drawFilledBoundingBox(block.renderBoundingBox, greenColor)
        for (block in renderRed)
            RenderUtils.drawFilledBoundingBox(block.renderBoundingBox, redColor)
    }

    private fun startScan() {
        job = BackgroundScope.launchLooping("Termainl Esp Scan", 250L) {
            val renderGreen1 = LinkedList<TileEntityCommandBlock>()
            val renderRed1 = LinkedList<TileEntityCommandBlock>()
            val listEntitiesData1 = mc.theWorld.loadedEntityList.filterIsInstance<EntityArmorStand>()
            val listTileEntitiesData1 = mc.theWorld.loadedEntityList.filterIsInstance<TileEntityCommandBlock>()

            listTileEntitiesData1.forEach { i ->
                listEntitiesData1.forEach { i2 ->
                    if (i2.name.contains("Inactive Terminal")) {
                        if ((i2.posX - i.pos.x) > -3 && (i2.posX - i.pos.x) < 3 && (i2.posY - i.pos.y) > -3 && (i2.posY - i.pos.y) < 3 && (i2.posZ - i.pos.z) > -3 && (i2.posZ - i.pos.z) < 3)
                            renderRed1.push(i)
                    } else if (i2.name.contains("Terminal Active")) {
                        if ((i2.posX - i.pos.x) > -3 && (i2.posX - i.pos.x) < 3 && (i2.posY - i.pos.y) > -3 && (i2.posY - i.pos.y) < 3 && (i2.posZ - i.pos.z) > -3 && (i2.posZ - i.pos.z) < 3)
                            renderGreen1.push(i)
                    }
                }
            }

            renderGreen = renderGreen1
            renderRed = renderRed1
        }
    }

    private fun stopScan() {
        if (::job.isInitialized)
            BackgroundScope.cancel(job)
    }
}