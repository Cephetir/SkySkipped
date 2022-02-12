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

package me.cephetir.skyskipped.config

object Cache {
    @JvmField
    var isInDungeon = false
    @JvmField
    var dungeonPercentage = 0
    @JvmField
    var dungeonName = ""

    @JvmField
    var inParty = false
    @JvmField
    var inSkyblock = false
    @JvmField
    var inWorkshop = false
    @JvmField
    var itemheld = ""
    @JvmField
    var lastCrit = "0"

    @JvmField
    var prevIP = ""
    @JvmField
    var prevName = ""
    @JvmField
    var prevIsLan = false
}