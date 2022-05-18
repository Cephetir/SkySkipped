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

package me.cephetir.skyskipped.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.Shader
import net.minecraft.util.Matrix4f
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30


class BlurUtils {
    private class OutputStuff(var framebuffer: Framebuffer, blurShaderHorz: Shader?, blurShaderVert: Shader?) {
        var blurShaderHorz: Shader? = null
        var blurShaderVert: Shader? = null

        init {
            this.blurShaderHorz = blurShaderHorz
            this.blurShaderVert = blurShaderVert
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onScreenRender(event: RenderGameOverlayEvent.Pre) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) processBlurs()
        Minecraft.getMinecraft().framebuffer.bindFramebuffer(false)
    }

    @SubscribeEvent
    fun onFogColour(event: FogColors) {
        fogColour = -0x1000000
        fogColour = fogColour or ((event.red * 255).toInt() and 0xFF shl 16)
        fogColour = fogColour or ((event.green * 255).toInt() and 0xFF shl 8)
        fogColour = fogColour or ((event.blue * 255).toInt() and 0xFF)
    }

    companion object {
        private val blurOutput = HashMap<Float, OutputStuff>()
        private val lastBlurUse = HashMap<Float, Long>()
        private var lastBlur: Long = 0
        private val requestedBlurs = HashSet<Float>()
        private var fogColour = 0
        private var registered = false
        fun registerListener() {
            if (!registered) {
                registered = true
                MinecraftForge.EVENT_BUS.register(BlurUtils())
            }
        }

        private var shouldBlur = true
        fun markDirty() {
            if (Minecraft.getMinecraft().theWorld != null)
                shouldBlur = true
        }

        fun processBlurs() {
            if (shouldBlur) {
                shouldBlur = false
                val currentTime = System.currentTimeMillis()
                for (blur in requestedBlurs) {
                    lastBlur = currentTime
                    lastBlurUse[blur] = currentTime
                    val width = Minecraft.getMinecraft().displayWidth
                    val height = Minecraft.getMinecraft().displayHeight
                    val output = blurOutput.computeIfAbsent(blur) {
                        val fb = Framebuffer(width, height, false)
                        fb.setFramebufferFilter(GL11.GL_NEAREST)
                        OutputStuff(fb, null, null)
                    }
                    if (output.framebuffer.framebufferWidth != width || output.framebuffer.framebufferHeight != height) {
                        output.framebuffer.createBindFramebuffer(width, height)
                        if (output.blurShaderHorz != null)
                            output.blurShaderHorz!!.setProjectionMatrix(createProjectionMatrix(width, height))
                        if (output.blurShaderVert != null)
                            output.blurShaderVert!!.setProjectionMatrix(createProjectionMatrix(width, height))
                    }
                    blurBackground(output, blur)
                }
                val remove: MutableSet<Float> = HashSet()
                for ((key, value) in lastBlurUse) if (currentTime - value > 30 * 1000) remove.add(key)

                for ((key, value) in blurOutput) {
                    if (remove.contains(key)) {
                        value.framebuffer.deleteFramebuffer()
                        value.blurShaderHorz!!.deleteShader()
                        value.blurShaderVert!!.deleteShader()
                    }
                }
                lastBlurUse.keys.removeAll(remove)
                blurOutput.keys.removeAll(remove)
                requestedBlurs.clear()
            }
        }

        private var blurOutputHorz: Framebuffer? = null

        /**
         * Creates a projection matrix that projects from our coordinate space [0->width; 0->height] to OpenGL coordinate
         * space [-1 -> 1; 1 -> -1] (Note: flipped y-axis).
         *
         * This is so that we can render to and from the framebuffer in a way that is familiar to us, instead of needing to
         * apply scales and translations manually.
         */
        private fun createProjectionMatrix(width: Int, height: Int): Matrix4f {
            val projMatrix = Matrix4f()
            projMatrix.setIdentity()
            projMatrix.m00 = 2.0f / width.toFloat()
            projMatrix.m11 = 2.0f / (-height).toFloat()
            projMatrix.m22 = -0.0020001999f
            projMatrix.m33 = 1.0f
            projMatrix.m03 = -1.0f
            projMatrix.m13 = 1.0f
            projMatrix.m23 = -1.0001999f
            return projMatrix
        }

        private const val lastBgBlurFactor = -1.0
        private fun blurBackground(output: OutputStuff?, blurFactor: Float) {
            if (!OpenGlHelper.isFramebufferEnabled() || !OpenGlHelper.areShadersSupported()) return
            val width = Minecraft.getMinecraft().displayWidth
            val height = Minecraft.getMinecraft().displayHeight
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.loadIdentity()
            GlStateManager.ortho(0.0, width.toDouble(), height.toDouble(), 0.0, 1000.0, 3000.0)
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.loadIdentity()
            GlStateManager.translate(0.0f, 0.0f, -2000.0f)
            if (blurOutputHorz == null) {
                blurOutputHorz = Framebuffer(width, height, false)
                blurOutputHorz!!.setFramebufferFilter(GL11.GL_NEAREST)
            }
            if (blurOutputHorz == null || output == null) return
            if (blurOutputHorz!!.framebufferWidth != width || blurOutputHorz!!.framebufferHeight != height) {
                blurOutputHorz!!.createBindFramebuffer(width, height)
                Minecraft.getMinecraft().framebuffer.bindFramebuffer(false)
            }
            if (output.blurShaderHorz == null)
                try {
                    output.blurShaderHorz = Shader(
                        Minecraft.getMinecraft().resourceManager, "blur",
                        output.framebuffer, blurOutputHorz
                    )
                    output.blurShaderHorz!!.shaderManager.getShaderUniform("BlurDir")[1f] = 0f
                    output.blurShaderHorz!!.setProjectionMatrix(createProjectionMatrix(width, height))
                } catch (_: Exception) {
                }
            if (output.blurShaderVert == null)
                try {
                    output.blurShaderVert = Shader(
                        Minecraft.getMinecraft().resourceManager, "blur",
                        blurOutputHorz, output.framebuffer
                    )
                    output.blurShaderVert!!.shaderManager.getShaderUniform("BlurDir")[0f] = 1f
                    output.blurShaderVert!!.setProjectionMatrix(createProjectionMatrix(width, height))
                } catch (_: Exception) {
                }
            if (output.blurShaderHorz != null && output.blurShaderVert != null) {
                if (output.blurShaderHorz!!.shaderManager.getShaderUniform("Radius") == null)
                    //Corrupted shader?
                    return
                output.blurShaderHorz!!.shaderManager.getShaderUniform("Radius").set(blurFactor)
                output.blurShaderVert!!.shaderManager.getShaderUniform("Radius").set(blurFactor)
                GL11.glPushMatrix()
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().framebuffer.framebufferObject)
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, output.framebuffer.framebufferObject)
                GL30.glBlitFramebuffer(
                    0, 0, width, height,
                    0, 0, output.framebuffer.framebufferWidth, output.framebuffer.framebufferHeight,
                    GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST
                )
                output.blurShaderHorz!!.loadShader(0f)
                output.blurShaderVert!!.loadShader(0f)
                GlStateManager.enableDepth()
                GL11.glPopMatrix()
            }
            Minecraft.getMinecraft().framebuffer.bindFramebuffer(false)
        }

        @JvmOverloads
        fun renderBlurredBackground(
            blurStrength: Float, screenWidth: Float, screenHeight: Float,
            x: Float, y: Float, blurWidth: Float, blurHeight: Float, forcedUpdate: Boolean = false
        ) {
            if (!OpenGlHelper.isFramebufferEnabled() || !OpenGlHelper.areShadersSupported()) return
            if (blurStrength < 0.5) return
            requestedBlurs.add(blurStrength)
            shouldBlur = true
            if (blurOutput.isEmpty()) return
            var out = blurOutput[blurStrength]
            if (out == null) out = blurOutput.values.iterator().next()
            val uMin = x / screenWidth
            val uMax = (x + blurWidth) / screenWidth
            val vMin = (screenHeight - y) / screenHeight
            val vMax = (screenHeight - y - blurHeight) / screenHeight
            GlStateManager.depthMask(false)
            Gui.drawRect(x.toInt(), y.toInt(), x.toInt() + blurWidth.toInt(), y.toInt() + blurHeight.toInt(), fogColour)
            out.framebuffer.bindFramebufferTexture()
            GlStateManager.color(1f, 1f, 1f, 1f)
            drawTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax)
            out.framebuffer.unbindFramebufferTexture()
            GlStateManager.depthMask(true)
        }

        private fun drawTexturedRect(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
            uMin: Float,
            uMax: Float,
            vMin: Float,
            vMax: Float
        ) {
            drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_NEAREST)
        }

        private fun drawTexturedRect(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
            uMin: Float,
            uMax: Float,
            vMin: Float,
            vMax: Float,
            filter: Int
        ) {
            GlStateManager.enableBlend()
            GL14.glBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ONE_MINUS_SRC_ALPHA
            )
            drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter)
            GlStateManager.disableBlend()
        }

        private fun drawTexturedRectNoBlend(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
            uMin: Float,
            uMax: Float,
            vMin: Float,
            vMax: Float,
            filter: Int
        ) {
            GlStateManager.enableTexture2D()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.worldRenderer
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
            worldrenderer
                .pos(x.toDouble(), (y + height).toDouble(), 0.0)
                .tex(uMin.toDouble(), vMax.toDouble()).endVertex()
            worldrenderer
                .pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
                .tex(uMax.toDouble(), vMax.toDouble()).endVertex()
            worldrenderer
                .pos((x + width).toDouble(), y.toDouble(), 0.0)
                .tex(uMax.toDouble(), vMin.toDouble()).endVertex()
            worldrenderer
                .pos(x.toDouble(), y.toDouble(), 0.0)
                .tex(uMin.toDouble(), vMin.toDouble()).endVertex()
            tessellator.draw()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        }
    }
}