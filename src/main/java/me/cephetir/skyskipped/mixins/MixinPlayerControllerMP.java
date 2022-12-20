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

import me.cephetir.bladecore.core.event.BladeEventBus;
import me.cephetir.skyskipped.event.events.ClickSlotControllerEvent;
import me.cephetir.skyskipped.event.events.PlayerAttackEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
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
}
