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

import me.cephetir.skyskipped.event.events.DrawSlotEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlot(Slot slot, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DrawSlotEvent.Pre((GuiContainer)(Object)this, slot));
    }

    @Inject(method = "drawSlot", at = @At("RETURN"))
    private void onDrawSlotPost(Slot slot, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DrawSlotEvent.Post((GuiContainer)(Object)this, slot));
    }
}
