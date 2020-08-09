package com.sudoajay.pdfviewer.firebase


import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sudoajay.pdfviewer.R
import com.sudoajay.pdfviewer.firebase.NotificationChannels.notificationOnCreate


class MyFirebaseMessagingService : FirebaseMessagingService() {


//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        val data = remoteMessage.data
//        val url = data["Click_Action"]
//        if (remoteMessage.notification != null && url!!.isNotEmpty() && url.isNotBlank()) {
//
//
//            val notificationCompat: NotificationCompat.Builder =
//                NotificationCompat.Builder(applicationContext, NotificationChannels.SERVICE_OPEN_URL)
//            notificationCompat.setSmallIcon(R.drawable.ic_pdf)
//
//            notificationCompat.setContentIntent(createPendingIntent(url.toString()))
//
//            FirebaseNotification(applicationContext).notifyCompat(
//                remoteMessage.notification,
//                notificationCompat
//            )
//        }
//    }

    private fun createPendingIntent(link:String): PendingIntent? {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onNewToken(token: String) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.e("MainActivityClass", token.toString())
    }


}