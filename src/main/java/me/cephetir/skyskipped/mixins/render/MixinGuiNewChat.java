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

package me.cephetir.skyskipped.mixins.render;

import me.cephetir.bladecore.utils.TextUtils;
import me.cephetir.skyskipped.features.impl.chat.ChatSearch;
import me.cephetir.skyskipped.mixins.accessors.IMixinGuiChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

    @Shadow
    @Final
    private List<ChatLine> chatLines;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;

    @Shadow
    public abstract void deleteChatLine(int id);

    @Shadow
    protected abstract void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly);

    @ModifyVariable(method = "refreshChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.BEFORE), index = 1)
    public int i(int in) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (ChatSearch.Companion.getSearch() && gui instanceof GuiChat) {
            GuiTextField input = ((IMixinGuiChat) gui).getInputField();
            if (!input.getText().equals("")) {
                String line = TextUtils.stripColor(this.chatLines.get(in).getChatComponent().getUnformattedText()).toLowerCase();
                if (!line.contains(input.getText().toLowerCase()) || !line.equals("search mode on")) {
                    for (int i = in; i >= 0; i--) {
                        String line2 = TextUtils.stripColor(this.chatLines.get(i).getChatComponent().getUnformattedText()).toLowerCase();
                        if (line2.contains(input.getText().toLowerCase()) || line2.equals("search mode on"))
                            return i;
                    }
                    return -1;
                }
                return in;
            }
        }
        return in;
    }

    @Inject(method = "refreshChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void refreshChat(CallbackInfo ci, int i) {
        if (i == -1)
            ci.cancel();
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (ChatSearch.Companion.getSearch() && Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            GuiTextField input = ((IMixinGuiChat) Minecraft.getMinecraft().currentScreen).getInputField();
            String text = chatComponent.getUnformattedText().toLowerCase();
            if (!input.getText().equals("") && !text.contains(input.getText().toLowerCase()) && !displayOnly) {
                this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
                ci.cancel();
            }
        }
    }

    @Redirect(method = "setChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;deleteChatLine(I)V"))
    public void deleteChatLine(GuiNewChat chat, int id, IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (!displayOnly)
            this.deleteChatLine(id);
    }

    @ModifyArg(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    public int modifyIndex(int in, Object line) {
        ChatLine cl = (ChatLine) line;
        if (ChatSearch.Companion.getSearch() && !cl.getChatComponent().getFormattedText().contains("§e§lSEARCH MODE ON")) {
            if (this.drawnChatLines.size() == 0) {
                this.deleteChatLine("skyskippedsearchmode".hashCode());
                this.setChatLine(new ChatComponentText("§e§lSEARCH MODE ON"), "skyskippedsearchmode".hashCode(), Minecraft.getMinecraft().ingameGUI.getUpdateCounter(), true);
            }
            return 1;
        }
        return in;
    }
}
