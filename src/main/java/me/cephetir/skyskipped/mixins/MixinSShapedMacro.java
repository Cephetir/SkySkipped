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

import gg.essential.universal.UChat;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.impl.hacks.FailSafe;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qolskyblockmod.pizzaclient.features.macros.farming.SShapedMacro;

@Mixin(SShapedMacro.class)
public class MixinSShapedMacro {
    @Inject(method = "changeKeys", at = @At("HEAD"), remap = false)
    public void changeKeys(CallbackInfo ci) {
        if (!Config.Companion.getFailSafeSpawn() || FailSafe.Companion.getStuck() || FailSafe.Companion.getDesynced())
            return;
        UChat.chat("§cSkySkipped §f:: §eSetting spawnpoint...");
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/sethome");
    }
}
