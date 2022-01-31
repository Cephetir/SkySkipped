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

import com.mojang.realmsclient.gui.ChatFormatting;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Features;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    public void getFormattedText(CallbackInfoReturnable<IChatComponent> cir) {
        if(!Config.Companion.getTerms()/* || !Cache.isInDungeon*/) return;
        IChatComponent toReturn = cir.getReturnValue();
        if (Features.Companion.getTermsDisplay().getPlayers().containsKey(TextUtils.stripColor(toReturn.getUnformattedText())))
            toReturn.appendText(" " + ChatFormatting.AQUA + Features.Companion.getTermsDisplay().getPlayers().get(TextUtils.stripColor(toReturn.getUnformattedText())));
        cir.setReturnValue(toReturn);
    }
}
