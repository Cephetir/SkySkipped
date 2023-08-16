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

import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.universal.UChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.events.PacketEvent
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.config.Config
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.util.ChatComponentText
import java.net.URL
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import kotlin.math.log10


object FunnyShit {
    fun gotSkillIssued() {
        try {
            val connection = URL("https://skyskipped.com/bhop.wav").openConnection()
            connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
            connection.connect()
            AudioSystem.getAudioInputStream(connection.getInputStream().buffered()).use { ais ->
                val clip = AudioSystem.getClip()
                clip.open(ais)
                (clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).value = 20.0f * log10(0.05f)
                clip.start()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private val time = listOf(
        "23h 59m 59s",
        "23h 59m 58s",
        "23h 59m 57s",
        "23h 59m 56s"
    )

    init {
        listener<PacketEvent.Receive> {
            val packet = it.packet
            if (packet !is S40PacketDisconnect) return@listener
            val reason = packet.reason.formattedText.stripColor()
            if (reason.containsAny(time) && reason.contains("Find out more: https://www.hypixel.net/appeal")) BackgroundScope.launch {
                gotSkillIssued()
            }
        }

        BladeEventBus.subscribe(this)
        DupePurse().register()
    }

    class DupePurse : Command("dupepurse", true, false) {
        @DefaultHandler
        fun handle() {
            BackgroundScope.launch {
                UChat.chat("§cSkySkipped §f:: §eLoading...")
                val oldToggle = Config.coinsToggle.value
                val oldCoins = Config.coins.value
                Config.coins.value = "0"
                delay(500L)
                BackgroundScope.launch { gotSkillIssued() }

                Config.coinsToggle.value = true
                repeat(100) {
                    player?.rotationYaw = RandomUtils.getRandom(-90f, 90f)
                    player?.rotationPitch = RandomUtils.getRandom(-90f, 90f)
                    UChat.chat("§eAdded §61,000,000 §ecoins!" + zzz(it))
                    Config.coins.value = (Config.coins.value.toLong() + 1_000_000).toString()
                    delay(50L)
                }

                val component = ChatComponentText("§cYou are temporarily banned for §r179d 23h 59m 59s §cfrom this server!")
                component.appendText("\n")
                component.appendText("\n§7Reason: §rBoosting detected on one or multiple SkyBlock profiles.")
                component.appendText("\n§7Find out more: §b§nhttps://www.hypixel.net/appeal")
                component.appendText("\n")
                component.appendText("\n§7Ban ID: §r#49871982")
                component.appendText("\n§7Sharing your Ban ID may affect the processing of your appeal!")
                mc.netHandler.networkManager.closeChannel(component)

                Config.coinsToggle.value = oldToggle
                Config.coins.value = oldCoins
            }
        }

        private fun zzz(times: Int): String {
            val sb = StringBuilder()
            repeat(times) {
                sb.append("§r")
            }
            return sb.toString()
        }
    }
}
