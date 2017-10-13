package com.timbuchalka.currencyapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
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
    private var isFABVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        initSpinner()
        initCurrencyList()
        initCurrencies()
        initLineChart()
        resetDownloads()
        retrieveCurrencyExchangeRate()
        addActionButtonListener()
        showLogs()
    }

    private fun initLineChart() {
        with(line_chart) {
            isHighlightPerDragEnabled = true
            isDragEnabled = true
            setNoDataText("No Data")
            setTouchEnabled(true)
            setScaleEnabled(true)
            setDrawGridBackground(false)
            setPinchZoom(true)
        }

        val lineData = LineData()
        lineData.setValueTextColor(Color.BLUE)
        line_chart.data = lineData

        val legend = line_chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textColor = ColorTemplate.getHoloBlue()

        val xAxis = line_chart.xAxis
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.setAvoidFirstLastClipping(true)

        val yAxis = line_chart.axisLeft
        yAxis.textColor = Color.BLACK
        yAxis.axisMaxValue = 120f
        yAxis.setDrawGridLines(true)

        val yAxisRight = line_chart.axisRight
        yAxisRight.isEnabled = false

    }

    private fun updateLineChart() {
        // line_chart.setDescription ("Currency Exchange Rate: $baseCurrency - $targetCurrency")
        val currencies = currencyTableHelper.getCurrencyHistory(baseCurrency, targetCurrency)
        val lineData = line_chart.data
        lineData.clearValues()
        currencies.forEach {
            addChartEntry(it.date, it.rate)
        }

    }

    private fun addChartEntry(date: String, rate: Double) {
        val lineData = line_chart.data
        if (lineData != null) {
            var lineDataSet = lineData.getDataSetByIndex(0)
            if (lineDataSet == null) {
                lineDataSet = createSet()
                lineData.addDataSet(lineDataSet)
            }

            if (!line_chart.data.xVals.contains(date)) {
                lineData.addDataSet(lineDataSet)
            }

            lineData.addEntry(Entry(rate.toFloat(), lineDataSet.entryCount, 0), 0)
            line_chart.notifyDataSetChanged()
        }
    }

    private fun createSet(): LineDataSet {
        val lineDataSet = LineDataSet(null, "value")
        with(lineDataSet) {
            setDrawCubic(true)
            cubicIntensity = 0.2f
            axisDependency = YAxis.AxisDependency.LEFT
            color = ColorTemplate.getHoloBlue()
            setCircleColor(ColorTemplate.getHoloBlue())
            lineWidth = 2f
            circleSize = 4f
            fillAlpha = 65
            fillColor = ColorTemplate.getHoloBlue()
            highLightColor = Color.CYAN
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }
        return lineDataSet
    }

    private fun showLogs() {
        LogUtils.logListener = object : LogUtils.LogListener {
            override fun onLogged(log: StringBuffer) {
                runOnUiThread {
                    log_text.text = log.toString()
                    log_text.invalidate()
                }
            }
        }
    }

    private fun initCurrencies() {
        baseCurrency = SharedPreferencesUtils.getCurrency(this, true)
        targetCurrency = SharedPreferencesUtils.getCurrency(this, false)
    }

    private fun initCurrencyList() {
        val baseCurrencyAdapter = CurrencyAdapter(this)
        val targetCurrencyAdapter = CurrencyAdapter(this)

        val baseCurrencyIndex: Int = Constants.CURRENCY_CODES.indexOf(baseCurrency)
        val targetCurrencyIndex: Int = Constants.CURRENCY_CODES.indexOf(targetCurrency)

        with(base_currency_list) {
            adapter = baseCurrencyAdapter
            setItemChecked(baseCurrencyIndex, true)
            setSelection(baseCurrencyIndex)
        }

        with(target_currency_list) {
            adapter = targetCurrencyAdapter
            setItemChecked(targetCurrencyIndex, true)
            setSelection(targetCurrencyIndex)
        }

        addCurrencySelectionListener()

    }

    private fun addCurrencySelectionListener() {
        base_currency_list.setOnItemClickListener { parent, view, position, id ->
            baseCurrency = Constants.CURRENCY_CODES[position]
            LogUtils.log(TAG, "Base currency has changed to: $baseCurrency")
            SharedPreferencesUtils.updateCurrency(this, baseCurrency, true)
            retrieveCurrencyExchangeRate()
        }
        target_currency_list.setOnItemClickListener { parent, view, position, id ->
            targetCurrency = Constants.CURRENCY_CODES[position]
            LogUtils.log(TAG, "Target currency has changed to: $targetCurrency")
            SharedPreferencesUtils.updateCurrency(this, targetCurrency, true)
            retrieveCurrencyExchangeRate()
        }

    }

    private fun initSpinner() {
        with(time_frequency) {
            isSaveEnabled = true
            setSelection(SharedPreferencesUtils.getServiceRepetition(this@MainActivity), false)
            post {
                time_frequency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        SharedPreferencesUtils.updateServiceRepetition(this@MainActivity, position)
                        serviceRepetition = position
                        if (position >= AlarmUtils.REPEAT.values().size) {
                            AlarmUtils.stopService()
                        } else {
                            retrieveCurrencyExchangeRate()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
            }
        }
    }

    private fun retrieveCurrencyExchangeRate() {
        if (serviceRepetition >= AlarmUtils.REPEAT.values().size) return

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
                        } else {
                            updateLineChart()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_logs -> {
                LogUtils.clearLog()
                return true
            }
            R.id.action_show_log -> {
                isLogVisible = !isLogVisible
                item.setIcon(
                        if (isLogVisible) R.mipmap.ic_keyboard_hide
                        else R.mipmap.ic_keyboard)

                log_layout.visibility = if (isLogVisible) View.VISIBLE else View.GONE

            }
            R.id.action_show_fab -> {
                isFABVisible = !isFABVisible
                item.setIcon(if (isFABVisible) R.mipmap.ic_remove else R.mipmap.ic_add)
                fab.visibility = if (isFABVisible) View.VISIBLE else View.GONE
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    private fun addActionButtonListener() {
        fab.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity, fab)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.clear_database -> {
                        currencyTableHelper.clearCurrencyTable()
                        LogUtils.log(TAG, "Currency database has been cleared.")
                        line_chart.clearValues()
                        updateLineChart()
                    }
                    R.id.graph -> {
                        currency_list_layout.visibility = View.GONE
                        line_chart.visibility = View.VISIBLE
                        updateLineChart()
                    }
                    R.id.selection -> {
                        currency_list_layout.visibility = View.VISIBLE
                        line_chart.visibility = View.GONE
                    }
                }
                true
            }

            popupMenu.show()

        }

    }
}
