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
import me.cephetir.skyskipped.event.events.ClickSlotEvent;
import me.cephetir.skyskipped.event.events.DrawSlotEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {
    @Shadow
    protected abstract Slot getSlotAtPosition(int x, int y);

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlot(Slot slot, CallbackInfo ci) {
        BladeEventBus.INSTANCE.post(new DrawSlotEvent.Pre((GuiContainer) (Object) this, slot));
    }

    @Inject(method = "drawSlot", at = @At("RETURN"))
    private void onDrawSlotPost(Slot slot, CallbackInfo ci) {
        BladeEventBus.INSTANCE.post(new DrawSlotEvent.Post((GuiContainer) (Object) this, slot));
    }

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    private void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (BladeEventBus.INSTANCE.post(new ClickSlotEvent((GuiContainer) (Object) this, slotIn, clickedButton)))
            ci.cancel();
    }
}
