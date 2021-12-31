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

package me.cephetir.skyskipped.utils;

import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public enum RarityUtils {
    COMMON("COMMON", EnumChatFormatting.WHITE, new Color(255, 255, 255)),
    UNCOMMON("UNCOMMON", EnumChatFormatting.GREEN, new Color(77, 231, 77)),
    RARE("RARE", EnumChatFormatting.BLUE, new Color(85, 85, 255)),
    EPIC("EPIC", EnumChatFormatting.DARK_PURPLE, new Color(151, 0, 151)),
    LEGENDARY("LEGENDARY", EnumChatFormatting.GOLD, new Color(255, 170, 0)),
    MYTHIC("MYTHIC", EnumChatFormatting.LIGHT_PURPLE, new Color(255, 85, 255));

    private static final RarityUtils[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(RarityUtils::ordinal)).toArray(RarityUtils[]::new);
    private final String name;
    private final EnumChatFormatting baseColor;
    private final Color colorToRender;

    static {
        for (RarityUtils rarity : values()) VALUES[rarity.ordinal()] = rarity;
    }

    RarityUtils(String name, EnumChatFormatting baseColor, Color colorToRender) {
        this.name = name;
        this.baseColor = baseColor;
        this.colorToRender = colorToRender;
    }

    public String getName() {
        return this.name;
    }

    public EnumChatFormatting getBaseColor() {
        return this.baseColor;
    }

    public Color getColorToRender() {
        return this.colorToRender;
    }

    public static RarityUtils byBaseColor(String color) {
        for (RarityUtils rarity : values()) if (rarity.baseColor.toString().equals(color)) return rarity;
        return null;
    }

    public RarityUtils getNextRarity() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}