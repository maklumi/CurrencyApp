package com.timbuchalka.currencyapp

/**
 * Created by HomePC on 12/10/2017.
 */
object Constants {
    val CURRENCY_URL = "http://api.fixer.io/latest?base="

    // for parsing currency from json response
    val BASE = "base"
    val DATE = "date"
    val RATES = "rates"

    // for currency service and receiver
    val URL = "url"
    val RECEIVER = "receiver"
    val RESULT = "result"
    val CURRENCY_BASE = "currencyBase"
    val CURRENCY_NAME = "currencyName"
    val REQUEST_ID = "requestId"
    val BUNDLE = "bundle"
    val STATUS_RUNNING = 0
    val STATUS_FINISHED = 1
    val STATUS_ERROR = 2

    // for database and table
    val DATABASE_NAME = "CurrencyDB"
    val CURRENCY_TABLE = "currencies"
    val KEY_ID = "_id"
    val KEY_BASE = "base"
    val KEY_DATE = "date"
    val KEY_RATE = "rate"
    val KEY_NAME = "name"

    // max number of retrieval running in background
    val MAX_DOWNLOADS = 5

    // for all currency code and name
    val CURRENCY_CODE_SIZE = 32

    val CURRENCY_CODES = arrayOf(
            "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HRK",
            "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN",
            "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    )

    val CURRENCY_NAMES = arrayOf("Australian Dollar", "Bulgarian Lev", "Brazilian Real",
            "Canadian Dollar", "Swiss Franc", "Yuan Renminbi", "Czech Koruna", "Danish Krone",
            "Euro", "Pound Sterling", "Hong Kong Dollar", "Croatian Kuna", "Hungarian Forint",
            "Indonesian Rupiah", "Israeli New Shekel", "Indian Rupee", "Japanese Yen", "Korean Won",
            "Mexican Nuevo Peso", "Malaysian Ringgit", "Norwegian Krone", "New Zealand Dollar",
            "Philippine Peso", "Polish Zloty", "Romanian New Leu", "Belarussian Ruble",
            "Swedish Krona", "Singapore Dollar", "Thai Baht", "Turkish Lira", "US Dollar",
            "South African Rand"
    )

    // Constant for notification
    val NOTIFICATION_ID = 100

    // Constants for SharedPreferences
    val CURRENCY_PREFERENCES = "CURRENCY_PREFERENCES"
    val BASE_CURRENCY = "BASE_CURRENCY"
    val TARGET_CURRENCY = "TARGET_CURRENCY"
    val SERVICE_REPETITION = "SERVICE_REPETITION"
    val NUM_DOWNLOADS = "NUM_DOWNLOADS"

    // for web connection
    val CONNECTION_TIMEOUT = 10000
    val READ_TIMEOUT = 10000


}