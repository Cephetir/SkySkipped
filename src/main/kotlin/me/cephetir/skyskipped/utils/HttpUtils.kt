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

import io.netty.handler.codec.http.HttpMethod
import me.cephetir.skyskipped.SkySkipped
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.StandardHttpRequestRetryHandler
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * @author chenerzhu
 * @create 2018-08-11 11:25
 */
object HttpUtils {
    private const val DEFAULT_CHARSET = "UTF-8"
    private var reqConf: RequestConfig? = null
    private var standardHandler: StandardHttpRequestRetryHandler? = null

    init {
        reqConf = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(2000)
            .setRedirectsEnabled(false)
            .setMaxRedirects(0)
            .build()
        standardHandler = StandardHttpRequestRetryHandler(3, true)
    }

    fun requestConfig() {
        reqConf = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(2000)
            .setRedirectsEnabled(false)
            .setMaxRedirects(0)
            .build()
        standardHandler = StandardHttpRequestRetryHandler(3, true)
    }

    private fun send(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET,
        method: HttpMethod?
    ): String? {
        var httpClient: CloseableHttpClient? = null
        try {
            httpClient = HttpClientBuilder.create().setRetryHandler(standardHandler).build()
            if (url.lowercase(Locale.getDefault()).startsWith("https")) {
                initSSL(httpClient, getPort(url))
            }
            var httpResponse: HttpResponse? = null
            when (method) {
                HttpMethod.GET -> {
                    val httpGet = HttpGet(url)
                    httpGet.config = reqConf
                    addHeader(httpGet, headerMap)
                    httpResponse = httpClient.execute(httpGet)
                }
                HttpMethod.POST -> {
                    val httpPost = HttpPost(url)
                    httpPost.config = reqConf
                    addHeader(httpPost, headerMap)
                    if (formParamMap == null || formParamMap.isEmpty()) {
                        httpPost.entity = StringEntity(content, contentCharset)
                    } else {
                        val ls: MutableList<NameValuePair> = ArrayList()
                        for ((key, value) in formParamMap) run {
                            ls.add(BasicNameValuePair(key, value))
                        }
                        httpPost.entity = UrlEncodedFormEntity(ls, "UTF-8")
                    }
                    httpResponse = httpClient.execute(httpPost)
                }
                HttpMethod.DELETE -> {
                    val httpDelete = HttpDelete(url)
                    httpDelete.config = reqConf
                    addHeader(httpDelete, headerMap)
                    httpResponse = httpClient.execute(httpDelete)
                }
                HttpMethod.PUT -> {
                    val httpPut = HttpPut(url)
                    httpPut.config = reqConf
                    addHeader(httpPut, headerMap)
                    httpPut.entity = StringEntity(content, contentCharset)
                    httpResponse = httpClient.execute(httpPut)
                }
                HttpMethod.PATCH -> {
                    val httpPatch = HttpPatch(url)
                    httpPatch.config = reqConf
                    addHeader(httpPatch, headerMap)
                    httpPatch.entity = StringEntity(content, contentCharset)
                    httpResponse = httpClient.execute(httpPatch)
                }
            }
            if (httpResponse != null) {
                if (httpResponse.statusLine.statusCode == 200) {
                    return EntityUtils.toString(httpResponse.entity)
                } else SkySkipped.logger.error("Failed request with status code ${httpResponse.statusLine.statusCode}")
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                httpClient?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun addHeader(httpRequest: HttpRequestBase, headerMap: Map<String, String>?): HttpRequestBase {
        if (headerMap != null && headerMap.isNotEmpty()) {
            val keys = headerMap.keys
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                httpRequest.addHeader(key, headerMap[key])
            }
        }
        return httpRequest
    }

    private fun getPort(url: String): Int {
        val startIndex = url.indexOf("://") + "://".length
        var host = url.substring(startIndex)
        if (host.indexOf("/") != -1) {
            host = host.substring(0, host.indexOf("/"))
        }
        var port = 443
        if (host.contains(":")) {
            val i = host.indexOf(":")
            port = host.substring(i + 1).toInt()
        }
        return port
    }

    private fun initSSL(httpClient: CloseableHttpClient?, port: Int) {
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
            val trustManager: X509TrustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            }
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            val ssf: ConnectionSocketFactory = SSLConnectionSocketFactory(sslContext)
            val r = RegistryBuilder.create<ConnectionSocketFactory>().register("https", ssf).build()
            val ccm = BasicHttpClientConnectionManager(r)
            HttpClients.custom().setConnectionManager(ccm).build()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun sendGet(
        url: String,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, "", headerMap, null, contentCharset, resultCharset, HttpMethod.GET)
    }

    fun sendPostForm(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?
    ): String? {
        return send(url, content, headerMap, formParamMap, DEFAULT_CHARSET, DEFAULT_CHARSET, HttpMethod.POST)
    }

    @JvmOverloads
    fun sendPost(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.POST)
    }

    fun sendPostForm(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        formParamMap: Map<String?, String?>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, content, headerMap, formParamMap, contentCharset, resultCharset, HttpMethod.POST)
    }

    @JvmOverloads
    fun sendDelete(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.DELETE)
    }

    @JvmOverloads
    fun sendPut(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.PUT)
    }

    @JvmOverloads
    fun sendPatch(
        url: String,
        content: String?,
        headerMap: Map<String, String>?,
        contentCharset: String = DEFAULT_CHARSET,
        resultCharset: String = DEFAULT_CHARSET
    ): String? {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.PATCH)
    }
}