package com.manan.annadata.notify

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manan.annadata.idtag.Mtag

class FireNotify : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.size > 0) {
            val m = remoteMessage.data
            val title = m["title"]
            val message = m["message"]
            Log.d(Mtag.Tag, "onMessageReceived: " + m["id"])
            val build = NotifyBuild(applicationContext)
            build.BuildNotification(title, message, m["id"])
        } else {
            Log.d(Mtag.Tag, "onMessageReceivertrtd: $remoteMessage")
        }
    }
}