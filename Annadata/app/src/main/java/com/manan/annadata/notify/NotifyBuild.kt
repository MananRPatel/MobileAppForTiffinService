package com.manan.annadata.notify

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manan.annadata.idtag.Mtag
import com.manan.annadata.notify.NotifyBuild
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.VolleyError
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.DefaultRetryPolicy
import android.os.Build
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import com.manan.annadata.activitys.DeliveryConform
import android.app.PendingIntent
import android.content.Context
import com.manan.annadata.R
import androidx.core.content.ContextCompat
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.manan.annadata.activitys.Profile

class NotifyBuild(var context: Context) {
    var title: String? = null
    var message: String? = null
    var bigIconRid: ImageView? = null
    @RequiresApi(Build.VERSION_CODES.S)
    fun BuildNotification(title: String?, message: String?, id: String?) {
        this.title = title
        this.message = message
        createChannel()
        createNotification(id)
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESC
            val manager = context.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun createNotification(id: String?) {
        val intent: Intent
        if (id == "No Need") {
            intent = Intent(context, Profile::class.java)
        } else {
            intent = Intent(context, DeliveryConform::class.java)
            intent.putExtra("DELIVERY_BOY_ID", id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context, CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_baseline_storefront_24)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher2))
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setContentText(message) //                .addAction("Mark as Read", )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManagerCompat = NotificationManagerCompat.from(
            context
        )
        notificationManagerCompat.notify(
            (System.currentTimeMillis() % 10000000).toInt(),
            builder.build()
        )
    }

    companion object {
        const val CHANNEL_ID = "annadata"
        const val CHANNEL_NAME = "Annadata"
        const val CHANNEL_DESC = "Annadata Notify Users"
    }
}