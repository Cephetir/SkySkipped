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

package me.cephetir.skyskipped.features

import gg.essential.universal.UChat
import me.cephetir.skyskipped.SkySkipped
import net.minecraft.client.Minecraft

abstract class Feature {
    protected val mc: Minecraft = Minecraft.getMinecraft()

    protected fun printdev(text: String) {
        if (SkySkipped.devMode) {
            println("[SkySkipped DEV] $text")
            UChat.chat("[SkySkipped DEV] $text")
        }
    }
}