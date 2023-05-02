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

package me.cephetir.skyskipped.features.impl.macro

import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.features.impl.macro.failsafes.Failsafes
import me.cephetir.skyskipped.utils.mc
import java.awt.SystemTray
import java.awt.TrayIcon
import java.net.URL
import javax.swing.ImageIcon


abstract class Macro(val name: String) : Feature() {
    var enabled = false

    abstract fun toggle()
    abstract fun info(): String

    abstract fun isBanwave(): String
    abstract fun banwaveCheckIn(): Long

    fun cropsMined(): Long = Failsafes.lastCount - Failsafes.startCount

    abstract fun stopAndOpenInv()
    abstract fun closeInvAndReturn()

    companion object {
        private lateinit var trayIcon: TrayIcon
        fun sendWebhook(title: String, message: String, ping: Boolean) {
            if (Config.desktopNotifications.value) {
                if (!this::trayIcon.isInitialized) {
                    val tray = SystemTray.getSystemTray()
                    val image = ImageIcon(URL("https://cdn.discordapp.com/app-assets/867366183057752094/925346125291061308.png")).image
                    trayIcon = TrayIcon(image, "SkySkipped")
                    trayIcon.isImageAutoSize = true
                    tray.add(trayIcon)
                }
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
            }

            if (!Config.webhook.value || Config.webhookUrl.value.isEmpty()) return
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
            HttpUtils.sendPost(Config.webhookUrl.value, json, headers)
        }
    }
}