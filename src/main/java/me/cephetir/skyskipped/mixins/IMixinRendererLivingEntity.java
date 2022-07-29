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

package me.cephetir.skyskipped.mixins;

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
