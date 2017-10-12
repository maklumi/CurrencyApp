package com.timbuchalka.currencyapp.database

import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.value_objects.Currency

/**
 * Created by HomePC on 13/10/2017.
 */

class CurrencyTableHelper(private val adapter: CurrencyDatabaseAdapter) {

    fun insertCurrency(currency: Currency) {
        val currencies = getCurrencyHistory(currency.base, currency.name, currency.date)
    }

    private fun getCurrencyHistory(base: String, name: String, date: String): ArrayList<Currency> {
        val currencies = arrayListOf<Currency>()
        val cursor = adapter.writableDatabase.query(
                Constants.CURRENCY_TABLE,
                arrayOf(Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE,
                        Constants.KEY_NAME),
                "${Constants.KEY_BASE} = '$base' + AND ${Constants.KEY_NAME} = '$name' AND" +
                        " ${Constants.KEY_DATE} = '$date'", null, null, null, null
        )

        return currencies
    }


}