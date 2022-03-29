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

import me.cephetir.skyskipped.event.events.RenderEntityModelEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.Entity
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11
import java.awt.Color

object OutlineUtils {
    private fun outlineEntity(
        model: ModelBase,
        entity: Entity?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        scaleFactor: Float
    ) {
        val fancyGraphics = Minecraft.getMinecraft().gameSettings.fancyGraphics
        val gamma = Minecraft.getMinecraft().gameSettings.gammaSetting
        Minecraft.getMinecraft().gameSettings.fancyGraphics = false
        Minecraft.getMinecraft().gameSettings.gammaSetting = Float.MAX_VALUE
        GlStateManager.resetColor()
        setColor(Color.GREEN)
        renderOne()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(Color.GREEN)
        renderTwo()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(Color.GREEN)
        renderThree()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(Color.GREEN)
        renderFour(Color.GREEN)
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(Color.GREEN)
        renderFive()
        setColor(Color.GREEN)
        Minecraft.getMinecraft().gameSettings.fancyGraphics = fancyGraphics
        Minecraft.getMinecraft().gameSettings.gammaSetting = gamma
    }

    fun outlineEntity(event: RenderEntityModelEvent) {
        outlineEntity(
            event.model,
            event.entity,
            event.limbSwing,
            event.limbSwingAmount,
            event.ageInTicks,
            event.headYaw,
            event.headPitch,
            event.scaleFactor
        )
    }

    private fun renderOne() {
        checkSetupFBO()
        GL11.glPushAttrib(1048575)
        GL11.glDisable(3008)
        GL11.glDisable(3553)
        GL11.glDisable(2896)
        GL11.glEnable(3042)
        GL11.glBlendFunc(770, 771)
        GL11.glLineWidth(5f)
        GL11.glEnable(2848)
        GL11.glEnable(2960)
        GL11.glClear(1024)
        GL11.glClearStencil(15)
        GL11.glStencilFunc(512, 1, 15)
        GL11.glStencilOp(7681, 7681, 7681)
        GL11.glPolygonMode(1032, 6913)
    }

    private fun renderTwo() {
        GL11.glStencilFunc(512, 0, 15)
        GL11.glStencilOp(7681, 7681, 7681)
        GL11.glPolygonMode(1032, 6914)
    }

    private fun renderThree() {
        GL11.glStencilFunc(514, 1, 15)
        GL11.glStencilOp(7680, 7680, 7680)
        GL11.glPolygonMode(1032, 6913)
    }

    private fun renderFour(color: Color) {
        setColor(color)
        GL11.glDepthMask(false)
        GL11.glDisable(2929)
        GL11.glEnable(10754)
        GL11.glPolygonOffset(1.0f, -2000000.0f)
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f)
    }

    private fun renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f)
        GL11.glDisable(10754)
        GL11.glEnable(2929)
        GL11.glDepthMask(true)
        GL11.glDisable(2960)
        GL11.glDisable(2848)
        GL11.glHint(3154, 4352)
        GL11.glEnable(3042)
        GL11.glEnable(2896)
        GL11.glEnable(3553)
        GL11.glEnable(3008)
        GL11.glPopAttrib()
    }

    private fun setColor(color: Color) {
        GL11.glColor4d(
            (color.red / 255.0f).toDouble(),
            (color.green / 255.0f).toDouble(),
            (color.blue / 255.0f).toDouble(),
            (color.alpha / 255.0f).toDouble()
        )
    }

    private fun checkSetupFBO() {
        val fbo = Minecraft.getMinecraft().framebuffer
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo)
            fbo.depthBuffer = -1
        }
    }

    private fun setupFBO(fbo: Framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer)
        val stencilDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT()
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferId)
        EXTFramebufferObject.glRenderbufferStorageEXT(
            36161,
            34041,
            Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight
        )
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferId)
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferId)
    }
}
