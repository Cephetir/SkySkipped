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
