package com.mananhirak.annadata.notify;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FireNotify extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
       super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size()>0){

            Map<String,String> m=remoteMessage.getData();

            String title = m.get("title");
            String message = m.get("message");
            Log.d(Mtag.Tag, "onMessageReceived: "+m.get("id"));

            NotifyBuild build = new NotifyBuild(getApplicationContext());

            build.BuildNotification(title,message,m.get("id"));


        }else{
            Log.d(Mtag.Tag, "onMessageReceivertrtd: "+remoteMessage);
        }

    }
}
