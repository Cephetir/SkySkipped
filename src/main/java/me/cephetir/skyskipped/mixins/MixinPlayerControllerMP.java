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

package me.cephetir.skyskipped.mixins;

import me.cephetir.bladecore.core.event.BladeEventBus;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.events.ClickSlotControllerEvent;
import me.cephetir.skyskipped.event.events.PlayerAttackEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Shadow
    @Final
    private NetHandlerPlayClient netClientHandler;

    @Shadow
    private int blockHitDelay;

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        if (BladeEventBus.INSTANCE.post(new PlayerAttackEvent(targetEntity)))
            ci.cancel();
    }

    @Inject(method = "windowClick", at = @At("HEAD"), cancellable = true)
    public void windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn, CallbackInfoReturnable<ItemStack> cir) {
        ClickSlotControllerEvent event = new ClickSlotControllerEvent(windowId, slotId, mouseButtonClicked, mode);
        BladeEventBus.INSTANCE.post(event);

        if (event.getSlot() != slotId || event.getButton() != mouseButtonClicked || event.getMode() != mode) {
            short short1 = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
            ItemStack itemstack = playerIn.openContainer.slotClick(event.getSlot(), event.getButton(), event.getMode(), playerIn);
            this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, event.getSlot(), event.getButton(), event.getMode(), itemstack, short1));
            cir.setReturnValue(itemstack);
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"))
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        if (Config.fastBreak.getValue())
            this.blockHitDelay = 0;
    }
}
