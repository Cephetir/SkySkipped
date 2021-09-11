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

package cephetir.skyskipped.gui.hud.impl;

import cephetir.skyskipped.gui.hud.HUD;
import cephetir.skyskipped.gui.hud.ScreenPosition;
import net.minecraft.client.Minecraft;

public class FPSHud extends HUD {

    @Override
    public int getHeight() {
        return font.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        return font.getStringWidth("[FPS] 999");
    }

    @Override
    public void render(ScreenPosition pos) {
        font.drawStringWithShadow(String.format("§7[§6FPS§7]§f %d", Minecraft.getDebugFPS()), pos.getAbsoluteX(), pos.getAbsoluteY(), -1);
    }

    @Override
    public void renderDummy(ScreenPosition pos) {
        font.drawStringWithShadow("§7[§6FPS§7]§f 999", pos.getAbsoluteX(), pos.getAbsoluteY(), -1);
    }
}
