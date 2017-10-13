package com.timbuchalka.currencyapp.helpers

import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.value_objects.Currency
import org.json.JSONObject

/**
 * Created by HomePC on 12/10/2017.
 */

object CurrencyParserHelper {

    fun parseCurrency(obj: JSONObject, currencyName: String): Currency {
        val rateObject = obj.optJSONObject(Constants.RATES)
        return Currency(
                rateObject.optDouble(currencyName),
                obj.optString(Constants.DATE),
                obj.optString(Constants.BASE),
                currencyName,
                obj.optLong(Constants.KEY_ID, 0L)
        )

    }
    /*
    private var rate: Double,
    private var date: String,
    private var base: String,
    private var name: String,
    private var id: Long)
    */

}