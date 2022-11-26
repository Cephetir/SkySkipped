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

import me.cephetir.skyskipped.config.Config;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Pseudo
@Mixin(targets = "io.github.moulberry.notenoughupdates.overlays.EquipmentOverlay", remap = false)
public class MixinEquipmentOverlay {
    @Unique
    private Class<?> petOverlayClass = null;
    @Unique
    private Object petOverlayInstance = null;
    @Unique
    private Class<?> customArmourClass = null;
    @Unique
    private Object customArmourInstance = null;

    @Dynamic
    @Inject(method = "updateGuiInfo", at = @At("HEAD"), cancellable = true)
    private void updateGuiInfo(GuiScreen screen, CallbackInfo ci) {
        if (!Config.Companion.getNeuOptimize()) return;
        try {
            if (petOverlayInstance == null || customArmourInstance == null) {
                Class<?> neu = Class.forName("io.github.moulberry.notenoughupdates.NotEnoughUpdates");
                Field neuInstanceField = getField(neu, "INSTANCE");
                Object neuInstance = neuInstanceField.get(null);

                Field configField = getField(neu, "config");
                Class<?> config = configField.getType();
                Object configInstance = configField.get(neuInstance);

                Field petOverlayField = getField(config, "petOverlay");
                petOverlayClass = petOverlayField.getType();
                petOverlayInstance = petOverlayField.get(configInstance);

                Field customArmourField = getField(config, "customArmour");
                customArmourClass = customArmourField.getType();
                customArmourInstance = customArmourField.get(configInstance);
            }

            Field petInvDisplayField = getField(petOverlayClass, "petInvDisplay");
            boolean petInvDisplay = petInvDisplayField.getBoolean(petOverlayInstance);

            Field enableArmourHudField = getField(customArmourClass, "enableArmourHud");
            boolean enableArmourHud = enableArmourHudField.getBoolean(customArmourInstance);

            if (!petInvDisplay && !enableArmourHud)
                ci.cancel();
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Unique
    private Field getField(Class<?> clazz, String name) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
            if (field.getName().equals(name))
                return field;
        return null;
    }
}
