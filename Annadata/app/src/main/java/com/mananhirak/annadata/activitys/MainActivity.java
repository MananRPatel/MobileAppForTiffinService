package com.mananhirak.annadata.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.idtag.Mtag;

import java.util.Objects;




public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

     CardView c1,c2,c3,c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        IdSaver();

        c1.setOnClickListener(view -> {
                Intent intent=new Intent(MainActivity.this,Profile.class);
                startActivity(intent);
        });

        c2.setOnClickListener(view -> {

                Intent intent=new Intent(MainActivity.this,FoodSell.class);
                startActivity(intent);

        });

        c3.setOnClickListener(view -> {

                Intent intent=new Intent(MainActivity.this,FoodBuy.class);
                startActivity(intent);

        });

        c4.setOnClickListener(view -> {

                Intent intent = new Intent(MainActivity.this, FoodDelivery.class);
                startActivity(intent);

        });

    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            Intent intent = new Intent(this, Flash.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    private void IdSaver(){
        c1=findViewById(R.id.mainprofile);
        c2=findViewById(R.id.mainseller);
        c3=findViewById(R.id.mainbuyer);
        c4=findViewById(R.id.maindelivery);
    }

}