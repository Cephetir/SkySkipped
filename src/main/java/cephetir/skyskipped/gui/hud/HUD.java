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

package cephetir.skyskipped.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class HUD implements IRenderer {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final FontRenderer font = mc.fontRendererObj;

    protected ScreenPosition pos;

    @Override
    public ScreenPosition load() {
        return pos;
    }

    @Override
    public void save(ScreenPosition pos) {
        this.pos = pos;
    }
}