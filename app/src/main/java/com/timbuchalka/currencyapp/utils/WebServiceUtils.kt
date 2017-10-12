package com.timbuchalka.currencyapp.utils

import android.content.Context
import android.net.ConnectivityManager
import com.timbuchalka.currencyapp.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

/**
 * Created by HomePC on 12/10/2017.
 */

object WebServiceUtils {

    val TAG = WebServiceUtils::class.java.simpleName

    fun requestJSONObject(serviceURL: String): JSONObject {
        var urlConnection: HttpURLConnection? = null
        try {
            val urlRequest = URL(serviceURL)
            urlConnection = urlRequest.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = Constants.CONNECTION_TIMEOUT // 10s
            urlConnection.readTimeout = Constants.READ_TIMEOUT

            val statusCode: Int = urlConnection.responseCode
            when (statusCode) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> LogUtils.log(TAG, "Unauthorized access!")
                HttpURLConnection.HTTP_NOT_FOUND -> LogUtils.log(TAG, "404 page not found")
                HttpURLConnection.HTTP_OK -> LogUtils.log(TAG, "URL Response OK")
                else -> LogUtils.log(TAG, "URL Response error")
            }

            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val inputAsString = inputStream.bufferedReader().use { it.readText() }
            return JSONObject(inputAsString)
        } catch (e: MalformedURLException) {
            LogUtils.log(TAG, e.message.orEmpty())
        } catch (e: SocketTimeoutException) {
            LogUtils.log(TAG, e.message.orEmpty())
        } catch (e: IOException) {
            LogUtils.log(TAG, e.message.orEmpty())
        } catch (e: JSONException) {
            LogUtils.log(TAG, e.message.orEmpty())
        } finally {
            urlConnection?.disconnect()
        }

        return JSONObject() // or null
    }

    fun hasInternetConnection(ctx: Context): Boolean {
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as  ConnectivityManager
        return connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo.isConnected

    }

}