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

import me.cephetir.skyskipped.mixins.IMixinLayerArmorBase
import me.cephetir.skyskipped.mixins.IMixinMinecraft
import me.cephetir.skyskipped.mixins.IMixinRenderManager
import me.cephetir.skyskipped.mixins.IMixinRendererLivingEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.layers.LayerArmorBase
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.item.ItemArmor
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin


object RenderUtils {
    private val mc = Minecraft.getMinecraft()
    private val frustrum = Frustum()

    @JvmStatic
    fun drawBox(pos: Vec3, color: Color, pt: Float) {
        val viewer: Entity = Minecraft.getMinecraft().renderViewEntity
        val viewerX: Double = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * pt
        val viewerY: Double = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * pt
        val viewerZ: Double = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * pt

        val x = pos.xCoord - 0.5 - viewerX
        val y = pos.yCoord - viewerY + 1.5
        val z = pos.zCoord - 0.5 - viewerZ

        drawFilledBoundingBox(AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), color, 0.6f)
    }

    @JvmStatic
    fun drawFilledBoundingBox(aabb: AxisAlignedBB, c: Color, alphaMultiplier: Float) {
        GlStateManager.disableDepth()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.color(
            c.red / 255f, c.green / 255f, c.blue / 255f,
            c.alpha / 255f * alphaMultiplier
        )
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.enableDepth()
        GlStateManager.enableCull()
    }

    fun renderStarredMobBoundingBox(entity: Entity, color: Int) {
        val rm = mc.renderManager as IMixinRenderManager
        val partialTicks = (mc as IMixinMinecraft).timer.renderPartialTicks.toDouble()
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ
        val entityBox = entity.entityBoundingBox
        val aabb = AxisAlignedBB.fromBounds(
            entityBox.minX - entity.posX + x - 0.4,
            entityBox.minY - entity.posY + y - if (entity.customNameTag.contains("Fels")) 3.15 else 2.1,
            entityBox.minZ - entity.posZ + z - 0.4,
            entityBox.maxX - entity.posX + x + 0.4,
            entityBox.maxY - entity.posY + y,
            entityBox.maxZ - entity.posZ + z + 0.4
        )
        drawFilledBoundingBox(aabb, color)
    }

    fun renderMiniBoundingBox(entity: Entity, color: Int) {
        val rm = mc.renderManager as IMixinRenderManager
        val partialTicks = (mc as IMixinMinecraft).timer.renderPartialTicks.toDouble()
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ
        val entityBox = entity.entityBoundingBox.expand(0.1, 0.1, 0.1)
        val aabb = AxisAlignedBB.fromBounds(
            entityBox.minX - entity.posX + x,
            entityBox.minY - entity.posY + y,
            entityBox.minZ - entity.posZ + z,
            entityBox.maxX - entity.posX + x,
            entityBox.maxY - entity.posY + y,
            entityBox.maxZ - entity.posZ + z
        )
        drawFilledBoundingBox(aabb, color)
    }

    fun renderKeyBoundingBox(entity: Entity, color: Int) {
        val rm = mc.renderManager as IMixinRenderManager
        val partialTicks = (mc as IMixinMinecraft).timer.renderPartialTicks.toDouble()
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ
        val bbox = entity.entityBoundingBox.contract(0.0, 0.5, 0.0)
        val aabb = AxisAlignedBB(
            bbox.minX - entity.posX + x,
            bbox.minY - entity.posY + y + 0.5,
            bbox.minZ - entity.posZ + z,
            bbox.maxX - entity.posX + x,
            bbox.maxY - entity.posY + y + 0.5,
            bbox.maxZ - entity.posZ + z
        )
        drawFilledBoundingBox(aabb, color)
    }

    fun renderBoundingBox(entity: Entity, color: Int) {
        val rm = mc.renderManager as IMixinRenderManager
        val partialTicks = (mc as IMixinMinecraft).timer.renderPartialTicks.toDouble()
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ
        val bbox = entity.entityBoundingBox
        var aabb = AxisAlignedBB(
            bbox.minX - entity.posX + x,
            bbox.minY - entity.posY + y,
            bbox.minZ - entity.posZ + z,
            bbox.maxX - entity.posX + x,
            bbox.maxY - entity.posY + y,
            bbox.maxZ - entity.posZ + z
        )
        if (entity is EntityArmorStand) aabb = aabb.expand(0.3, 2.0, 0.3)
        drawFilledBoundingBox(aabb, color)
    }

    fun renderSmallBox(vec: Vec3, color: Int) {
        val rm = mc.renderManager as IMixinRenderManager
        val renderPosX = rm.renderPosX
        val renderPosY = rm.renderPosY
        val renderPosZ = rm.renderPosZ
        val x = vec.xCoord - renderPosX
        val y = vec.yCoord - renderPosY
        val z = vec.zCoord - renderPosZ
        val aabb = AxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1)
        drawFilledBoundingBox(aabb, color)
    }

    private fun drawFilledBoundingBox(aabb: AxisAlignedBB, color: Int) {
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val a = (color shr 24 and 255).toFloat() / 255.0f
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        val opacity = 0.3f
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a * opacity)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.color(r, g, b, a)
        RenderGlobal.drawSelectionBoundingBox(aabb)
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun getChroma(speed: Float, offset: Int): Int {
        return Color.HSBtoRGB(
            ((System.currentTimeMillis() - offset.toLong() * 10L) % speed.toLong()).toFloat() / speed,
            0.88f,
            0.88f
        )
    }

    // Tenacity momento ez
    fun animate(endPoint: Float, current: Float, speed: Float): Float {
        val sped = speed.coerceIn(0f, 1f)
        val shouldContinueAnimation = endPoint > current
        val dif = endPoint.coerceAtLeast(current) - endPoint.coerceAtMost(current)
        val factor = dif * sped
        return current + if (shouldContinueAnimation) factor else -factor
    }

    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        if (left < right) {
            val i = left
            left = right
            right = i
        }
        if (top < bottom) {
            val j = top
            top = bottom
            bottom = j
        }
        val f3 = (color shr 24 and 255).toFloat() / 255.0f
        val f = (color shr 16 and 255).toFloat() / 255.0f
        val f1 = (color shr 8 and 255).toFloat() / 255.0f
        val f2 = (color and 255).toFloat() / 255.0f
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(f, f1, f2, f3)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    fun quickDrawRect(x: Float, y: Float, x2: Float, y2: Float) {
        glBegin(GL_QUADS)
        glVertex2d(x2.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y.toDouble())
        glVertex2d(x.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
    }

    fun fastRoundedRect(paramXStart: Float, paramYStart: Float, paramXEnd: Float, paramYEnd: Float, radius: Float) {
        var paramXStart = paramXStart
        var paramYStart = paramYStart
        var paramXEnd = paramXEnd
        var paramYEnd = paramYEnd
        var z: Float
        if (paramXStart > paramXEnd) {
            z = paramXStart
            paramXStart = paramXEnd
            paramXEnd = z
        }
        if (paramYStart > paramYEnd) {
            z = paramYStart
            paramYStart = paramYEnd
            paramYEnd = z
        }
        val x1 = (paramXStart + radius).toDouble()
        val y1 = (paramYStart + radius).toDouble()
        val x2 = (paramXEnd - radius).toDouble()
        val y2 = (paramYEnd - radius).toDouble()
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(1f)
        glBegin(GL_POLYGON)
        val degree = Math.PI / 180
        run {
            var i = 0.0
            while (i <= 90) {
                glVertex2d(x2 + sin(i * degree) * radius, y2 + cos(i * degree) * radius)
                i += 1.0
            }
        }
        run {
            var i = 90.0
            while (i <= 180) {
                glVertex2d(x2 + sin(i * degree) * radius, y1 + cos(i * degree) * radius)
                i += 1.0
            }
        }
        run {
            var i = 180.0
            while (i <= 270) {
                glVertex2d(x1 + sin(i * degree) * radius, y1 + cos(i * degree) * radius)
                i += 1.0
            }
        }
        var i = 270.0
        while (i <= 360) {
            glVertex2d(x1 + sin(i * degree) * radius, y2 + cos(i * degree) * radius)
            i += 1.0
        }
        glEnd()
        glDisable(GL_LINE_SMOOTH)
    }

    fun isInViewFrustrum(entity: Entity): Boolean {
        return isInViewFrustrum(entity.entityBoundingBox) || entity.ignoreFrustumCheck
    }

    private fun isInViewFrustrum(bb: AxisAlignedBB): Boolean {
        val current = mc.renderViewEntity
        frustrum.setPosition(current.posX, current.posY, current.posZ)
        return frustrum.isBoundingBoxInFrustum(bb)
    }

    fun drawOutlinedEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        val modelData = preModelDraw(entity, model, partialTicks)

        OutlineUtils.outlineEntity(
            model,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            partialTicks,
            color
        )

        postModelDraw()
    }

    fun drawChamsEsp(entity: EntityLivingBase, model: ModelBase, color: Int, partialTicks: Float) {
        val modelData = preModelDraw(entity, model, partialTicks)
        val f3 = (color shr 24 and 255).toFloat() / 255f
        val f = (color shr 16 and 255).toFloat() / 255f
        val f1 = (color shr 8 and 255).toFloat() / 255f
        val f2 = (color and 255).toFloat() / 255f

        GlStateManager.pushMatrix()
        // polygonOffsetLine
        glEnable(10754)
        GlStateManager.doPolygonOffset(1f, 1000000f)
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(f, f1, f2, f3)

        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)

        model.render(
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f
        )
        renderLayers(
            modelData.renderer,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            partialTicks,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            f, f1, f2, f3
        )

        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.color(f, f1, f2, f3)

        model.render(
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f
        )
        renderLayers(
            modelData.renderer,
            entity,
            modelData.limbSwing,
            modelData.limbSwingAmount,
            partialTicks,
            modelData.age,
            modelData.rotationYaw,
            modelData.rotationPitch,
            0.0625f,
            f, f1, f2, f3
        )

        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()

        GlStateManager.doPolygonOffset(1f, -1000000f)
        // polygonOffsetLine
        glDisable(10754)
        GlStateManager.popMatrix()

        postModelDraw()
    }

    private fun preModelDraw(entity: EntityLivingBase, model: ModelBase, partialTicks: Float): ModelData {
        val renderer = mc.renderManager.getEntityRenderObject<EntityLivingBase>(entity) as IMixinRendererLivingEntity
        val renderManager = mc.renderManager as IMixinRenderManager

        val renderYaw = renderer.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks)
        val prevYaw = renderer.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks)
        val rotationYaw = prevYaw - renderYaw
        val rotationPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        val limbSwing = entity.limbSwing - entity.limbSwingAmount * (1f - partialTicks)
        val limbSwingAmout = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks
        val age = renderer.handleRotationFloat(entity, partialTicks)

        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        model.swingProgress = entity.getSwingProgress(partialTicks)
        model.isChild = entity.isChild
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()
        GlStateManager.translate(x - renderManager.renderPosX, y - renderManager.renderPosY, z - renderManager.renderPosZ)
        val f = renderer.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks)
        renderer.rotateCorpse(entity, age, f, partialTicks)
        GlStateManager.enableRescaleNormal()
        GlStateManager.scale(-1f, -1f, 1f)
        renderer.preRenderCallback(entity, partialTicks)
        GlStateManager.translate(0.0f, -1.5078125f, 0.0f)
        model.setLivingAnimations(entity, limbSwing, limbSwingAmout, partialTicks)
        model.setRotationAngles(limbSwing, limbSwingAmout, age, rotationYaw, rotationPitch, 0.0625f, entity as Entity)

        return ModelData(renderer, rotationYaw, rotationPitch, limbSwing, limbSwingAmout, age)
    }

    private fun postModelDraw() {
        GlStateManager.resetColor()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.popMatrix()
    }

    fun renderLayers(
        renderer: IMixinRendererLivingEntity,
        entitylivingbaseIn: EntityLivingBase,
        p_177093_2_: Float,
        p_177093_3_: Float,
        partialTicks: Float,
        p_177093_5_: Float,
        p_177093_6_: Float,
        p_177093_7_: Float,
        p_177093_8_: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        for (layerrenderer in renderer.layerRenderers)
            if (layerrenderer is LayerArmorBase<*>)
                for (i in 1..4) {
                    val itemstack = entitylivingbaseIn.getCurrentArmor(i - 1)
                    if (itemstack == null || itemstack.item !is ItemArmor) continue

                    val armorModel = layerrenderer.getArmorModel(i)
                    armorModel.setModelAttributes(renderer.mainModel)
                    armorModel.setLivingAnimations(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks)
                    val layerrendererAccessor = layerrenderer as IMixinLayerArmorBase
                    layerrendererAccessor.setModelPartVisible(armorModel, i)

                    GlStateManager.color(red, green, blue, alpha)
                    armorModel.render(entitylivingbaseIn, p_177093_2_, p_177093_3_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_)
                }
    }

    data class ModelData(
        val renderer: IMixinRendererLivingEntity,
        val rotationYaw: Float,
        val rotationPitch: Float,
        val limbSwing: Float,
        val limbSwingAmount: Float,
        val age: Float
    )
}