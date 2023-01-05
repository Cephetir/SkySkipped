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

package me.cephetir.skyskipped.mixins.accessors;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(RendererLivingEntity.class)
public interface IMixinRendererLivingEntity {
    @Accessor("mainModel")
    ModelBase getMainModel();

    @Invoker("rotateCorpse")
    void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks);

    @Invoker("handleRotationFloat")
    float handleRotationFloat(EntityLivingBase livingBase, float partialTicks);

    @Invoker("interpolateRotation")
    float interpolateRotation(float par1, float par2, float par3);

    @Invoker("preRenderCallback")
    void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime);

    @Accessor("layerRenderers")
    List<LayerRenderer<EntityLivingBase>> getLayerRenderers();
}
