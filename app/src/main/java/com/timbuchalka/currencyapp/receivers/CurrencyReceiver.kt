package com.timbuchalka.currencyapp.receivers

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

/**
 * Created by HomePC on 12/10/2017.
 */
class CurrencyReceiver(handler: Handler): ResultReceiver (handler){

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle)
    }

    var receiver: Receiver = object : Receiver {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        }
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        receiver.onReceiveResult(resultCode, resultData)
    }
}