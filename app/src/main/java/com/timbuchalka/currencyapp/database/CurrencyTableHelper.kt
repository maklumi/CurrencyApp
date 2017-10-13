package com.timbuchalka.currencyapp.database

import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.value_objects.Currency
import org.jetbrains.anko.db.*

/**
 * Created by HomePC on 13/10/2017.
 */

class CurrencyTableHelper(private val adapter: CurrencyDatabaseAdapter) {

    private var rowParser = MyRowParser()

    fun insertCurrency(currency: Currency): Long {
        val currencies = getCurrencyHistory(currency.base, currency.name, currency.date)
        if (currencies.size == 0) {

            val id: Long = adapter.use {
                insert(Constants.CURRENCY_TABLE,
                        Constants.KEY_BASE to currency.base,
                        Constants.KEY_DATE to currency.date,
                        Constants.KEY_RATE to currency.rate,
                        Constants.KEY_NAME to currency.name)
            }
            return id
        }
        return currencies[0].id
    }

    private fun getCurrencyHistory(base: String, name: String, date: String): ArrayList<Currency> {
        val cursor = adapter.writableDatabase.query(
                Constants.CURRENCY_TABLE,
                arrayOf(Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE,
                        Constants.KEY_NAME),
                "${Constants.KEY_BASE} = '$base'  AND ${Constants.KEY_NAME} = '$name' AND" +
                        " ${Constants.KEY_DATE} = '$date'", null, null, null, null
        )
        val currencies: List<Currency> = cursor.parseList(rowParser)
        cursor.close()
        return ArrayList(currencies)
    }

    private fun getCurrencyHistory(base: String, name: String): ArrayList<Currency> {
        val cursor = adapter.writableDatabase.query(
                Constants.CURRENCY_TABLE,
                arrayOf(Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE,
                        Constants.KEY_NAME),
                "${Constants.KEY_BASE} = '$base'  AND ${Constants.KEY_NAME} = '$name'",
                null, null, null, null
        )
        cursor.close()
        val rowParser = classParser<Currency>()
        return ArrayList(cursor.parseList(rowParser))
    }

    fun getCurrency(id: Long): Currency? {
        val cursor = adapter.writableDatabase.query(
                Constants.CURRENCY_TABLE,
                arrayOf(Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE,
                        Constants.KEY_NAME),
                "${Constants.KEY_ID} = $id", null, null, null, null
        )
        cursor?.let {
            if (cursor.moveToFirst())
                return cursor.parseSingle(rowParser)
        }
        cursor?.close()
        return null
    }

    fun clearCurrencyTable() {
        adapter.writableDatabase.delete(Constants.CURRENCY_TABLE, null, null)
    }

    inner class MyRowParser : MapRowParser<Currency> {
        override fun parseRow(columns: Map<String, Any?>): Currency {
            return Currency(columns[Constants.KEY_RATE] as Double, columns[Constants.KEY_DATE] as String,
                    columns[Constants.KEY_BASE] as String, columns[Constants.KEY_NAME] as String,
                    columns[Constants.KEY_ID] as Long)
        }
    }
}

