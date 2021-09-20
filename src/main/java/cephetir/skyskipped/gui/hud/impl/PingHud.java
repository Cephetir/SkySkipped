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

import cephetir.skyskipped.config.Config;
import cephetir.skyskipped.gui.hud.HUD;
import cephetir.skyskipped.gui.hud.ScreenPosition;
import net.minecraft.client.Minecraft;

public class PingHud extends HUD {

    @Override
    public void save(ScreenPosition pos) {
        this.pos = pos;
        Config.pingPosX = pos.getAbsoluteX();
        Config.pingPosY = pos.getAbsoluteY();
    }

    @Override
    public int getHeight() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth("[Ping] 9999");
    }

    @Override
    public void render(ScreenPosition pos) {
        Minecraft.getMinecraft().fontRendererObj.drawString(String.format("§7[§cPing§7]§f %d", Minecraft.getMinecraft().getCurrentServerData().pingToServer), pos.getAbsoluteX(), pos.getAbsoluteY(), -1, true);
    }

    @Override
    public void renderDummy(ScreenPosition pos) {
        Minecraft.getMinecraft().fontRendererObj.drawString(String.format("§7[§cPing§7]§f %d", Minecraft.getMinecraft().getCurrentServerData().pingToServer), pos.getAbsoluteX(), pos.getAbsoluteY(), -1, true);
    }
}