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

package me.cephetir.skyskipped.utils

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import me.cephetir.bladecore.utils.mc
import me.cephetir.bladecore.utils.player
import net.minecraft.entity.Entity
import net.minecraft.util.EntitySelectors
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object RaytracingUtils {
    fun raytraceFromVec(vec: Vec3, rotation: RotationClass.Rotation, reach: Double): MovingObjectPosition? {
        val vec3 = Vec3(vec.xCoord, vec.yCoord, vec.zCoord)
        val vec31 = this.getVectorForRotation(rotation)
        val vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach)
        return player!!.worldObj.rayTraceBlocks(vec3, vec32, false, true, false)
    }

    fun raytraceFromEntity(entity: Entity, rotation: RotationClass.Rotation = RotationClass.Rotation(entity.rotationYaw, entity.rotationPitch), reach: Double): MovingObjectPosition? {
        val vec3 = entity.getPositionEyes(1f)
        val vec31 = this.getVectorForRotation(rotation)
        val vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach)
        return entity.worldObj.rayTraceBlocks(vec3, vec32, false, true, false)
    }

    fun raytraceToBlock(rotation: RotationClass.Rotation = RotationClass.Rotation(player!!.rotationYaw, player!!.rotationPitch), reach: Double): MovingObjectPosition? {
        val vec3 = player!!.getPositionEyes(1f)
        val vec31 = this.getVectorForRotation(rotation)
        val vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach)
        return player!!.worldObj.rayTraceBlocks(vec3, vec32, false, true, false)
    }

    fun raytrace(rotation: RotationClass.Rotation = RotationClass.Rotation(player!!.rotationYaw, player!!.rotationPitch), reach: Double): MovingObjectPosition? {
        var d1 = reach
        val vec3 = player!!.getPositionEyes(1f)
        val ray = raytraceToBlock(rotation, reach)
        if (ray != null) d1 = ray.hitVec.distanceTo(vec3)
        val vec31 = getVectorForRotation(rotation)
        val vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach)
        var pointedEntity: Entity? = null
        var vec33: Vec3? = null
        val f = 1f
        val list = mc.theWorld
            .getEntitiesInAABBexcluding(
                player!!,
                player!!.entityBoundingBox.addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand(f.toDouble(), f.toDouble(), f.toDouble()),
                Predicates.and(EntitySelectors.NOT_SPECTATING, Predicate { input -> input?.canBeCollidedWith() == true })
            )
        var d2 = d1

        for (j in list.indices) {
            val entity1 = list[j]
            val f1 = entity1.collisionBorderSize
            val axisalignedbb = entity1.entityBoundingBox.expand(f1.toDouble(), f1.toDouble(), f1.toDouble())
            val movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32)
            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0) {
                    pointedEntity = entity1
                    vec33 = if (movingobjectposition == null) vec3 else movingobjectposition.hitVec
                    d2 = 0.0
                }
            } else if (movingobjectposition != null) {
                val d3 = vec3.distanceTo(movingobjectposition.hitVec)
                if (d3 < d2 || d2 == 0.0) {
                    if (entity1 != player!!.ridingEntity || player!!.canRiderInteract()) {
                        pointedEntity = entity1
                        vec33 = movingobjectposition.hitVec
                        d2 = d3
                    } else if (d2 == 0.0) {
                        pointedEntity = entity1
                        vec33 = movingobjectposition.hitVec
                    }
                }
            }
        }

        return if (pointedEntity == null) ray else MovingObjectPosition(pointedEntity, vec33)
    }

    fun getVectorForRotation(rotation: RotationClass.Rotation): Vec3 {
        val f = cos(-rotation.yaw * (PI / 180.0) - PI)
        val f1 = sin(-rotation.yaw * (PI / 180.0) - PI)
        val f2 = -cos(-rotation.pitch * (PI / 180.0))
        val f3 = sin(-rotation.pitch * (PI / 180.0))
        return Vec3(f1 * f2, f3, f * f2)
    }
}