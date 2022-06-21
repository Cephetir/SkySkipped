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

package me.cephetir.skyskipped.utils.render.shaders

import me.cephetir.skyskipped.mixins.IMixinMinecraft
import me.cephetir.skyskipped.mixins.IMixinShaderGroup
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12


object ShadowUtils {
    private var initialFB: Framebuffer? = null
    private var frameBuffer: Framebuffer? = null
    private var blackBuffer: Framebuffer? = null
    private var mainShader: ShaderGroup? = null
    private var lastWidth = 0f
    private var lastHeight = 0f
    private var lastStrength = 0f
    private val mc = Minecraft.getMinecraft()
    private val blurDirectory = ResourceLocation("skyskipped/shadow.json")

    private fun initBlur(sc: ScaledResolution, strength: Float) {
        val w = sc.scaledWidth
        val h = sc.scaledHeight
        val f = sc.scaleFactor
        if ((lastWidth != w.toFloat()) || lastHeight != h.toFloat() || initialFB == null || frameBuffer == null || mainShader == null) {
            initialFB = Framebuffer(w * f, h * f, true)
            initialFB!!.setFramebufferColor(0f, 0f, 0f, 0f)
            initialFB!!.setFramebufferFilter(GL11.GL_LINEAR)
            mainShader = ShaderGroup(mc.textureManager, mc.resourceManager, initialFB, blurDirectory)
            mainShader!!.createBindFramebuffers(w * f, h * f)
            frameBuffer = (mainShader!! as IMixinShaderGroup).mainFramebuffer
            blackBuffer = mainShader!!.getFramebufferRaw("braindead")
        }
        lastWidth = w.toFloat()
        lastHeight = h.toFloat()
        if (strength != lastStrength) {
            lastStrength = strength
            for (i in 0..1)
                (mainShader!! as IMixinShaderGroup).listShaders[i].shaderManager.getShaderUniform("Radius").set(strength)
        }
    }

    fun processShadow(begin: Boolean, strength: Float) {
        if (!OpenGlHelper.isFramebufferEnabled()) return
        val sc = ScaledResolution(mc)
        initBlur(sc, strength)
        if (begin) {
            mc.framebuffer.unbindFramebuffer()
            initialFB!!.framebufferClear()
            blackBuffer!!.framebufferClear()
            initialFB!!.bindFramebuffer(true)
        } else {
            frameBuffer!!.bindFramebuffer(true)
            mainShader!!.loadShaderGroup((mc as IMixinMinecraft).timer.renderPartialTicks)
            mc.framebuffer.bindFramebuffer(true)
            // Variables
            val f = sc.scaledWidth.toFloat()
            val f1 = sc.scaledHeight.toFloat()
            val f2 = blackBuffer!!.framebufferWidth.toFloat() / blackBuffer!!.framebufferTextureWidth.toFloat()
            val f3 = blackBuffer!!.framebufferHeight.toFloat() / blackBuffer!!.framebufferTextureHeight.toFloat()

            // Enable/Disable required things
            GlStateManager.pushMatrix()
            GlStateManager.disableLighting()
            GlStateManager.disableAlpha()
            GlStateManager.enableTexture2D()
            GlStateManager.disableDepth()
            GlStateManager.depthMask(false)
            GlStateManager.colorMask(true, true, true, true)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            blackBuffer!!.bindFramebufferTexture()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.worldRenderer
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
            worldrenderer.pos(0.0, f1.toDouble(), 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex()
            worldrenderer.pos(f.toDouble(), f1.toDouble(), 0.0).tex(f2.toDouble(), 0.0).color(255, 255, 255, 255).endVertex()
            worldrenderer.pos(f.toDouble(), 0.0, 0.0).tex(f2.toDouble(), f3.toDouble()).color(255, 255, 255, 255).endVertex()
            worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, f3.toDouble()).color(255, 255, 255, 255).endVertex()
            tessellator.draw()
            blackBuffer!!.unbindFramebufferTexture()
            GlStateManager.disableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.enableDepth()
            GlStateManager.depthMask(true)
            GlStateManager.enableTexture2D()
            GlStateManager.popMatrix()
            GlStateManager.resetColor()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(770, 771)
        }
    }
}