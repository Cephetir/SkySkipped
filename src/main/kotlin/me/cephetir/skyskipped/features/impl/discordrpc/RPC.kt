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

package me.cephetir.skyskipped.features.impl.discordrpc

import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature

object RPC : Feature() {
    private val discordRPCManager = DiscordRPCManager()

    fun init() = discordRPCManager.connect()

    fun shutdown() = discordRPCManager.disconnect()

    fun reset(enabled: Boolean = Config.DRPC) {
        if (enabled && !discordRPCManager.isActive) discordRPCManager.connect()
        else if (!enabled && discordRPCManager.isActive) discordRPCManager.disconnect()
    }
}
