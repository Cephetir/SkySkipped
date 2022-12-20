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

import me.cephetir.skyskipped.config.Cache;
import me.cephetir.skyskipped.mixins.accessors.IMixinGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public class MixinGuiDisconnected {
    @Shadow
    private int field_175353_i;

    @Shadow
    @Final
    private GuiScreen parentScreen;

    @Inject(method = "initGui", at = @At("TAIL"))
    public void initGui(CallbackInfo ci) {
        GuiScreen guiScreen = (GuiScreen) (Object) this;
        IMixinGuiScreen iMixinGuiScreen = (IMixinGuiScreen) this;
        iMixinGuiScreen.getButtonList().add(new GuiButton(1, guiScreen.width / 2 - 100, guiScreen.height / 2 + this.field_175353_i / 2 + iMixinGuiScreen.getFontRendererObj().FONT_HEIGHT + 24, "Reconnect"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 1) Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(this.parentScreen, Minecraft.getMinecraft(), new ServerData(Cache.prevName, Cache.prevIP, Cache.prevIsLan)));
    }
}
