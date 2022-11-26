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