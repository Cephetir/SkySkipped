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
import me.cephetir.skyskipped.features.impl.misc.TokenAuth;
import me.cephetir.skyskipped.gui.impl.GuiProxyMenu;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer extends GuiScreen {
    @Inject(method = "connectToServer", at = @At("HEAD"))
    public void connectToServer(ServerData server, CallbackInfo ci) {
        Cache.prevName = server.serverName;
        Cache.prevIP = server.serverIP;
        Cache.prevIsLan = server.isOnLAN();
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        this.buttonList.add(new GuiButton(69420, this.width - 108, 8, 100, 20, "Proxy"));
        this.buttonList.add(new GuiButton(69402, this.width - 210, 8, 100, 20, "TokenAuth"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button.id == 69420) {
            mc.displayGuiScreen(new GuiProxyMenu(this));
            callbackInfo.cancel();
        } else if (button.id == 69402) {
            mc.displayGuiScreen(new TokenAuth.SessionGui(this));
            callbackInfo.cancel();
        }
    }

    @Inject(method = "connectToServer", at = @At("HEAD"))
    public void connectToServer(CallbackInfo callbackInfo) {
        if (mc.getNetHandler() != null)
            mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText(""));
    }
}
