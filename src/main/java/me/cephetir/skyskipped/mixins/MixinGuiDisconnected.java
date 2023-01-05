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
