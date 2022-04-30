package com.mananhirak.annadata.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Flash extends AppCompatActivity {

    Intent intent;
    int LOCATION_PERMISSION_CODE=44;
    boolean checkToken;
    String SellerId,ProductId;

    // hirak
    TextView textView;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        Objects.requireNonNull(getSupportActionBar()).hide();


        // hirak
        textView = findViewById(R.id.textView);
        animateText("Annadata");

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               checkPermissionIsGivenOrNot();
            }
        },900);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            textView.setText(charSequence.subSequence(0,index++));

            if(index <= charSequence.length()){
                handler.postDelayed(runnable,delay);
            }
        }
    };

    public void animateText(CharSequence cs){
        charSequence = cs;
        index = 0;
        textView.setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,delay);
    }

    private void SECURITY_CHECK(){

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseFirestore.getInstance().
                    collection(FireTag.FireUser)
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        FOOD_USERS foodUsers=documentSnapshot.toObject(FOOD_USERS.class);

                        if(foodUsers==null){
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else if(foodUsers.getPHOTO_URL()==null){
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else if(foodUsers.getADDRESS()==null) {
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else if(foodUsers.getUSER_LOCATION()==null){
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else if(foodUsers.getUSER_NAME()==null){
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else if(foodUsers.getID()==null){
                            intent = new Intent(Flash.this, AdditionInfo.class);
                        }else{
                            intent = new Intent(Flash.this, MainActivity.class);
                            FirebaseDynamicLinks.getInstance()
                                    .getDynamicLink(getIntent())
                                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                                        @Override
                                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                            // Get deep link from result (may be null if no link is found)
                                            Uri deepLink = null;
                                            if (pendingDynamicLinkData != null) {
                                                deepLink = pendingDynamicLinkData.getLink();
                                                if(deepLink!=null) {
                                                    SellerId = deepLink.getQueryParameter("sellerid");
                                                    ProductId = deepLink.getQueryParameter("productid");
                                                    if(SellerId!=null) {
                                                        Log.d(Mtag.Tag, "onSu96ccess: " + SellerId);
                                                        Intent intent1 = new Intent(Flash.this, FoodBuy.class);
                                                        intent1.putExtra("SellerIDFromSharableLink", SellerId);
                                                        startActivity(intent1);
                                                        finish();
                                                    }else if(ProductId!=null){
                                                        Log.d(Mtag.Tag, "onSu96ccess: " + ProductId);
                                                        Intent intent1 = new Intent(Flash.this, SellConform.class);
                                                        intent1.putExtra("FIRE_STORE_DOCUMENT", ProductId);
                                                        startActivity(intent1);
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(Mtag.Tag, "getDynamicLink:onFailure", e);
                                        }
                                    });
                        }
                        startActivity(intent);
                        finish();
                    });

        }else{

            List<AuthUI.IdpConfig> provider= Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
                  /*  , new AuthUI.IdpConfig.PhoneBuilder()
                    .setDefaultCountryIso("IN")
                    .build()*/
            );

            Intent intent= AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .build();
            startActivityForResult(intent,7144);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(requestCode==7144){
            switch(resultCode) {
                case RESULT_OK:
                assert user != null;
                Log.d(Mtag.Tag, "onActivityResult: " + user.getDisplayName());

                if ((Objects.requireNonNull(user.getMetadata()).getCreationTimestamp()) == (user.getMetadata().getLastSignInTimestamp())) {
                    Intent intent = new Intent(this, AdditionInfo.class);
                    startActivity(intent);
                    finish();
                } else {

                    FirebaseFirestore.getInstance()
                            .collection(FireTag.FireUser)
                            .document(user.getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.get("fcm_TOKEN")==null) {
                                        Log.d(Mtag.Tag, "SECURITY_CHECK: 656gfhf");
                                        SetFCMToken(documentSnapshot);
                                    }else if(CheckOldFCM((String) documentSnapshot.get("fcm_TOKEN"))){
                                        Log.d(Mtag.Tag, "SECURITY_CHECK:dfdfdfdfdf 656gfhf");
                                        SetFCMToken(documentSnapshot);
                                    }else{
                                        Intent intent = new Intent(Flash.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                }
                break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "Sign-In must Required ", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
            }
        }
    }

    private  void SetFCMToken(DocumentSnapshot documentSnapshot){

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> documentSnapshot.getReference().update("fcm_TOKEN",s)
                .addOnSuccessListener(unused -> {
                    intent = new Intent(Flash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }));

    }



    public boolean CheckOldFCM(String token ) {

        checkToken=true;

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> {
                    checkToken= !(token.equals(s));
                });
        return checkToken;
    }

    void checkPermissionIsGivenOrNot(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }else SECURITY_CHECK();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SECURITY_CHECK();
            } else {
                Toast.makeText(this, "Without Permission can't Use App", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }


}