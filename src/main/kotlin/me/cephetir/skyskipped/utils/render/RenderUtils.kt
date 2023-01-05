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

package me.cephetir.skyskipped.utils.render

import me.cephetir.skyskipped.mixins.accessors.*
import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerArmorBase
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.item.ItemArmor
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


object RenderUtils {
    private val frustrum = Frustum()

    fun getViewerPos(partialTicks: Float): Triple<Double, Double, Double> {
        val viewer = mc.renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        return Triple(viewerX, viewerY, viewerZ)
    }

    fun drawBox(pos: Vec3, color: Color, pt: Float) {
        val viewer: Entity = mc.renderViewEntity
        val viewerX: Double = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * pt
        val viewerY: Double = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * pt
        val viewerZ: Double = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * pt

        val x = pos.xCoord - 0.5 - viewerX
        val y = pos.yCoord - viewerY + 1.5
        val z = pos.zCoord - 0.5 - viewerZ

        drawFilledBoundingBox(AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), color, 0.6f)
    }

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

    fun drawFilledBoundingBox(aabb: AxisAlignedBB, color: Int) {
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

    fun getChroma(speed: Float, offset: Int): Int = Color.HSBtoRGB(
        ((System.currentTimeMillis() - offset.toLong() * 10L) % speed.toLong()).toFloat() / speed,
        0.88f,
        0.88f
    )

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
        glVertex2f(x2, y)
        glVertex2f(x, y)
        glVertex2f(x, y2)
        glVertex2f(x2, y2)
        glEnd()
    }

    fun isInViewFrustrum(entity: Entity): Boolean {
        return isInViewFrustrum(entity.entityBoundingBox) || entity.ignoreFrustumCheck
    }

    fun isInViewFrustrum(bb: AxisAlignedBB): Boolean {
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
        val render = mc.renderManager.getEntityRenderObject<EntityLivingBase>(entity)
        val renderer = render as IMixinRendererLivingEntity
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
        model.setRotationAngles(limbSwing, limbSwingAmout, age, rotationYaw, rotationPitch, 0.0625f, entity)
        if (render is RenderPlayer && entity is AbstractClientPlayer) (render as IMixinRenderPlayer).setModelVisibilities(entity)

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
        if (entitylivingbaseIn !is EntitySkeleton) return
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

    fun drawCycle(
        x: Double,
        y: Double,
        z: Double,
        radius: Float,
        height: Float,
        color: Int,
        partialTicks: Float
    ) {
        val alpha = (color shr 24 and 0xFF) / 255.0f
        val r = (color shr 16 and 0xFF) / 255f
        val g = (color shr 8 and 0xFF) / 255f
        val b = (color and 0xFF) / 255f

        GlStateManager.pushMatrix()
        glNormal3f(0.0f, 1.0f, 0.0f)

        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.enableDepth()
        GlStateManager.enableBlend()
        GlStateManager.depthFunc(GL_LEQUAL)
        GlStateManager.disableCull()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.enableAlpha()
        GlStateManager.disableTexture2D()
        //GlStateManager.disableDepth()

        /*var il = 0.0
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        while (il < 0.05) {
            GlStateManager.pushMatrix()
            GlStateManager.disableTexture2D()
            glLineWidth(2F)
            worldrenderer.begin(1, DefaultVertexFormats.POSITION)
            val pix2 = Math.PI * 2.0
            for (i in 0..90) {
                GlStateManager.color(r, g, b, alpha)
                worldrenderer.pos(x + radius * cos(i * pix2 / 45.0), y + il, z + radius * sin(i * pix2 / 45.0)).endVertex()
            }
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.popMatrix()
            il += 0.0006
        }*/

        GlStateManager.color(r, g, b, alpha)
        var x1 = x
        var y1 = y
        var z1 = z
        val renderViewEntity = mc.renderViewEntity
        val viewX = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks.toDouble()
        val viewY = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks.toDouble()
        val viewZ = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks.toDouble()
        x1 -= viewX
        y1 -= viewY
        z1 -= viewZ
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(GL_QUAD_STRIP, DefaultVertexFormats.POSITION)
        var currentAngle = 0f
        val angleStep = 0.1f
        while (currentAngle < 2 * Math.PI) {
            val xOffset = radius * cos(currentAngle.toDouble()).toFloat()
            val zOffset = radius * sin(currentAngle.toDouble()).toFloat()
            worldrenderer.pos(x1 + xOffset, y1 + height, z1 + zOffset).endVertex()
            worldrenderer.pos(x1 + xOffset, y1 + 0, z1 + zOffset).endVertex()
            currentAngle += angleStep
        }
        worldrenderer.pos(x1 + radius, y1 + height, z1).endVertex()
        worldrenderer.pos(x1 + radius, y1 + 0.0, z1).endVertex()
        tessellator.draw()

        //GlStateManager.enableDepth()
        GlStateManager.enableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    fun drawBeaconBeam(entity: EntityLivingBase, color: Int) {
        val partialTicks = (mc as IMixinMinecraft).timer.renderPartialTicks
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks

        renderBeaconBeam(x, y, z, color, partialTicks)
    }

    private val beaconBeam = ResourceLocation("textures/entity/beacon_beam.png")
    fun renderBeaconBeam(x: Double, y: Double, z: Double, color: Int, partialTicks: Float) {
        val player = mc.thePlayer
        val playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks
        val playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks
        val playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks
        GlStateManager.pushMatrix()
        GlStateManager.translate(-playerX, -playerY, -playerZ)
        val height = 300
        val bottomOffset = 0
        val topOffset = bottomOffset + height
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        mc.textureManager.bindTexture(beaconBeam)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, 10497.0f)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, 10497.0f)
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val time = mc.theWorld.totalWorldTime + partialTicks.toDouble()
        val d1 = MathHelper.func_181162_h(-time * 0.2 - MathHelper.floor_double(-time * 0.1).toDouble())
        val alpha = (color shr 24 and 0xFF) / 255.0f
        val r = (color shr 16 and 0xFF) / 255f
        val g = (color shr 8 and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val d2 = time * 0.025 * -1.5
        val d4 = 0.5 + cos(d2 + 2.356194490192345) * 0.2
        val d5 = 0.5 + sin(d2 + 2.356194490192345) * 0.2
        val d6 = 0.5 + cos(d2 + PI / 4.0) * 0.2
        val d7 = 0.5 + sin(d2 + PI / 4.0) * 0.2
        val d8 = 0.5 + cos(d2 + 3.9269908169872414) * 0.2
        val d9 = 0.5 + sin(d2 + 3.9269908169872414) * 0.2
        val d10 = 0.5 + cos(d2 + 5.497787143782138) * 0.2
        val d11 = 0.5 + sin(d2 + 5.497787143782138) * 0.2
        val d14 = -1.0 + d1
        val d15 = height.toDouble() * 2.5 + d14
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0, d15).color(r, g, b, alpha).endVertex()
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0, d15).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        val d12 = -1.0 + d1
        val d13 = height + d12
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alpha).endVertex()
        tessellator.draw()
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }
}