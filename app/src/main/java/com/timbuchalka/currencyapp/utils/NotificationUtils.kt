package com.timbuchalka.currencyapp.utils

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.widget.Toast
import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.MainActivity
import com.timbuchalka.currencyapp.R

/**
 * Created by HomePC on 13/10/2017.
 */
object NotificationUtils {

    private val TAG = NotificationUtils::class.java.simpleName

    fun showNotificationMessage(ctx: Context, title: String, message: String) {
        if (TextUtils.isEmpty(message)) return

        if (isAppInBackground(ctx)) {
            val icon = R.mipmap.ic_launcher
            val intent = Intent(ctx, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val resultPendingIntent = PendingIntent.getActivity(ctx, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT)

            val inboxStyle = NotificationCompat.InboxStyle()
            val builder = NotificationCompat.Builder(ctx, title)
            val notification: Notification = with(builder) {
                setSmallIcon(icon)
                setTicker(title)
                setWhen(0)
                setAutoCancel(true)
                setContentTitle(title)
                setStyle(inboxStyle)
                setContentIntent(resultPendingIntent)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                setLargeIcon(BitmapFactory.decodeResource(ctx.resources, icon))
                setContentText(message)
                build()
            }

            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Constants.NOTIFICATION_ID, notification)
        } else {
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun isAppInBackground(ctx: Context): Boolean {
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