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

package me.cephetir.skyskipped.mixins.render;

import me.cephetir.bladecore.core.event.BladeEventBus;
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent;
import me.cephetir.skyskipped.mixins.accessors.IMixinRendererLivingEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    @Shadow
    public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);

    @Shadow
    public TextureManager renderEngine;

    @Inject(method = "doRenderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)Z", at = @At("HEAD"), cancellable = true)
    public void onRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof EntityLivingBase)) return;
        Render<Entity> render = this.getEntityRenderObject(entity);
        if (this.renderEngine == null || !(render instanceof RendererLivingEntity)) return;

        if (BladeEventBus.INSTANCE.post(new RenderEntityModelEvent((EntityLivingBase) entity, ((IMixinRendererLivingEntity) render).getMainModel(), partialTicks)))
            cir.cancel();
    }
}