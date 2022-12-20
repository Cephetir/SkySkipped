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
    public abstract void deleteChatLine(int id);

    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;

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
