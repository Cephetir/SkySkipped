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
