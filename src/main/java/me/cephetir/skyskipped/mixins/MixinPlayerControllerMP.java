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
