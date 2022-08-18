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

import me.cephetir.skyskipped.event.Listener
import me.cephetir.skyskipped.event.SkyblockIsland

object Cache {
    val inDungeon
        get() = Listener.island == SkyblockIsland.Dungeon

    @JvmField
    var inSkyblock = false
    val inWorkshop
        get() = Listener.island == SkyblockIsland.JerryWorkshop
    val onIsland
        get() = Listener.island == SkyblockIsland.PrivateIsland
    val isJacob
        get() = Listener.jacobEvent

    @JvmField
    var prevIP = ""

    @JvmField
    var prevName = ""

    @JvmField
    var prevIsLan = false
}