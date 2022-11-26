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
