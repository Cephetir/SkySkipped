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

package me.cephetir.skyskipped.features.impl.misc

import com.google.gson.JsonObject
import gg.essential.api.utils.Multithreading
import me.cephetir.bladecore.utils.HttpUtils
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.mc
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class Metrics : Feature() {
    private var first = true

    @SubscribeEvent
    fun onJoin(event: WorldEvent.Load) {
        if (!first) return
        first = false
        Multithreading.runAsync {
            update(true)
        }
    }

    companion object {
        fun update(online: Boolean) {
            val obj = JsonObject()
            obj.addProperty("name", mc.session.username)
            obj.addProperty("online", online)
            HttpUtils.sendPost(
                "https://skyskipped.com/api/user",
                obj.toString(),
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
        }
    }
}