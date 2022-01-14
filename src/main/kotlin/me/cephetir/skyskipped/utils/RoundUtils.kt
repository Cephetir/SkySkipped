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
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

object RoundUtils {
    val mc = Minecraft.getMinecraft()!!
    val fr = mc.fontRendererObj
    
    fun enableGL2D() {
        GL11.glDisable(2929)
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glDepthMask(true)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glHint(3155, 4354)
    }

    fun disableGL2D() {
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glEnable(2929)
        GL11.glDisable(2848)
        GL11.glHint(3154, 4352)
        GL11.glHint(3155, 4352)
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
    @JvmStatic
    fun drawSmoothRoundedRect(x: Float, y: Float, x1: Float, y1: Float, radius: Float, color: Int) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GL11.glPushAttrib(0)
        GL11.glScaled(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        setColor(color)
        GL11.glEnable(2848)
        GL11.glBegin(GL11.GL_POLYGON)
        var i: Int = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y + radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y1 - radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y1 - radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y + radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        GL11.glEnd()
        GL11.glBegin(GL11.GL_LINE_LOOP)
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y + radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y1 - radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y1 - radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y + radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glDisable(3042)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(3553)
        GL11.glScaled(2.0, 2.0, 2.0)
        GL11.glPopAttrib()
    }

    @JvmStatic
    fun drawRoundedRect(x: Float, y: Float, x1: Float, y1: Float, radius: Float, color: Int) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        setColor(color)
        GL11.glEnable(2848)
        GL11.glBegin(GL11.GL_POLYGON)
        var i: Int = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y + radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y1 - radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y1 - radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y + radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        GL11.glEnd()
        GlStateManager.disableBlend()
        GL11.glDisable(2848)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GlStateManager.enableTexture2D()
        GlStateManager.scale(2f, 2f, 2f)
        GlStateManager.resetColor()
        GlStateManager.popMatrix()
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
    @JvmStatic
    fun drawRoundedOutline(x: Float, y: Float, x1: Float, y1: Float, radius: Float, lineWidth: Float, color: Int) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        setColor(color)
        GL11.glEnable(2848)
        GL11.glLineWidth(lineWidth)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        var i: Int = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y + radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
                y1 - radius + cos(i * Math.PI / 180.0) * radius * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y1 - radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius + sin(i * Math.PI / 180.0) * radius,
                y + radius + cos(i * Math.PI / 180.0) * radius
            )
            i += 3
        }
        GL11.glEnd()
        GlStateManager.disableBlend()
        GL11.glDisable(2848)
        GlStateManager.enableTexture2D()
        GlStateManager.scale(2f, 2f, 2f)
        GlStateManager.resetColor()
        GlStateManager.popMatrix()
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
    @JvmStatic
    fun drawSelectRoundedRect(
        x: Float,
        y: Float,
        x1: Float,
        y1: Float,
        radius1: Float,
        radius2: Float,
        radius3: Float,
        radius4: Float,
        color: Int
    ) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GL11.glPushAttrib(0)
        GL11.glScaled(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        setColor(color)
        GL11.glEnable(2848)
        GL11.glBegin(9)
        var i: Int = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius1 + sin(i * Math.PI / 180.0) * radius1 * -1.0,
                y + radius1 + cos(i * Math.PI / 180.0) * radius1 * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius2 + sin(i * Math.PI / 180.0) * radius2 * -1.0,
                y1 - radius2 + cos(i * Math.PI / 180.0) * radius2 * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius3 + sin(i * Math.PI / 180.0) * radius3,
                y1 - radius3 + cos(i * Math.PI / 180.0) * radius3
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius4 + sin(i * Math.PI / 180.0) * radius4,
                y + radius4 + cos(i * Math.PI / 180.0) * radius4
            )
            i += 3
        }
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glDisable(3042)
        GL11.glEnable(3553)
        GL11.glScaled(2.0, 2.0, 2.0)
        GL11.glPopAttrib()
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
    @JvmStatic
    fun drawSelectRoundedOutline(
        x: Float,
        y: Float,
        x1: Float,
        y1: Float,
        radius1: Float,
        radius2: Float,
        radius3: Float,
        radius4: Float,
        lineWidth: Float,
        color: Int
    ) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GL11.glPushAttrib(0)
        GL11.glScaled(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        setColor(color)
        GL11.glEnable(2848)
        GL11.glLineWidth(lineWidth)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        var i: Int = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x + radius1 + sin(i * Math.PI / 180.0) * radius1 * -1.0,
                y + radius1 + cos(i * Math.PI / 180.0) * radius1 * -1.0
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x + radius2 + sin(i * Math.PI / 180.0) * radius2 * -1.0,
                y1 - radius2 + cos(i * Math.PI / 180.0) * radius2 * -1.0
            )
            i += 3
        }
        i = 0
        while (i <= 90) {
            GL11.glVertex2d(
                x1 - radius3 + sin(i * Math.PI / 180.0) * radius3,
                y1 - radius3 + cos(i * Math.PI / 180.0) * radius3
            )
            i += 3
        }
        i = 90
        while (i <= 180) {
            GL11.glVertex2d(
                x1 - radius4 + sin(i * Math.PI / 180.0) * radius4,
                y + radius4 + cos(i * Math.PI / 180.0) * radius4
            )
            i += 3
        }
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glDisable(3042)
        GL11.glEnable(3553)
        GL11.glScaled(2.0, 2.0, 2.0)
        GL11.glPopAttrib()
    }

    fun setColor(color: Int) {
        val a = (color shr 24 and 0xFF) / 255.0f
        val r = (color shr 16 and 0xFF) / 255.0f
        val g = (color shr 8 and 0xFF) / 255.0f
        val b = (color and 0xFF) / 255.0f
        GL11.glColor4f(r, g, b, a)
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
    @JvmStatic
    fun drawRoundedGradientRectCorner(
        x: Float,
        y: Float,
        x1: Float,
        y1: Float,
        radius: Float,
        color: Int,
        color2: Int,
        color3: Int,
        color4: Int
    ) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        GL11.glPushAttrib(0)
        GL11.glScaled(0.5, 0.5, 0.5)
        x *= 2.0.toFloat()
        y *= 2.0.toFloat()
        x1 *= 2.0.toFloat()
        y1 *= 2.0.toFloat()
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        setColor(color)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        GL11.glBegin(9)
        var i: Int = 0
        while (i <= 90) {
            setColor(color)
            i += 3
        }
        GL11.glVertex2d(
            x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
            y + radius + cos(i * Math.PI / 180.0) * radius * -1.0
        )
        i = 90
        while (i <= 180) {
            setColor(color2)
            i += 3
        }
        GL11.glVertex2d(
            x + radius + sin(i * Math.PI / 180.0) * radius * -1.0,
            y1 - radius + cos(i * Math.PI / 180.0) * radius * -1.0
        )
        i = 0
        while (i <= 90) {
            setColor(color3)
            i += 3
        }
        GL11.glVertex2d(
            x1 - radius + sin(i * Math.PI / 180.0) * radius,
            y1 - radius + cos(i * Math.PI / 180.0) * radius
        )
        i = 90
        while (i <= 180) {
            setColor(color4)
            i += 3
        }
        GL11.glVertex2d(
            x1 - radius + sin(i * Math.PI / 180.0) * radius,
            y + radius + cos(i * Math.PI / 180.0) * radius
        )
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glDisable(3042)
        GL11.glEnable(3553)
        GL11.glScaled(2.0, 2.0, 2.0)
        GL11.glPopAttrib()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
    }
}