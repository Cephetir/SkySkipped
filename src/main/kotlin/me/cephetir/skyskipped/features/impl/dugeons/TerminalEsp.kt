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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.threading.BackgroundJob
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.mixins.accessors.IMixinRenderManager
import me.cephetir.skyskipped.utils.render.RenderUtils
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.tileentity.TileEntityCommandBlock
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.util.*
import kotlin.math.abs

class TerminalEsp : Feature() {
    private var job: BackgroundJob? = null
    private var renderGreen = LinkedList<TileEntityCommandBlock>()
    private var renderRed = LinkedList<TileEntityCommandBlock>()

    private val greenColor = Color(0, 255, 20).rgb
    private val redColor = Color(255, 20, 0).rgb

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Unload) {
        renderGreen.clear()
        renderRed.clear()
        stopScan()
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.inDungeon || !Config.terminalEsp) return

        val msg = event.message.unformattedText.stripColor()
        if (msg.contains("[BOSS] Goldor: Little ants, plotting and scheming, thinking they are invincible…"))
            startScan()
        else if (msg.contains("[BOSS] Necron: Goodbye."))
            stopScan()
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!Cache.inDungeon || !Config.terminalEsp || job == null) return

        val rm = mc.renderManager as IMixinRenderManager
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ

        for (block in renderGreen) {
            val x = block.pos.x - renderPosX
            val y = block.pos.y - renderPosY
            val z = block.pos.z - renderPosZ
            val bbox = block.renderBoundingBox
            val aabb = AxisAlignedBB(
                bbox.minX - block.pos.x + x,
                bbox.minY - block.pos.y + y,
                bbox.minZ - block.pos.z + z,
                bbox.maxX - block.pos.x + x,
                bbox.maxY - block.pos.y + y,
                bbox.maxZ - block.pos.z + z
            )
            RenderUtils.drawFilledBoundingBox(aabb, greenColor)
        }
        for (block in renderRed) {
            val x = block.pos.x - renderPosX
            val y = block.pos.y - renderPosY
            val z = block.pos.z - renderPosZ
            val bbox = block.renderBoundingBox
            val aabb = AxisAlignedBB(
                bbox.minX - block.pos.x + x,
                bbox.minY - block.pos.y + y,
                bbox.minZ - block.pos.z + z,
                bbox.maxX - block.pos.x + x,
                bbox.maxY - block.pos.y + y,
                bbox.maxZ - block.pos.z + z
            )
            RenderUtils.drawFilledBoundingBox(aabb, redColor)
        }
    }

    private fun startScan() {
        job = BackgroundScope.launchLooping("Termainl Esp Scan", 250L) {
            printdev("Running scan")
            val renderGreen1 = LinkedList<TileEntityCommandBlock>()
            val renderRed1 = LinkedList<TileEntityCommandBlock>()
            val listEntitiesData1 = mc.theWorld.loadedEntityList.filterIsInstance<EntityArmorStand>()
            val listTileEntitiesData1 = mc.theWorld.loadedTileEntityList.filterIsInstance<TileEntityCommandBlock>()

            listTileEntitiesData1.forEach { i ->
                listEntitiesData1.forEach { i2 ->
                    printdev("Checking terminal")
                    if (abs(i2.posX - i.pos.x) < 3 && abs(i2.posY - i.pos.y) < 3 && abs(i2.posZ - i.pos.z) < 3)
                        when {
                            i2.name.stripColor().contains("Inactive Terminal") -> {
                                printdev("Detected inactive")
                                renderRed1.push(i)
                            }
                            i2.name.stripColor().contains("Terminal Active") -> {
                                printdev("Detected active")
                                renderGreen1.push(i)
                            }
                        }
                }
            }

            renderGreen = renderGreen1
            renderRed = renderRed1
        }
    }

    private fun stopScan() {
        if (job != null) {
            BackgroundScope.cancel(job!!)
            job = null
        }
    }
}