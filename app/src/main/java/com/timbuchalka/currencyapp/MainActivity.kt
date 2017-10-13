package com.timbuchalka.currencyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.timbuchalka.currencyapp.adapters.CurrencyAdapter
import com.timbuchalka.currencyapp.database.CurrencyDatabaseAdapter
import com.timbuchalka.currencyapp.database.CurrencyTableHelper
import com.timbuchalka.currencyapp.receivers.CurrencyReceiver
import com.timbuchalka.currencyapp.services.CurrencyService
import com.timbuchalka.currencyapp.utils.AlarmUtils
import com.timbuchalka.currencyapp.utils.LogUtils
import com.timbuchalka.currencyapp.utils.NotificationUtils
import com.timbuchalka.currencyapp.utils.SharedPreferencesUtils
import com.timbuchalka.currencyapp.value_objects.Currency
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.SQLException

class MainActivity : AppCompatActivity(), CurrencyReceiver.Receiver {

    private val TAG = MainActivity::class.java.simpleName

    private var baseCurrency = Constants.CURRENCY_CODES[30]
    private var targetCurrency = Constants.CURRENCY_CODES[19]

    private var serviceRepetition = AlarmUtils.REPEAT.EVERY_MINUTE.ordinal

    private val currencyTableHelper: CurrencyTableHelper by lazy {
        val currencyDatabaseAdapter = CurrencyDatabaseAdapter(this)
        CurrencyTableHelper(currencyDatabaseAdapter)
    }

    private var isLogVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)

        initSpinner()
        initCurrencyList()

        resetDownloads()
        retrieveCurrencyExchangeRate()
    }

    private fun initCurrencyList() {
        val baseCurrencyAdapter = CurrencyAdapter(this)
        val targetCurrencyAdapter = CurrencyAdapter(this)

        val baseCurrencyIndex: Int = Constants.CURRENCY_CODES.indexOf(baseCurrency)
        val targetCurrencyIndex: Int = Constants.CURRENCY_CODES.indexOf(targetCurrency)

        base_currency_list.setItemChecked(baseCurrencyIndex, true)
        base_currency_list.setSelection(baseCurrencyIndex)

        target_currency_list.setItemChecked(targetCurrencyIndex, true)
        target_currency_list.setSelection(targetCurrencyIndex)
    }

    private fun initSpinner() {
        with(time_frequency) {
            isSaveEnabled = true
            setSelection(SharedPreferencesUtils.getServiceRepetition(this@MainActivity), false)
            post {
                setOnItemClickListener { parent, view, position, id ->
                    SharedPreferencesUtils.updateServiceRepetition(this@MainActivity, position)
                    serviceRepetition = position
                    if (position >= AlarmUtils.REPEAT.values().size) AlarmUtils.stopService()
                    else retrieveCurrencyExchangeRate()
                }
            }
        }
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
//        startService(intent)
        AlarmUtils.startService(this, intent, AlarmUtils.REPEAT.values()[serviceRepetition])
    }

    private fun resetDownloads() {
        SharedPreferencesUtils.updateNumDownloads(this, 0)
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
                        val id: Long = currencyTableHelper.insertCurrency(currencyParcel)
                        var currency = currencyParcel
                        try {
                            currency = currencyTableHelper.getCurrency(id)
                        } catch (e: SQLException) {
                            e.printStackTrace()
                            LogUtils.log(TAG, "Currency retrieval has failed")
                        }

                        val dbMessage = "Currency (DB) ${currency.base} - ${currency.name}: ${currency.rate}"
                        LogUtils.log(TAG, dbMessage)
                        NotificationUtils.showNotificationMessage(applicationContext,
                                "Currency Exchange Rate", dbMessage)

                        if (NotificationUtils.isAppInBackground(this)) {
                            var numDownLoads = SharedPreferencesUtils.getNumDownloads(applicationContext)
                            SharedPreferencesUtils.updateNumDownloads(applicationContext, ++numDownLoads)

                            if (numDownLoads == Constants.MAX_DOWNLOADS) {
                                LogUtils.log(TAG, "Max downloads for the background processing has been reached.")
                                serviceRepetition = AlarmUtils.REPEAT.EVERY_DAY.ordinal
                                retrieveCurrencyExchangeRate()
                            }
                        }
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

    override fun onPostResume() {
        super.onPostResume()
        serviceRepetition = SharedPreferencesUtils.getServiceRepetition(this)
        retrieveCurrencyExchangeRate()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.logListener = null
    }
}
