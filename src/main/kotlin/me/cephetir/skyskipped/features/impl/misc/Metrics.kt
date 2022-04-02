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

package me.cephetir.skyskipped.features.impl.misc

import com.google.gson.JsonObject
import gg.essential.api.utils.Multithreading
import me.cephetir.skyskipped.features.Feature
import me.cephetir.skyskipped.utils.HttpUtils
import net.minecraft.client.Minecraft
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
            obj.addProperty("name", Minecraft.getMinecraft().session.username)
            obj.addProperty("online", online)
            HttpUtils.sendPost(
                "https://skyskipped-website.vercel.app/api/user",
                obj.toString(),
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
        }
    }
}