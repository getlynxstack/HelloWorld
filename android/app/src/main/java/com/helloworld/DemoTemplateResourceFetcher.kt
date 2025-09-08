package com.helloworld

import android.content.Context
import com.lynx.tasm.core.LynxThreadPool
import com.lynx.tasm.resourceprovider.LynxResourceCallback
import com.lynx.tasm.resourceprovider.LynxResourceRequest
import com.lynx.tasm.resourceprovider.LynxResourceResponse
import com.lynx.tasm.resourceprovider.template.LynxTemplateResourceFetcher
import com.lynx.tasm.resourceprovider.template.TemplateProviderResult
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DemoTemplateResourceFetcher(context: Context) : LynxTemplateResourceFetcher() {
    private val mApplicationContext: Context? = context.applicationContext

    private fun requestResource(
        request: LynxResourceRequest, 
        callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        LynxThreadPool.getBriefIOExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(request.url)
                connection = url.openConnection() as HttpURLConnection
                
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 15000
                }
                
                val responseCode = connection.responseCode
                
                if (responseCode in 200..299) {
                    connection.inputStream.use { inputStream ->
                        ByteArrayOutputStream().use { byteArrayOutputStream ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while ((inputStream.read(buffer).also { length = it }) != -1) {
                                byteArrayOutputStream.write(buffer, 0, length)
                            }
                            val result = TemplateProviderResult.fromBinary(byteArrayOutputStream.toByteArray())
                            callback.onResponse(LynxResourceResponse.onSuccess(result))
                        }
                    }
                }
            } catch (e: IOException) {
//                callback.onResponse(LynxResourceResponse.onFailed(e))
            } catch (e: Exception) {
//                callback.onResponse(LynxResourceResponse.onFailed(e))
            } finally {
                connection?.disconnect()
            }
        }
    }

    override fun fetchTemplate(
        request: LynxResourceRequest?, callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        if (request == null) {
//            callback.onResponse(
//                LynxResourceResponse.onFailed(
//                    Throwable("request is null!")
//                )
//            )
            return
        }

        val url = request.url
        if (url.startsWith("file://") || url.startsWith("assets://")) {
            readBundleFromAssets(url, callback)
            return
        }

        requestResource(request, callback)
    }

    private fun readBundleFromAssets(
        url: String, callback: LynxResourceCallback<TemplateProviderResult>
    ) {
        LynxThreadPool.getBriefIOExecutor().execute {
            if (mApplicationContext == null) {
//                callback.onResponse(
//                    LynxResourceResponse.onFailed(
//                        Throwable("Context is null.")
//                    )
//                )
                return@execute
            }
            
            try {
                val assetPath = url.removePrefix("file://").removePrefix("assets://")
                val inputStream = mApplicationContext.assets.open(assetPath)
                
                inputStream.use { stream ->
                    ByteArrayOutputStream().use { byteArrayOutputStream ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while ((stream.read(buffer).also { length = it }) != -1) {
                            byteArrayOutputStream.write(buffer, 0, length)
                        }
                        val result = TemplateProviderResult.fromBinary(byteArrayOutputStream.toByteArray())
                        callback.onResponse(LynxResourceResponse.onSuccess(result))
                    }
                }
            } catch (e: IOException) {
//                callback.onResponse(
//                    LynxResourceResponse.onFailed(
//                        Throwable("Unable to read file: ${e.message}")
//                    )
//                )
            }
        }
    }

    override fun fetchSSRData(
        request: LynxResourceRequest?,
        callback: LynxResourceCallback<ByteArray>
    ) {
        if (request == null) {
//            callback.onResponse(
//                LynxResourceResponse.onFailed(
//                    Throwable("request is null!")
//                )
//            )
            return
        }

        LynxThreadPool.getBriefIOExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(request.url)
                connection = url.openConnection() as HttpURLConnection
                
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 15000
                }
                
                val responseCode = connection.responseCode
                
                if (responseCode in 200..299) {
                    connection.inputStream.use { inputStream ->
                        ByteArrayOutputStream().use { byteArrayOutputStream ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while ((inputStream.read(buffer).also { length = it }) != -1) {
                                byteArrayOutputStream.write(buffer, 0, length)
                            }
                            callback.onResponse(
                                LynxResourceResponse.onSuccess(byteArrayOutputStream.toByteArray())
                            )
                        }
                    }
                } else {
//                    callback.onResponse(
//                        LynxResourceResponse.onFailed(
//                            Throwable("HTTP Error: $responseCode")
//                        )
//                    )
                }
            } catch (e: IOException) {
//                callback.onResponse(LynxResourceResponse.onFailed(e))
            } catch (e: Exception) {
//                callback.onResponse(LynxResourceResponse.onFailed(e))
            } finally {
                connection?.disconnect()
            }
        }
    }
}