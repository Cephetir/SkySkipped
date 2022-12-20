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
