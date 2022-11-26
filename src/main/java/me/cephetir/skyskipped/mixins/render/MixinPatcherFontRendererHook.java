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

import me.cephetir.skyskipped.utils.render.font.CustomFontRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.FontRendererHook", remap = false)
public class MixinPatcherFontRendererHook {
    @Dynamic
    @Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
    private void renderStringAtPos(String text, boolean shadow, CallbackInfoReturnable<Boolean> cir) {
        if (text.contains("§#") || text.contains("§p"))
            cir.setReturnValue(CustomFontRenderer.INSTANCE.renderStringAtPos(text, shadow));
    }

    @Dynamic
    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    private void getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if (text.contains("§#") || text.contains("§p"))
            cir.setReturnValue(CustomFontRenderer.INSTANCE.getStringWidth(text));
    }
}
