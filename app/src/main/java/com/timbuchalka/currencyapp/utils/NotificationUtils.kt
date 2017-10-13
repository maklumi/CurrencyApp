package com.timbuchalka.currencyapp.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.text.TextUtils

/**
 * Created by HomePC on 13/10/2017.
 */
object NotificationUtils {

    private val TAG = NotificationUtils::class.java.simpleName

    fun showNotificationMessage(ctx: Context, title: String, message: String) {
        if (TextUtils.isEmpty(message)) return


    }

    fun isAppInBackground(ctx: Context): Boolean {
        var isInBackground = true
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = activityManager.runningAppProcesses
            runningProcesses.forEach { it ->
                if (it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    it.pkgList.forEach { activeProcess ->
                        if (activeProcess == ctx.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else { // older version of android
            val taskInfo = activityManager.getRunningTasks(1)
            val componentName = taskInfo[0].topActivity
            if (componentName.packageName == ctx.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }
}