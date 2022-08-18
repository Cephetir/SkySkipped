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

import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.impl.chat.ChatSearch;
import me.cephetir.skyskipped.utils.TextUtils;
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

    @ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"), index = 0)
    private String onDrawString(String text) {
        return !Config.Companion.getAdvancedCustomNames() ? SkySkipped.getCosmetics(text) : text;
    }

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
