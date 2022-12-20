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