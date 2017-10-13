package com.timbuchalka.currencyapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Created by HomePC on 13/10/2017.
 */
object AlarmUtils {

    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null

    private val TAG = AlarmUtils::class.java.simpleName

    enum class REPEAT {
        EVERY_MINUTE, EVERY_2_MINUTE, EVERY_5_MINUTE, EVERY_20_MINUTE,
        EVERY_HOUR, EVERY_DAY
    }

    val REPEAT_TIME = intArrayOf(60, 120, 300, 1200, 3600, 86400)

    fun startService(ctx: Context, intent: Intent, repeat: REPEAT) {
        stopService()
        pendingIntent = PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().timeInMillis,
                REPEAT_TIME[repeat.ordinal] * 1000L,
                pendingIntent)
        LogUtils.log(TAG, "Alarm has been started. $repeat")
    }

    private fun stopService() {
        if (alarmManager != null && pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            LogUtils.log(TAG, "Alarm has been stopped")
        }
    }
}