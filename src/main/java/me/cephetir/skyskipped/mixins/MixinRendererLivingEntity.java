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

import com.mojang.realmsclient.gui.ChatFormatting;
import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.events.RenderEntityModelEvent;
import me.cephetir.skyskipped.features.Features;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity {
    @Shadow
    protected ModelBase mainModel;

    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"), cancellable = true)
    public void doRender(final EntityLivingBase entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new RenderEntityModelEvent(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, this.mainModel))) ci.cancel();
    }

    @ModifyArg(method = "renderName*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    public String renderName(String text) {
        text = SkySkipped.replaceCosmetics(text);

        if (Config.Companion.getTerms() && Features.Companion.getTermsDisplay().getPlayers().containsKey(text))
            text = text + " " + ChatFormatting.AQUA + Features.Companion.getTermsDisplay().getPlayers().get(text);

        return text;
    }
}