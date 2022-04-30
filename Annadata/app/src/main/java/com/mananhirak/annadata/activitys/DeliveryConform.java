package com.mananhirak.annadata.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;
import com.mananhirak.annadata.notify.SendNotify;

import java.util.Objects;

public class DeliveryConform extends AppCompatActivity implements OnMapReadyCallback {

    String DELIVERY_DOC_ID;
    TextView t1,t2,t3,t4,t5,t6;
    Button conformDeliveryButton, cancelDeliveryButton;
    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    MapFragment mapFragment;
    GeoPoint geoPoint;
    ScrollView scrollView;

    ImageView backButton;


    private void IdSaver() {


        t1=findViewById(R.id.act_dc_foodname);
        t2=findViewById(R.id.act_dc_foodweight);
        t3=findViewById(R.id.act_dc_totalorder);
        t4=findViewById(R.id.aact_dc_sellerfoodinfo);
        t5=findViewById(R.id.act_dc_deliverytime);
        t6=findViewById(R.id.act_dc_buyername);

        conformDeliveryButton = findViewById(R.id.act_dc_conform);
        cancelDeliveryButton = findViewById(R.id.act_dc_cancel);

        backButton = findViewById(R.id.act_dc_back_button);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_conform);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent=getIntent();
        DELIVERY_DOC_ID=intent.getStringExtra("DELIVERY_DOC_ID");

        FIRE_db=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();


        IdSaver();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireDelivery).document(DELIVERY_DOC_ID).get()
                .addOnSuccessListener(this::setDELIVERY);

    }

    private void getGeoPoints() {

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.act_dc_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        scrollView = findViewById(R.id.act_dc_scroll);
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        googleMap.setTrafficEnabled(true);

        Log.d(Mtag.Tag, "onMapReady: ");
        googleMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), 15));

    }

   private void setDELIVERY(DocumentSnapshot documentSnapshot) {

         DELIVERY_BOY deliveryBoy=documentSnapshot.toObject(DELIVERY_BOY.class);
         assert deliveryBoy != null;

         geoPoint=deliveryBoy.getDELIVERY_ADDRESS();
         getGeoPoints();

         String s="Food : "+deliveryBoy.getFOOD_NAME();
        t1.setText(s);

        FIRE_db.collection(FireTag.FireUser)
                .document(deliveryBoy.getBUYER_ID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String s1="Buyer : "+documentSnapshot.get("user_NAME");
                        t6.setText(s1);
                    }
                });


         String s3="Delivery Time : "+deliveryBoy.getDELIVERY_TIME();
         t5.setText(s3);

         String s4="No. Order  :  "+deliveryBoy.getTOTAL_ORDER();
         t3.setText(s4);


         FIRE_db.collection(FireTag.FireMarket).document(deliveryBoy.getFOOD_ID()).get()
                 .addOnSuccessListener(documentSnapshot1 -> {
                     String s6="Food Info: "+ documentSnapshot1.get("food_INFO");
                     t4.setText(s6);

                     String s7= "Weight: "+ documentSnapshot1.get("food_WEIGHT")+" KG";
                     t2.setText(s7);

                 });

   DocumentReference HISTORY_DOC_REF  =FIRE_db.collection(FireTag.FireUser)
               .document((String) Objects.requireNonNull(documentSnapshot.get("buyer_ID")))
               .collection(FireTag.FireHistory)
               .document((String) Objects.requireNonNull(documentSnapshot.get("buyer_HISTORY_ID")));

        conformDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                documentSnapshot.getReference()
                        .update("conform_DELIVERY", true);

                FIRE_db.collection(FireTag.FireUser)
                        .document((String) Objects.requireNonNull(documentSnapshot.get("buyer_ID")))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                HistoryStatusChange(BUY_HISTORY.ORDER_CONFORM
                                        ,HISTORY_DOC_REF
                                        ,(String) documentSnapshot.get("fcm_TOKEN")
                                        ,""
                                );
                               }
                        });


            }
        });

        cancelDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                documentSnapshot.getReference()
                        .update("cancel_DELIVERY",true);

                LayoutInflater layoutInflater=LayoutInflater.from(DeliveryConform.this);
                View v=layoutInflater.inflate(R.layout.dial_cancelorder,null);

                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(DeliveryConform.this)
                        .setView(v)
                        .create();
                alertDialog.show();

                EditText cancelReason= v.findViewById(R.id.dial_co_cancelreason);
                Button sendQuery= v.findViewById(R.id.dial_co_sendquery);

                sendQuery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        FIRE_db.collection(FireTag.FireUser)
                                .document((String) Objects.requireNonNull(documentSnapshot.get("buyer_ID")))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        HistoryStatusChange(BUY_HISTORY.ORDER_CANCEL
                                                ,HISTORY_DOC_REF
                                                ,(String) documentSnapshot.get("fcm_TOKEN")
                                                ,cancelReason.getText().toString()
                                        );
                                    }
                                });

                    }
                });

           /*     i1=v.findViewById(R.id.);
                i1.setOnClickListener(view -> alertDialog.dismiss());

                i2=v.findViewById(R.id.dial_fp_foodpic);

                Glide.with(context)
                        .load(snapshot.get("photo_URL"))
                        .into(i2);
*/

                }
        });


    }


    private void ConformOrderNotify(String token){

        SendNotify sendNotify= new SendNotify(DeliveryConform.this);
        sendNotify.StartNotify(token,"Order Conform..","Your Order is conform","No Need");
        finish();
    }

    private void CancelOrderNotify(String token,String DisplayMessage){

        SendNotify sendNotify= new SendNotify(DeliveryConform.this);
        sendNotify.StartNotify(token
                ,"Order Cancel.."
                ,"Your Order is Canceled Reason is "+DisplayMessage
                ,"No Need"
        );
        finish();
    }

    private void HistoryStatusChange(int code , @NonNull DocumentReference HistoryOrderDocId , String token,String DisplayMessage){
       HistoryOrderDocId
               .update("order_STATUS",code)
       .addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               switch (code) {
                   case BUY_HISTORY.ORDER_CONFORM:
                       ConformOrderNotify(token);
                       break;
                   case BUY_HISTORY.ORDER_CANCEL:
                       CancelOrderNotify(token,DisplayMessage);
                       break;
               }
           }
       });
    }

}