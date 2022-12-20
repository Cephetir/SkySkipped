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
