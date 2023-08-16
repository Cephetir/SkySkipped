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

package me.cephetir.skyskipped.utils

import java.util.*
import java.util.stream.Collectors


fun romanToArabic(input: String): Int {
    var romanNumeral = input.uppercase()
    var result = 0
    val romanNumerals = RomanNumeral.reverseSortedValues
    var i = 0
    while (romanNumeral.isNotEmpty() && i < romanNumerals.size) {
        val symbol: RomanNumeral = romanNumerals[i]
        if (romanNumeral.startsWith(symbol.name)) {
            result += symbol.value
            romanNumeral = romanNumeral.substring(symbol.name.length)
        } else i++
    }
    require(romanNumeral.isEmpty()) { "$input cannot be converted to a Roman Numeral" }
    return result
}


internal enum class RomanNumeral(val value: Int) {
    I(1),
    IV(4),
    V(5),
    IX(9),
    X(10),
    XL(40),
    L(50),
    XC(90),
    C(100),
    CD(400),
    D(500),
    CM(900),
    M(1000);

    companion object {
        val reverseSortedValues: List<RomanNumeral>
            get() = Arrays.stream(values())
                .sorted(Comparator.comparing { e: RomanNumeral -> e.value }.reversed())
                .collect(Collectors.toList())
    }
}