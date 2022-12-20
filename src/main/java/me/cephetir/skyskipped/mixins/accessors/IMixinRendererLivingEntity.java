/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
