package com.timbuchalka.currencyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.timbuchalka.currencyapp.receivers.CurrencyReceiver
import com.timbuchalka.currencyapp.services.CurrencyService
import com.timbuchalka.currencyapp.utils.LogUtils
import com.timbuchalka.currencyapp.value_objects.Currency

class MainActivity : AppCompatActivity(), CurrencyReceiver.Receiver {

    private val TAG = MainActivity::class.java.simpleName

    private var baseCurrency = Constants.CURRENCY_CODES[30]
    private var targetCurrency = Constants.CURRENCY_CODES[19]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrieveCurrencyExchangeRate()
    }

    private fun retrieveCurrencyExchangeRate() {
        val whichReceiver = CurrencyReceiver(Handler())
        whichReceiver.receiver = this

        val intent = Intent(Intent.ACTION_SYNC, null, applicationContext, CurrencyService::class.java)
        intent.setExtrasClassLoader(CurrencyService::class.java.classLoader)

        val bundle = Bundle()
        val url = Constants.CURRENCY_URL + baseCurrency
        bundle.putString(Constants.URL, url)
        bundle.putParcelable(Constants.RECEIVER, whichReceiver)
        bundle.putInt(Constants.REQUEST_ID, Constants.REQUEST_ID_NUM)
        bundle.putString(Constants.CURRENCY_NAME, targetCurrency)
        bundle.putString(Constants.CURRENCY_BASE, baseCurrency)
        intent.putExtra(Constants.BUNDLE, bundle)
        startService(intent)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        when (resultCode) {
            Constants.STATUS_RUNNING -> LogUtils.log(TAG, "Currency Service Running")
            Constants.STATUS_FINISHED -> {

                runOnUiThread {
                    val currencyParcel: Currency? = resultData.getParcelable(Constants.RESULT)
                    currencyParcel?.let {
                        val msg = "Currency: ${currencyParcel.base} - ${currencyParcel.name}: " +
                                "${currencyParcel.rate}"
                        LogUtils.log(TAG, msg)
                    }
                }

            }
            Constants.STATUS_ERROR -> {
                val error: String = resultData.getString(Intent.EXTRA_TEXT)
                LogUtils.log(TAG, error)
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }
}
