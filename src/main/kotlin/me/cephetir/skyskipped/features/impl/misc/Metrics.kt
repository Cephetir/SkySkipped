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
                "https://skyskipped-website.vercel.app/api/user",
                obj.toString(),
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
        }
    }
}