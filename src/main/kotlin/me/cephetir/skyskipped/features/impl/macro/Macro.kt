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

package me.cephetir.skyskipped.features.impl.macro

import ds.desktop.notify.DesktopNotify
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.HttpUtils
import java.net.URL
import javax.swing.ImageIcon

abstract class Macro(val name: String) : Feature() {
    var enabled = false
    abstract fun toggle()

    protected fun sendWebhook(title: String, message: String, ping: Boolean) {
        if (Config.desktopNotifications)
            DesktopNotify.showDesktopMessage(
                "SkySkipped Macro: $title",
                message,
                DesktopNotify.SUCCESS,
                ImageIcon(URL("https://cdn.discordapp.com/app-assets/867366183057752094/925346125291061308.png")).image
            )

        if (!Config.webhook || Config.webhookUrl.isEmpty()) return
        val headers = mapOf(
            "Content-Type" to "application/json",
            "User-Agent" to "Mozilla/5.0"
        )
        val json =
            "{ \"content\": " + (if (ping) "\"@everyone\"" else "null") + ", " +
                    "\"embeds\":[ { \"title\": \"" + title + "\", \"description\": \"Account: " + mc.session.username + "\", \"color\": " + 8224125 + ", " +
                    "\"fields\": [ { \"name\": \"Message:\", \"value\": \"" + message + "\" } ], " +
                    "\"footer\": { \"text\": \"SkySkipped\", \"icon_url\": \"https://cdn.discordapp.com/icons/875274674332905502/c1d97bdf1ea9ecc3d0a010cd3a58d69e.png?size=4096\" } } ], " +
                    "\"username\": \"SkySkipped Macro\", \"avatar_url\": \"https://cdn.discordapp.com/icons/875274674332905502/c1d97bdf1ea9ecc3d0a010cd3a58d69e.png?size=4096\" }"
        HttpUtils.sendPost(Config.webhookUrl, json, headers)
    }
}