/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
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

//
// Decompiled by Procyon v0.5.36
// 

package me.cephetir.skyskipped.utils;

import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.listeners.events.RenderEntityModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class OutlineUtils {
    public static void outlineEntity(final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float headYaw, final float headPitch, final float scaleFactor) {
        final boolean fancyGraphics = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        final float gamma = Minecraft.getMinecraft().gameSettings.gammaSetting;
        Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
        Minecraft.getMinecraft().gameSettings.gammaSetting = Float.MAX_VALUE;
        GlStateManager.resetColor();
        setColor(Config.espColor);
        renderOne();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        setColor(Config.espColor);
        renderTwo();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        setColor(Config.espColor);
        renderThree();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        setColor(Config.espColor);
        renderFour(Config.espColor);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor);
        setColor(Config.espColor);
        renderFive();
        setColor(Config.espColor);
        Minecraft.getMinecraft().gameSettings.fancyGraphics = fancyGraphics;
        Minecraft.getMinecraft().gameSettings.gammaSetting = gamma;
    }

    public static void outlineEntity(final RenderEntityModelEvent event) {
        outlineEntity(event.model, event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.headYaw, event.headPitch, event.scaleFactor);
    }

    private static void renderOne() {
        checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth((float) 5);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    private static void renderTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    private static void renderThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    private static void renderFour(final Color color) {
        setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }

    private static void renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    private static void setColor(final Color color) {
        GL11.glColor4d(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    private static void checkSetupFBO() {
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencilDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferId);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferId);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferId);
    }
}
