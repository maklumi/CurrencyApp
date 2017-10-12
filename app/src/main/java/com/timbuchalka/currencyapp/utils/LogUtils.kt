package com.timbuchalka.currencyapp.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by HomePC on 12/10/2017.
 */

object LogUtils {

    private val stringBuffer = StringBuffer()

    var logListener: LogListener = object : LogListener {
        override fun onLogged(log: StringBuffer) {
        }
    }


    interface LogListener {
        fun onLogged(log: StringBuffer)
    }

    fun log(tag: String, message: String) {
        Log.d(tag, message)
        val date = formatDate(Calendar.getInstance())
        val stringBuilder = StringBuilder()
        with(stringBuilder) {
            append(date)
            append(" ")
            append(tag)
            append(" ")
            append(message)
            append("\n\n")
        }

        stringBuffer.insert(0, stringBuilder.toString())
        printLog()
    }

    private fun formatDate(calendar: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM hh:mm:ss")
        return simpleDateFormat.format(calendar.time)
    }

    private fun printLog() = logListener.onLogged(stringBuffer)

    private fun clearLog() {
        stringBuffer.setLength(0)
        printLog()
    }


}