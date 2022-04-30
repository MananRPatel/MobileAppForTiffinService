package com.mananhirak.annadata.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activitys.DeliveryConform;
import com.mananhirak.annadata.activitys.Profile;

public class NotifyBuild {

    public static final String CHANNEL_ID="annadata";
    public static final String CHANNEL_NAME="Annadata";
    public static final String CHANNEL_DESC="Annadata Notify Users";

    Context context;
    String title,message;


    public NotifyBuild(Context context) {
        this.context = context;
    }


    public void BuildNotification(String title, String message,String id){

        this.title =  title;

        this.message = message;

        createChannel();

        createNotification(id);

    }

    public void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);

        }

    }

    public void createNotification(String id) {

        Intent intent;


        if(id.equals("No Need")){
            intent = new Intent(context, Profile.class);
        }else{
            intent =new Intent(context, DeliveryConform.class);
            intent.putExtra("DELIVERY_BOY_ID",id);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );




        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_storefront_24)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher2))
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
//                .addAction("Mark as Read", )
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int)((System.currentTimeMillis()%10000000)),builder.build());

    }


    }
