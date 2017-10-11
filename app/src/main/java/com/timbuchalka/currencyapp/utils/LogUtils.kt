package com.timbuchalka.currencyapp.utils

/**
 * Created by HomePC on 12/10/2017.
 */

object LogUtils {

    private val sStringBuffer = StringBuffer()

    private val sLogListener: LogListener? = null


    interface LogListener {
        fun onLogged(log: StringBuffer)
    }

    fun log(tag: String, message: String) {

    }
}