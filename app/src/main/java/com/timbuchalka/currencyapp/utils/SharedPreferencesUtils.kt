package com.timbuchalka.currencyapp.utils

import android.content.Context
import com.timbuchalka.currencyapp.Constants

/**
 * Created by HomePC on 13/10/2017.
 */
object SharedPreferencesUtils {

    val TAG = SharedPreferencesUtils::class.java.simpleName

    fun getCurrency(ctx: Context, isBaseCurrency: Boolean): String {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )

        return sharedPreferences.getString(
                if (isBaseCurrency) Constants.BASE_CURRENCY else Constants.TARGET_CURRENCY,
                if (isBaseCurrency) "USD" else "MYR"
        )
    }

    fun updateCurrency(ctx: Context, currency: String, isBaseCurrency: Boolean) {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )

        val editor = sharedPreferences.edit()
        editor.putString(if (isBaseCurrency) Constants.BASE_CURRENCY else Constants.TARGET_CURRENCY, currency)
        editor.apply()
    }

    fun getServiceRepetition(ctx: Context): Int {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(Constants.SERVICE_REPETITION, 0)
    }

    fun updateServiceRepetition(ctx: Context, serviceRepetition: Int) {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(Constants.SERVICE_REPETITION, serviceRepetition)
        editor.apply()
    }

    fun getNumDownloads(ctx: Context): Int {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(Constants.NUM_DOWNLOADS, 0)
    }

    fun updateNumDownloads(ctx: Context, numDownloads: Int) {
        val sharedPreferences = ctx.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(Constants.NUM_DOWNLOADS, numDownloads)
        editor.apply()
    }
}