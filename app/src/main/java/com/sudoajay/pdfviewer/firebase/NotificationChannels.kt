package com.sudoajay.pdfviewer.firebase

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.sudoajay.pdfviewer.R

/**
 * Static class containing IDs of notification channels and code to create them.
 */
object NotificationChannels {
    private const val GROUP_SERVICE = "com.sudoajay.dnswidget.notifications.service"
    private const val SERVICE_OPEN_URL = "com.sudoajay.dnswidget.notifications.open_Url"


    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun notificationOnCreate(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_SERVICE,
                context.getString(R.string.notifications_group_service)
            )
        )

        val runningChannel = NotificationChannel(
            SERVICE_OPEN_URL,
            context.getString(R.string.default_notification_channel_id),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        runningChannel.description = context.getString(R.string.default_notification_channel_id)
        runningChannel.group = GROUP_SERVICE
        runningChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(runningChannel)







    }
}