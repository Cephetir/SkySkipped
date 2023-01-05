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
