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
