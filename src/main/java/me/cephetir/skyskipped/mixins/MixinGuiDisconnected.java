/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
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

import me.cephetir.skyskipped.config.Cache;
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
