/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.utils.render.shaders

import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.mixins.accessors.IMixinMinecraft
import me.cephetir.skyskipped.mixins.accessors.IMixinShaderGroup
import me.cephetir.skyskipped.utils.mc
import me.cephetir.skyskipped.utils.render.RenderUtils
import me.cephetir.skyskipped.utils.render.RoundUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.util.ResourceLocation

object BlurUtils {
    private val shaderGroup = ShaderGroup(mc.textureManager, mc.resourceManager, mc.framebuffer, ResourceLocation("shaders/post/blurArea.json"))
    private val framebuffer = (shaderGroup as IMixinShaderGroup).mainFramebuffer
    private val frbuffer = shaderGroup.getFramebufferRaw("result")

    private var lastFactor = 0
    private var lastWidth = 0
    private var lastHeight = 0

    private var lastX = 0F
    private var lastY = 0F
    private var lastW = 0F
    private var lastH = 0F

    private var lastStrength = 5F

    private fun setupFramebuffers() {
        try {
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
        } catch (e : Exception) {
            SkySkipped.logger.error("Exception caught while setting up shader group", e)
        }
    }

    private fun setValues(strength: Float, x: Float, y: Float, w: Float, h: Float, width: Float, height: Float) {
        if (strength == lastStrength && lastX == x && lastY == y && lastW == w && lastH == h) return
        lastStrength = strength
        lastX = x
        lastY = y
        lastW = w
        lastH = h

        val shaderGroupAccessor = shaderGroup as IMixinShaderGroup
        for (i in 0..1) {
            shaderGroupAccessor.listShaders[i].shaderManager.getShaderUniform("Radius").set(strength)
            shaderGroupAccessor.listShaders[i].shaderManager.getShaderUniform("BlurXY")[x] = height - y - h
            shaderGroupAccessor.listShaders[i].shaderManager.getShaderUniform("BlurCoord")[w] = h
        }
    }

    @JvmStatic
    fun blur(posX: Float, posY: Float, posXEnd: Float, posYEnd: Float, blurStrength: Float, displayClipMask: Boolean, triggerMethod: () -> Unit) {
        if (!OpenGlHelper.isFramebufferEnabled()) return

        var x = posX
        var y = posY
        var x2 = posXEnd
        var y2 = posYEnd

        if (x > x2) {
            val z = x
            x = x2
            x2 = z
        }

        if (y > y2) {
            val z = y
            y = y2
            y2 = z
        }

        val sc = ScaledResolution(mc)
        val scaleFactor = sc.scaleFactor
        val width = sc.scaledWidth
        val height = sc.scaledHeight

        if (sizeHasChanged(scaleFactor, width, height))
            setupFramebuffers()
        //GlStateManager.ortho(0.0, mc.displayWidth.toDouble(), mc.displayHeight.toDouble(), 0.0, 1000.0, 3000.0)

        lastFactor = scaleFactor
        lastWidth = width
        lastHeight = height

        setValues(blurStrength, x, y, x2 - x, y2 - y, width.toFloat(), height.toFloat())

        framebuffer.bindFramebuffer(true)
        shaderGroup.loadShaderGroup((mc as IMixinMinecraft).timer.renderPartialTicks)
        mc.framebuffer.bindFramebuffer(true)

        Stencil.write(displayClipMask)
        triggerMethod()

        Stencil.erase(true)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.pushMatrix()
        GlStateManager.colorMask(true, true, true, false)
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.enableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableAlpha()
        frbuffer.bindFramebufferTexture()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        val f2 = frbuffer.framebufferWidth.toDouble() / frbuffer.framebufferTextureWidth.toDouble()
        val f3 = frbuffer.framebufferHeight.toDouble() / frbuffer.framebufferTextureHeight.toDouble()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(0.0, height.toDouble(), 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(width.toDouble(), height.toDouble(), 0.0).tex(f2, 0.0).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(width.toDouble(), 0.0, 0.0).tex(f2, f3).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, f3).color(255, 255, 255, 255).endVertex()
        tessellator.draw()
        frbuffer.unbindFramebufferTexture()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()

        Stencil.dispose()
        GlStateManager.enableAlpha()
        //GlStateManager.ortho(0.0, sc.scaledWidth_double, sc.scaledHeight_double, 0.0, 1000.0, 3000.0)
    }

    @JvmStatic
    fun blurArea(x: Float, y: Float, x2: Float, y2: Float, blurStrength: Float) = blur(x, y, x2, y2, blurStrength, false) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(x, y, x2, y2)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun blurAreaRounded(x: Float, y: Float, x2: Float, y2: Float, rad: Float, blurStrength: Float) = blur(x, y, x2, y2, blurStrength, false) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RoundUtils.drawRoundedRect(x, y, x2, y2, rad)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    private fun sizeHasChanged(scaleFactor: Int, width: Int, height: Int): Boolean = lastFactor != scaleFactor || lastWidth != width || lastHeight != height
}