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

package me.cephetir.skyskipped.utils.skyblock

import net.minecraft.util.EnumChatFormatting
import java.awt.Color

enum class ItemRarity(val rarityName: String = "COMMON", val baseColor: EnumChatFormatting = EnumChatFormatting.WHITE, val color: Color = Color.WHITE) {
    COMMON("COMMON", EnumChatFormatting.WHITE, Color(255, 255, 255)),
    UNCOMMON("UNCOMMON", EnumChatFormatting.GREEN, Color(77, 231, 77)),
    RARE("RARE", EnumChatFormatting.BLUE, Color(85, 85, 255)),
    EPIC("EPIC", EnumChatFormatting.DARK_PURPLE, Color(151, 0, 151)),
    LEGENDARY("LEGENDARY", EnumChatFormatting.GOLD, Color(255, 170, 0)),
    MYTHIC("MYTHIC", EnumChatFormatting.LIGHT_PURPLE, Color(255, 85, 255)),
    DIVINE("DIVINE", EnumChatFormatting.AQUA, Color(85, 255, 255)),
    SUPREME("SUPREME", EnumChatFormatting.DARK_RED, Color(170, 0, 0)),
    SPECIAL("SPECIAL", EnumChatFormatting.RED, Color(255, 85, 85)),
    VERY_SPECIAL("VERY SPECIAL", EnumChatFormatting.RED, Color(170, 0, 0));

    companion object {
        private val VALUES = values().sortedBy { it.ordinal }.toMutableList()
        val RARITY_PATTERN = Regex("(?:§[\\da-f]§l§ka§r )?(?<rarity>${VALUES.joinToString("|") { "(?:${it.baseColor}§l)+${it.rarityName}" }})")

        fun byBaseColor(color: String): ItemRarity = VALUES.find { it.baseColor.toString() == color } ?: COMMON
    }

    val nextRarity: ItemRarity
        get() = VALUES[(ordinal + 1) % VALUES.size]
}