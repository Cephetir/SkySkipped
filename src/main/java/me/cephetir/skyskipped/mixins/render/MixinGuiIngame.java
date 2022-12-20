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

import gg.essential.universal.ChatColor;
import me.cephetir.bladecore.core.event.BladeEventBus;
import me.cephetir.bladecore.utils.TextUtils;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.event.events.ScoreboardRenderEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    public String a(String text) {
        if (Config.Companion.getCustomSb()) return text;
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
        if (BladeEventBus.INSTANCE.post(new ScoreboardRenderEvent(s, score))) ci.cancel();
    }
}
