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

package me.cephetir.skyskipped.utils.render

import me.cephetir.skyskipped.mixins.IMixinRendererLivingEntity
import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11

object OutlineUtils {
    fun outlineEntity(
        model: ModelBase,
        entity: EntityLivingBase,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        scaleFactor: Float,
        partialTicks: Float,
        color: Int
    ) {
        val renderer = mc.renderManager.getEntityRenderObject<EntityLivingBase>(entity) as IMixinRendererLivingEntity
        val fancyGraphics = mc.gameSettings.fancyGraphics
        val gamma = mc.gameSettings.gammaSetting
        mc.gameSettings.fancyGraphics = false
        mc.gameSettings.gammaSetting = Float.MAX_VALUE
        val f = (color shr 16 and 255).toFloat() / 255f
        val f1 = (color shr 8 and 255).toFloat() / 255f
        val f2 = (color and 255).toFloat() / 255f
        val f3 = (color shr 24 and 255).toFloat() / 255f
        GlStateManager.resetColor()
        setColor(f, f1, f2, f3)
        renderOne()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(f, f1, f2, f3)
        RenderUtils.renderLayers(
            renderer,
            entity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            headYaw,
            headPitch,
            scaleFactor,
            f, f1,f2, f3
        )
        setColor(f, f1, f2, f3)
        renderTwo()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(f, f1, f2, f3)
        RenderUtils.renderLayers(
            renderer,
            entity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            headYaw,
            headPitch,
            scaleFactor,
            f, f1,f2, f3
        )
        setColor(f, f1, f2, f3)
        renderThree()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        RenderUtils.renderLayers(
            renderer,
            entity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            headYaw,
            headPitch,
            scaleFactor,
            f, f1,f2, f3
        )
        setColor(f, f1, f2, f3)
        setColor(f, f1, f2, f3)
        renderFour()
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor)
        setColor(f, f1, f2, f3)
        RenderUtils.renderLayers(
            renderer,
            entity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            headYaw,
            headPitch,
            scaleFactor,
            f, f1,f2, f3
        )
        setColor(f, f1, f2, f3)
        renderFive()
        setColor(f, f1, f2, f3)
        mc.gameSettings.fancyGraphics = fancyGraphics
        mc.gameSettings.gammaSetting = gamma
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

    private fun renderFour() {
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

    private fun setColor(f: Float, f1: Float, f2: Float, f3: Float) {
        GlStateManager.color(f, f1, f2, f3)
    }

    private fun checkSetupFBO() {
        val fbo = mc.framebuffer
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
            mc.displayWidth,
            mc.displayHeight
        )
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferId)
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferId)
    }
}
