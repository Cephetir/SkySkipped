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
