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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.skyblock.PingUtils
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.max
import kotlin.math.roundToInt

class Pings : Feature() {
    private var ffTimer = -1L
    private var lastGo = -1L

    init {
        listener<ClientChatReceivedEvent> {
            if (!Cache.inDungeon || !Config.fireFreezePing) return@listener
            val msg = it.message.unformattedText.stripColor()
            if (msg.startsWith("[BOSS] The Professor: Oh? You found my Guardians one weakness?")) {
                ffTimer = System.currentTimeMillis() + 5000L
                PingUtils(110, "", false) {
                    val seconds = max(0.0, ((this.ffTimer - System.currentTimeMillis()) / 10.0).roundToInt() / 100.0)
                    "§4Use §c§lFire §3§lFreeze §4in: §c${seconds}s"
                }
            }
        }

        listener<ClientTickEvent> {
            if (!Cache.inDungeon || !Config.autoGo || player == null || world == null) return@listener
            if (System.currentTimeMillis() - lastGo < 2000L) return@listener
            if (world!!.getBlockState(player!!.position).block != Blocks.portal) return@listener

            player!!.sendChatMessage("going")
            lastGo = System.currentTimeMillis()
        }
    }

    private var rabPing = false
    private var mimicPing = false

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        rabPing = false
        mimicPing = false
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Cache.inDungeon || !Config.rabbitPing || rabPing) return
        if (event.message.unformattedText.stripColor().contains("You have proven yourself. You may pass")) {
            PingUtils(50, "Rabbit Hat!")
            rabPing = true
        }
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (!Cache.inDungeon || !Config.mimic || mimicPing) return
        if (event.entity is EntityZombie) {
            val entity = event.entity as EntityZombie
            if (entity.isChild &&
                entity.getCurrentArmor(0) == null &&
                entity.getCurrentArmor(1) == null &&
                entity.getCurrentArmor(2) == null &&
                entity.getCurrentArmor(3) == null
            ) {
                Queues.sendMessage("/pc " + Config.mimicText)
                mimicPing = true
            }
        }
    }
}
