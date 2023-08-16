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
import io.netty.bootstrap.ChannelFactory
import io.netty.channel.socket.oio.OioSocketChannel
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import java.io.File
import java.io.IOException
import java.net.*
import java.net.Proxy


object Proxy {
    var proxyEnabled = false
    var proxyAddress = "127.0.0.1:10808:username:pass"
    var proxyType = ProxyType.SOCKS

    fun loadProxy() {
        val file = File(Config.modDir, "proxy.json")
        if (!file.exists())
            return saveProxy()

        runCatching {
            val json = SkySkipped.gson.fromJson(file.readText(), JsonObject::class.java)
            if (json.has("proxyEnabled")) proxyEnabled = json.getAsJsonPrimitive("proxyEnabled").asBoolean
            if (json.has("proxyAddress")) proxyAddress = json.getAsJsonPrimitive("proxyAddress").asString
            if (json.has("proxyType")) proxyType = ProxyType.valueOf(json.getAsJsonPrimitive("proxyType").asString)
        }.onFailure {
            it.printStackTrace()
            saveProxy()
        }
    }

    fun saveProxy() {
        val file = File(Config.modDir, "proxy.json")
        if (!file.exists())
            file.createNewFile()

        val json = JsonObject()
        json.addProperty("proxyEnabled", proxyEnabled)
        json.addProperty("proxyAddress", proxyAddress)
        json.addProperty("proxyType", proxyType.name)
        file.writeText(SkySkipped.gson.toJson(json))
    }

    fun getProxy(): Proxy? {
        if (!proxyEnabled)
            return null

        try {
            val split = proxyAddress.split(":")
            val addressStr = split[0]
            val port = split[1].toInt()
            val address = InetSocketAddress(InetAddress.getByName(addressStr), port)
            val proxy = when (proxyType) {
                ProxyType.HTTP -> Proxy(Proxy.Type.HTTP, address)
                ProxyType.SOCKS -> Proxy(Proxy.Type.SOCKS, address)
            }
            if (split.size == 4) Authenticator.setDefault(object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication =
                    PasswordAuthentication(split[2], split[3].toCharArray())
            })
            return proxy
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    enum class ProxyType {
        SOCKS,
        HTTP
    }

    class ProxyOioChannelFactory(private val proxy: Proxy) : ChannelFactory<OioSocketChannel> {
        override fun newChannel(): OioSocketChannel =
            OioSocketChannel(Socket(proxy))
    }
}