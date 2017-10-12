package com.timbuchalka.currencyapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.timbuchalka.currencyapp.Constants
import org.jetbrains.anko.db.*
import java.sql.Date

/**
 * Created by HomePC on 13/10/2017.
 */
class CurrencyDatabaseAdapter(ctx: Context) :
        ManagedSQLiteOpenHelper(ctx, Constants.DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val TAG = CurrencyDatabaseAdapter::class.java.simpleName
        val DATABASE_VERSION = 10
        private var instance: CurrencyDatabaseAdapter? = null

        @Synchronized
        fun getInstance(ctx: Context): CurrencyDatabaseAdapter {
            if (instance == null) {
                instance = CurrencyDatabaseAdapter(ctx.applicationContext)
            }
            return instance!!
        }

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(Constants.CURRENCY_TABLE, true,
                Constants.KEY_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Constants.KEY_BASE to TEXT + NOT_NULL,
                Constants.KEY_NAME to TEXT + NOT_NULL,
                Constants.KEY_RATE to REAL)
            //    Constants.KEY_DATE to Date)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(Constants.CURRENCY_TABLE, true)
    }
}

val Context.database: CurrencyDatabaseAdapter
    get() = CurrencyDatabaseAdapter.getInstance(applicationContext)