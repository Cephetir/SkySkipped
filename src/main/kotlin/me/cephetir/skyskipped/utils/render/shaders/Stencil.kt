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