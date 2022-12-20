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
            if (Config.desktopNotifications) {
                if (!this::trayIcon.isInitialized) {
                    val tray = SystemTray.getSystemTray()
                    val image = ImageIcon(URL("https://cdn.discordapp.com/app-assets/867366183057752094/925346125291061308.png")).image
                    trayIcon = TrayIcon(image, "SkySkipped")
                    trayIcon.isImageAutoSize = true
                    tray.add(trayIcon)
                }
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
            }

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
}