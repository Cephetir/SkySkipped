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

package me.cephetir.skyskipped.utils

import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

object HttpUtils {
    @JvmStatic
    fun sendGet(endpoint: String, params: List<NameValuePair>?): String? {
        var endpoint = endpoint
        return try {
            if (params != null) {
                endpoint += "?" + URLEncodedUtils.format(params, "UTF-8")
            }
            val httpGet = HttpGet(endpoint)
            val httpClient: HttpClient = HttpClientBuilder.create().build()
            val httpResponse = httpClient.execute(httpGet)
            EntityUtils.toString(httpResponse.entity)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun sendPost(endpoint: String, params: String?): String? {
        return try {
            val httpPost = HttpPost(endpoint)
            if (params != null) {
                httpPost.entity = StringEntity(params)
            }
            val httpClient: HttpClient = HttpClientBuilder.create().build()
            val httpResponse = httpClient.execute(httpPost)
            EntityUtils.toString(httpResponse.entity)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}