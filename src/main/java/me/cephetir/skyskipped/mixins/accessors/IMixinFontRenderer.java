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

package me.cephetir.skyskipped.mixins.accessors;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(value = FontRenderer.class, priority = 2000)
public interface IMixinFontRenderer {
    @Accessor
    boolean getRandomStyle();

    @Accessor
    void setRandomStyle(boolean randomStyle);

    @Accessor
    boolean getBoldStyle();

    @Accessor
    void setBoldStyle(boolean boldStyle);

    @Accessor
    boolean getStrikethroughStyle();

    @Accessor
    void setStrikethroughStyle(boolean strikethroughStyle);

    @Accessor
    boolean getUnderlineStyle();

    @Accessor
    void setUnderlineStyle(boolean underlineStyle);

    @Accessor
    boolean getItalicStyle();

    @Accessor
    void setItalicStyle(boolean italicStyle);

    @Accessor
    int getTextColor();

    @Accessor
    void setTextColor(int textColor);

    @Accessor
    int[] getColorCode();

    @Accessor("red")
    float getReddd();

    @Accessor("red")
    void setReddd(float red);

    @Accessor("blue")
    float getBlueee();

    @Accessor("blue")
    void setBlueee(float blue);

    @Accessor("green")
    float getGreennn();

    @Accessor("green")
    void setGreennn(float green);

    @Accessor("alpha")
    float getAlphaaa();

    @Accessor("alpha")
    void setAlphaaa(float alpha);

    @Invoker("getCharWidth")
    int getCharWidth(char character);

    @Accessor
    boolean getUnicodeFlag();

    @Accessor
    float getPosX();

    @Accessor
    void setPosX(float posX);

    @Accessor
    float getPosY();

    @Accessor
    void setPosY(float posY);

    @Invoker("renderChar")
    float renderChar(char ch, boolean italic);

    @Accessor
    Random getFontRandom();

    @Accessor
    byte[] getGlyphWidth();

    @Accessor
    int[] getCharWidth();

    @Accessor
    @Final
    ResourceLocation getLocationFontTexture();

    @Invoker("loadGlyphTexture")
    void loadGlyphTexture(int page);
}
