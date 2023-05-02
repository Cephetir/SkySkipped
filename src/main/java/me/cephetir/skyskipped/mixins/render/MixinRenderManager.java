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
    public TextureManager renderEngine;

    @Shadow
    public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);

    @Inject(method = "doRenderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)Z", at = @At("HEAD"), cancellable = true)
    public void onRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof EntityLivingBase)) return;
        Render<Entity> render = this.getEntityRenderObject(entity);
        if (this.renderEngine == null || !(render instanceof RendererLivingEntity)) return;

        if (BladeEventBus.INSTANCE.post(new RenderEntityModelEvent((EntityLivingBase) entity, ((IMixinRendererLivingEntity) render).getMainModel(), partialTicks)))
            cir.setReturnValue(false);
    }
}