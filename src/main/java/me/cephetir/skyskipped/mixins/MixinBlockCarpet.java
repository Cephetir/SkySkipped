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

package me.cephetir.skyskipped.mixins;

import me.cephetir.skyskipped.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockCarpet.class)
public class MixinBlockCarpet extends Block {

    public MixinBlockCarpet(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    public MixinBlockCarpet(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "setBlockBoundsFromMeta", at = @At(value = "HEAD"), cancellable = true)
    public void setBlockBoundsFromMeta(int meta, CallbackInfo ci) {
        if (!Config.Companion.getRemoveCarpets()) return;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);
        ci.cancel();
    }
}
