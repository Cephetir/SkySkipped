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

package me.cephetir.skyskipped.features.impl.dugeons

import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.bladecore.utils.player
import me.cephetir.bladecore.utils.world
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.InventoryUtils
import me.cephetir.skyskipped.utils.skyblock.PingUtils
import me.cephetir.skyskipped.utils.skyblock.Queues
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
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
    private var saidGo = false

    init {
        listener<ClientChatReceivedEvent> {
            if (!Cache.inDungeon || !Config.fireFreezePing.value) return@listener
            val msg = it.message.unformattedText.stripColor()
            if (msg.startsWith("[BOSS] The Professor: Oh? You found my Guardians' one weakness?")) {
                ffTimer = System.currentTimeMillis() + 5000L
                PingUtils(110, "", false, {
                    val seconds = max(0.0, ((this.ffTimer - System.currentTimeMillis()) / 10.0).roundToInt() / 100.0)
                    "§4Use §c§lFire §3§lFreeze §4in: §c${seconds}s"
                }, {
                    if (Config.fireFreezeAuto.value) {
                        player?.inventory?.currentItem = InventoryUtils.findItemInHotbar("Fire Freeze")
                        mc.playerController.sendUseItem(player, world, player?.heldItem)
                    }
                })
            }
        }

        listener<ClientTickEvent> {
            if (!Cache.inDungeon || !Config.autoGo.value || player == null || world == null) return@listener
            if (System.currentTimeMillis() - lastGo < 2000L) return@listener
            if (world!!.getBlockState(BlockPos(player!!.posX, player!!.posY, player!!.posZ)).block != Blocks.portal) {
                saidGo = false
                return@listener
            }
            if (saidGo) return@listener

            player!!.sendChatMessage("going")
            lastGo = System.currentTimeMillis()
            saidGo = true
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
        if (!Cache.inDungeon || !Config.rabbitPing.value || rabPing) return
        if (event.message.unformattedText.stripColor().contains("You have proven yourself. You may pass")) {
            PingUtils(50, "Watcher done!")
            rabPing = true
        }
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (!Cache.inDungeon || !Config.mimic.value || mimicPing) return
        if (event.entity is EntityZombie) {
            val entity = event.entity as EntityZombie
            if (entity.isChild &&
                entity.getCurrentArmor(0) == null &&
                entity.getCurrentArmor(1) == null &&
                entity.getCurrentArmor(2) == null &&
                entity.getCurrentArmor(3) == null
            ) {
                Queues.sendMessage("/pc " + Config.mimicText.value)
                mimicPing = true
            }
        }
    }
}
