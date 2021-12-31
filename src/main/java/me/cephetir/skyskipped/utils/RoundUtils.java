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

package me.cephetir.skyskipped.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;


public class RoundUtils {

    final static Minecraft mc = Minecraft.getMinecraft();
    final static FontRenderer fr = mc.fontRendererObj;

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    /*
     *
     * NORMAL
     *
     */

    /**
     * @param x      : X pos
     * @param y      : Y pos
     * @param x1     : X2 pos
     * @param y1     : Y2 pos
     * @param radius : round of edges;
     * @param color  : color;
     */

    public static void drawSmoothRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        glEnable(GL_LINE_SMOOTH);
        setColor(color);
        glEnable(2848);
        glBegin(GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glBegin(GL_LINE_LOOP);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glDisable(GL_LINE_SMOOTH);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        glEnable(GL_LINE_SMOOTH);
        setColor(color);
        glEnable(2848);
        glBegin(GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        GlStateManager.disableBlend();
        glDisable(2848);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.scale(2, 2, 2);
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    /**
     * @param x         : X pos
     * @param y         : Y pos
     * @param x1        : X2 pos
     * @param y1        : Y2 pos
     * @param radius    : round of edges;
     * @param lineWidth : width of outline line;
     * @param color     : color;
     */

    public static void drawRoundedOutline(float x, float y, float x1, float y1, float radius, float lineWidth, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        setColor(color);
        glEnable(2848);
        glLineWidth(lineWidth);
        glBegin(GL_LINE_LOOP);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        GlStateManager.disableBlend();
        glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.scale(2, 2, 2);
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    /*
     *
     * SELECTED EDGES
     *
     */

    /**
     * @param x       : X pos
     * @param y       : Y pos
     * @param x1      : X2 pos
     * @param y1      : Y2 pos
     * @param radius1 : round of left top edges;
     * @param radius2 : round of right top edges;
     * @param radius3 : round of left bottom edges;
     * @param radius4 : round of right bottom edges;
     * @param color   : color;
     */

    public static void drawSelectRoundedRect(float x, float y, float x1, float y1, float radius1, float radius2, float radius3, float radius4, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius1 + Math.sin(i * Math.PI / 180.0D) * radius1 * -1.0D, y + radius1 + Math.cos(i * Math.PI / 180.0D) * radius1 * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius2 + Math.sin(i * Math.PI / 180.0D) * radius2 * -1.0D, y1 - radius2 + Math.cos(i * Math.PI / 180.0D) * radius2 * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius3 + Math.sin(i * Math.PI / 180.0D) * radius3, y1 - radius3 + Math.cos(i * Math.PI / 180.0D) * radius3);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius4 + Math.sin(i * Math.PI / 180.0D) * radius4, y + radius4 + Math.cos(i * Math.PI / 180.0D) * radius4);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }

    /**
     * @param x         : X pos
     * @param y         : Y pos
     * @param x1        : X2 pos
     * @param y1        : Y2 pos
     * @param radius1   : round of left top edges;
     * @param radius2   : round of right top edges;
     * @param radius3   : round of left bottom edges;
     * @param radius4   : round of right bottom edges;
     * @param lineWidth : width of outline line;
     * @param color     : color;
     */

    public static void drawSelectRoundedOutline(float x, float y, float x1, float y1, float radius1, float radius2, float radius3, float radius4, float lineWidth, int color) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        glLineWidth(lineWidth);
        glBegin(GL_LINE_LOOP);
        int i;
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x + radius1 + Math.sin(i * Math.PI / 180.0D) * radius1 * -1.0D, y + radius1 + Math.cos(i * Math.PI / 180.0D) * radius1 * -1.0D);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x + radius2 + Math.sin(i * Math.PI / 180.0D) * radius2 * -1.0D, y1 - radius2 + Math.cos(i * Math.PI / 180.0D) * radius2 * -1.0D);
        for (i = 0; i <= 90; i += 3)
            glVertex2d(x1 - radius3 + Math.sin(i * Math.PI / 180.0D) * radius3, y1 - radius3 + Math.cos(i * Math.PI / 180.0D) * radius3);
        for (i = 90; i <= 180; i += 3)
            glVertex2d(x1 - radius4 + Math.sin(i * Math.PI / 180.0D) * radius4, y + radius4 + Math.cos(i * Math.PI / 180.0D) * radius4);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
    }

    public static void setColor(int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        glColor4f(r, g, b, a);
    }

    /*
     *
     * GRADIENT
     *
     */

    /**
     * @param x      : X pos
     * @param y      : Y pos
     * @param x1     : X2 pos
     * @param y1     : Y2 pos
     * @param radius : round of edges;
     * @param color  : color;
     * @param color2 : color2;
     * @param color3 : color3;
     * @param color4 : color4;
     */
    public static void drawRoundedGradientRectCorner(float x, float y, float x1, float y1, float radius, int color, int color2, int color3, int color4) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        glEnable(3042);
        glDisable(3553);
        setColor(color);
        glEnable(2848);
        GL11.glShadeModel(7425);
        glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            setColor(color);
        glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            setColor(color2);
        glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            setColor(color3);
        glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            setColor(color4);
        glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        glDisable(3042);
        glEnable(3553);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();


        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    /*NOT WORKING ATM*/
    /*
     *
     * IMAGE
     *
     */

/**
 *
 * @see dont worget to bind a texture to it!
 *
 * @param x : X pos
 * @param y : Y pos
 * @param x1 : X2 pos
 * @param y1 : Y2 pos
 * @param radius : round of edges;
 */
}