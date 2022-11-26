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

import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.utils.render.font.CustomFontRenderer;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FontRenderer.class, priority = 2000)
public abstract class MixinFontRenderer {

    @ModifyVariable(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "HEAD"), argsOnly = true)
    public String drawString(String text) {
        return SkySkipped.getCosmetics(text);
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    private void getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if (text == null || !text.contains("§#")) return;
        cir.setReturnValue(CustomFontRenderer.INSTANCE.getStringWidth(text));
    }

    @Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
    private void renderStringAtPos(String text, boolean shadow, CallbackInfo ci) {
        if (!text.contains("§#") && !text.contains("§p")) return;
        ci.cancel();

        CustomFontRenderer.INSTANCE.renderStringAtPos(text, shadow);
    }

    @Inject(method = "renderChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderDefaultChar(IZ)F"), cancellable = true)
    private void renderDefaultRainbowChar(char ch, boolean italic, CallbackInfoReturnable<Float> cir) {
        if (CustomFontRenderer.chromaStyle)
            cir.setReturnValue(CustomFontRenderer.INSTANCE.renderRainbowChar(ch, italic, CustomFontRenderer.drawingShadow));
    }

    @Inject(method = "renderChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderUnicodeChar(CZ)F"), cancellable = true)
    private void renderUnicodeRainbowChar(char ch, boolean italic, CallbackInfoReturnable<Float> cir) {
        if (CustomFontRenderer.chromaStyle)
            cir.setReturnValue(CustomFontRenderer.INSTANCE.renderUniRainbowChar(ch, italic, CustomFontRenderer.drawingShadow));
    }
}
