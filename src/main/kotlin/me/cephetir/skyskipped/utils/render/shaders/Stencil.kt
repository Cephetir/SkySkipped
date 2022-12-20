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

package me.cephetir.skyskipped.utils.render.shaders

import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11

object Stencil {

    fun dispose() {
        GL11.glDisable(GL11.GL_STENCIL_TEST)
        GlStateManager.disableAlpha()
        GlStateManager.disableBlend()
    }

    fun erase(invert: Boolean) {
        GL11.glStencilFunc(if (invert) GL11.GL_EQUAL else GL11.GL_NOTEQUAL, 1, 65535)
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f)
    }

    fun write(renderClipLayer: Boolean) {
        checkSetupFBO()
        GL11.glClearStencil(0)
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT)
        GL11.glEnable(GL11.GL_STENCIL_TEST)
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 65535)
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE)
        if (!renderClipLayer) GlStateManager.colorMask(false, false, false, false)
    }

    fun write(renderClipLayer: Boolean, fb: Framebuffer?) {
        checkSetupFBO(fb)
        GL11.glClearStencil(0)
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT)
        GL11.glEnable(GL11.GL_STENCIL_TEST)
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 65535)
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE)
        if (!renderClipLayer) GlStateManager.colorMask(false, false, false, false)
    }

    fun checkSetupFBO() {
        val fbo = mc.framebuffer
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo)
            fbo.depthBuffer = -1
        }
    }

    fun checkSetupFBO(fbo: Framebuffer?) {
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo)
            fbo.depthBuffer = -1
        }
    }

    fun setupFBO(fbo: Framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer)
        val stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT()
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID)
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight)
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID)
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID)
    }
}