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

import com.google.gson.Gson
import com.google.gson.JsonArray
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
        private val gson = Gson()
        fun update(online: Boolean) {
            val body = HttpUtils.sendGet(
                "https://api.github.com/gists/3fe4fa02268f33412236daf318008d33",
                mapOf(
                    "Authorization" to "Basic Y2VwaGV0aXI6Z2hwX0JOTGV2UEpxNzA2WUtybUlEYjluVElrOW5CZElIVDJiZW5vNQ=="
                )
            )

            val data = gson.fromJson(
                body,
                JsonObject::class.java
            )["files"]!!.asJsonObject["users-data.json"]!!.asJsonObject["content"]!!.asString

            val json =
                gson.fromJson(data.removePrefix("\"").removeSuffix("\"").replace("\\", ""), JsonArray::class.java)

            json.removeAll { it.asJsonObject["username"]!!.asString == Minecraft.getMinecraft().session.username }
            val obj = JsonObject()
            obj.addProperty("username", Minecraft.getMinecraft().session.username)
            obj.addProperty("online", online)
            json.add(obj)

            val sampleJson =
                "{\"files\":{\"users-data.json\":{\"filename\":\"users-data.json\",\"type\":\"application/json\",\"language\":\"JSON\",\"raw_url\":\"https://gist.githubusercontent.com/Cephetir/3fe4fa02268f33412236daf318008d33/raw/e7e3afccf9f57577948e9abf526735d228ffe3df/users-data.json\",\"size\":82,\"truncated\":false,\"content\":\"${
                    json.toString().replace("\"", "\\\"")
                }\"}}}"


            HttpUtils.sendPatch(
                "https://api.github.com/gists/3fe4fa02268f33412236daf318008d33",
                sampleJson,
                mapOf(
                    "Content-Type" to "application/json",
                    "accept" to "application/vnd.github.v3+json",
                    "Authorization" to "Basic Y2VwaGV0aXI6Z2hwX0JOTGV2UEpxNzA2WUtybUlEYjluVElrOW5CZElIVDJiZW5vNQ=="
                )
            )
        }
    }
}