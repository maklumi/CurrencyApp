package com.timbuchalka.currencyapp.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.ResultReceiver
import android.text.TextUtils
import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.helpers.CurrencyParserHelper
import com.timbuchalka.currencyapp.utils.LogUtils
import com.timbuchalka.currencyapp.utils.WebServiceUtils
import org.json.JSONObject

/**
 * Created by HomePC on 12/10/2017.
 */
class CurrencyService(name: String = TAG) : IntentService(name) {

    companion object {
        val TAG = CurrencyService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent) {
        LogUtils.log(TAG, "Currency Service has started")
        val intentBundle = intent.getBundleExtra(Constants.BUNDLE)
        val receiver: ResultReceiver = intentBundle.getParcelable(Constants.RECEIVER)

        val parcel = Parcel.obtain()
        receiver.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel)
        parcel.recycle()

        val url: String? = intentBundle.getString(Constants.URL)
        val currencyName = intentBundle.getString(Constants.CURRENCY_NAME)

        val bundle = Bundle()
        if (url != null && !TextUtils.isEmpty(url)) {
            receiverForSending.send(Constants.STATUS_RUNNING, Bundle.EMPTY)

            if (WebServiceUtils.hasInternetConnection(applicationContext)) {
                try {
                    val obj: JSONObject = WebServiceUtils.requestJSONObject(url)

                    val currency = CurrencyParserHelper.parseCurrency(obj, currencyName)
                    bundle.putParcelable(Constants.RESULT, currency)
                    receiverForSending.send(Constants.STATUS_FINISHED, bundle)
                } catch (e: Exception) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString())
                    receiverForSending.send(Constants.STATUS_ERROR, bundle)
                }
            }
        }
    }
}