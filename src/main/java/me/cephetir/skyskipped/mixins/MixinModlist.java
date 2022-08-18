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

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(FMLHandshakeMessage.ModList.class)
public class MixinModlist {
    @Shadow(remap = false)
    private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At(value = "RETURN"),remap = false)
    public void test(List<ModContainer> modList, CallbackInfo ci) {
        try {
            if (Minecraft.getMinecraft().isSingleplayer()) return;
        } catch (Exception e) {
            return;
        }
        this.modTags.entrySet().removeIf(mod -> !mod.getKey().equalsIgnoreCase("fml") && !mod.getKey().equalsIgnoreCase("forge") && !mod.getKey().equalsIgnoreCase("mcp"));
    }
}
