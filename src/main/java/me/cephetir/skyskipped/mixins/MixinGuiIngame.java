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

import gg.essential.universal.ChatColor;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.events.ScoreboardRenderEvent;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"), index = 0)
    public String a(String text) {
        String txt = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(text)).trim();
        if (Config.Companion.getCoinsToggle() && txt.startsWith("Purse: ")) {
            double coins = Double.parseDouble(txt.substring(7).split(" ")[0].replace(",", ""));
            double needed = coins + Config.Companion.getCoins();
            DecimalFormat format = new DecimalFormat("###,###.##");
            String s = format.format(needed).replace(" ", ",");
            return "Purse: " + ChatColor.GOLD + s;
        } else if (Config.Companion.getCustomSbNumbers() && text.startsWith(EnumChatFormatting.RED + "") && Pattern.compile("\\d+").matcher(txt).matches())
            return "";
        else return text;
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void renderScoreboard(ScoreObjective s, ScaledResolution score, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new ScoreboardRenderEvent(s, score))) ci.cancel();
    }
}
