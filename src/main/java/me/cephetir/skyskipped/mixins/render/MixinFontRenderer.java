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
