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

import gg.essential.universal.ChatColor;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.DecimalFormat;

@Mixin(ScorePlayerTeam.class)
public class MixinScorePlayerTeam {

    @Inject(method = "formatPlayerName", at = @At(value = "RETURN"), cancellable = true)
    private static void formatPlayerName(Team p_96667_0_, String p_96667_1_, CallbackInfoReturnable<String> cir) {
        String text = TextUtils.keepScoreboardCharacters(TextUtils.stripColor(cir.getReturnValue())).trim();
        if (Config.coinsToggle && text.startsWith("Purse: ")) {
            double coins = Double.parseDouble(text.substring(7).split(" ")[0].replace(",", ""));
            double needed = coins + Config.coins;
            DecimalFormat format = new DecimalFormat("###,###.##");
            String s = format.format(needed).replace(" ", ",");
            cir.setReturnValue("Purse: " + ChatColor.GOLD + s);
        }
    }
}
