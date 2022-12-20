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
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockCocoa.class)
public class MixinBlockCocoa extends Block {
    public MixinBlockCocoa(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    @Inject(method = "getCollisionBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void fixBoxWhenColliding(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<AxisAlignedBB> cir) {
        if (!Config.Companion.getBeansSize()) return;
        IBlockState ibs = worldIn.getBlockState(pos);
        int i = ibs.getValue(BlockCocoa.AGE);
        int j = 4 + i * 2;
        int k = 5 + i * 2;
        float f = (float) j / 2.0f;
        switch (ibs.getValue(BlockCocoa.FACING)) {
            case SOUTH:
                cir.setReturnValue(new AxisAlignedBB((float) pos.getX() + (8.0f - f) / 16.0f, (float) pos.getY() + (12.0f - (float) k) / 16.0f, (float) pos.getZ() + (15.0f - (float) j) / 16.0f, (float) pos.getX() + (8.0f + f) / 16.0f, (float) pos.getY() + 0.75f, (float) pos.getZ() + 0.9375f));
                break;
            case NORTH:
                cir.setReturnValue(new AxisAlignedBB((float) pos.getX() + (8.0f - f) / 16.0f, (float) pos.getY() + (12.0f - (float) k) / 16.0f, (float) pos.getZ() + 0.0625f, (float) pos.getX() + (8.0f + f) / 16.0f, (float) pos.getY() + 0.75f, (float) pos.getZ() + (1.0f + (float) j) / 16.0f));
                break;
            case WEST:
                cir.setReturnValue(new AxisAlignedBB((float) pos.getX() + 0.0625f, (float) pos.getY() + (12.0f - (float) k) / 16.0f, (float) pos.getZ() + (8.0f - f) / 16.0f, (float) pos.getX() + (1.0f + (float) j) / 16.0f, (float) pos.getY() + 0.75f, (float) pos.getZ() + (8.0f + f) / 16.0f));
                break;
            case EAST:
                cir.setReturnValue(new AxisAlignedBB((float) pos.getX() + (15.0f - (float) j) / 16.0f, (float) pos.getY() + (12.0f - (float) k) / 16.0f, (float) pos.getZ() + (8.0f - f) / 16.0f, (float) pos.getX() + 0.9375f, (float) pos.getY() + 0.75f, (float) pos.getZ() + (8.0f + f) / 16.0f));
                break;
        }
    }

    @Inject(method = "setBlockBoundsBasedOnState", at = @At(value = "HEAD"), cancellable = true)
    private void increaseBlockBox(IBlockAccess worldIn, BlockPos pos, CallbackInfo ci) {
        if (!Config.Companion.getBeansSize()) return;
        float f = worldIn.getBlockState(pos).getValue(BlockCocoa.AGE) < 2 ? 0f : 1f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, f, f, f);
        ci.cancel();
    }
}
