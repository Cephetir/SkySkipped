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
