package com.mananhirak.annadata.activitys;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activityshelpers.HeyToaster;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;
import com.mananhirak.annadata.notify.SendNotify;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SellConform extends AppCompatActivity implements OnMapReadyCallback {


    EditText orderFoodNumbersET;
    String FIREBASE_DOCUMENT_ID;
    boolean Time_Flag;
    String Food_time;
    int Hour,Minute;
    Calendar calendar;
    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    TextView t1,t2,t3,t4,t5,t6,t7,t8;
    ImageView i;
    Button button,button2,close_dialog;
    ImageButton locationAddress,tiffinShareLink;
    Dialog dialog,dialogForLoad;
    MapFragment mapFragment;
    GeoPoint geoPoint;
    String cityName;
    Long currentLeftOrder;


    ImageView backButton;


    ListenerRegistration listenerRegistration_of_orderLeft;


    private void IdSaver(){

        t2=findViewById(R.id.scfoodname);
        t3=findViewById(R.id.scfoodvalue);
        t4=findViewById(R.id.scfoodweight);
        t5=findViewById(R.id.scfoodtime);
        t6=findViewById(R.id.scfooddate);
        t7=findViewById(R.id.scfoodleft);


        orderFoodNumbersET =findViewById(R.id.scnumberoforder);

        button=findViewById(R.id.scconformbutton);

        button2 = findViewById(R.id.scordertime);

        locationAddress=findViewById(R.id.act_sc_location);

        tiffinShareLink = findViewById(R.id.act_sc_sharetiffinlink);



        backButton = findViewById(R.id.act_sc_back_button);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_conform);
        Objects.requireNonNull(getSupportActionBar()).hide();

        user= FirebaseAuth.getInstance().getCurrentUser();

        Intent get_intent=getIntent();
        FIREBASE_DOCUMENT_ID=get_intent.getStringExtra("FIRE_STORE_DOCUMENT");

        FIRE_db= FirebaseFirestore.getInstance();

        IdSaver();

        dialogForLoad = new Dialog(this);
        dialogForLoad.setContentView(R.layout.dial_set_animation);
        dialogForLoad.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogForLoad.setCancelable(false);
        dialogForLoad.create();
        LottieAnimationView lottieAnimationView=dialogForLoad.findViewById(R.id.dial_sa_load);
        lottieAnimationView.setVisibility(View.VISIBLE);
        dialogForLoad.show();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setUserCityLocation();

        calendar=Calendar.getInstance();

        Hour=calendar.get(Calendar.AM_PM)==Calendar.PM?calendar.get(Calendar.HOUR)+12:calendar.get(Calendar.HOUR);
        Minute=calendar.get(Calendar.MINUTE);

        CharSequence charSequence2= DateFormat.format("hh:mm a",calendar);
        Food_time=(String)charSequence2;
        Log.d(Mtag.Tag,"1->"+Food_time);
        Time_Flag=false;
        button2.setOnClickListener(view -> {
            TimeSelectForOrder();
            Toast.makeText(SellConform.this,Food_time,Toast.LENGTH_SHORT).show();
        });



        Log.d(Mtag.Tag, "onCreate: "+FIREBASE_DOCUMENT_ID);

        FIRE_db.collection(FireTag.FireMarket).document(FIREBASE_DOCUMENT_ID)
                .get().addOnSuccessListener(documentSnapshot -> {
                    SetMarketItems(documentSnapshot);
                });

    }

    private void setUserCityLocation() {

        FIRE_db.collection(FireTag.FireUser)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        geoPoint=(GeoPoint) documentSnapshot.get("user_LOCATION");
                        cityName=(String)documentSnapshot.get("address");
                        createDialogBox();
                    }
                });

    }

    private void createDialogBox(){

        setPermissionControl();

        HeyToaster.HeyToaster1(this,"Using Map Icon you can open the map \nand provide accurate information for deliver your order",R.layout.toast_info);


        dialogForLoad.dismiss();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dial_map);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        close_dialog=dialog.findViewById(R.id.dial_m_close);

        locationAddress.setOnClickListener(view -> openMap());

        close_dialog.setOnClickListener(view -> closeMap());

    }

    private void openMap(){

        dialog.show();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.dial_m_google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    private void closeMap(){
        dialog.dismiss();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        Log.d(Mtag.Tag, "onSuccess: 275");
        setPermissionControl();

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), 10));

        googleMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Marker Set On Your City");


            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            googleMap.addMarker(markerOptions);
            geoPoint =new GeoPoint(latLng.latitude, latLng.longitude);

        });


    }

    void SetMarketItems(DocumentSnapshot documentSnapshot){

        MARKET m=documentSnapshot.toObject(MARKET.class);

        String s;
        assert m != null;
        s="Food Name : "+m.getFOOD_NAME();
        t2.setText(s);
        if(m.getFOOD_PRICE()==0){
            s="Donation Purpose";
        }else{
            s="₹ " +m.getFOOD_PRICE();
        }
        t3.setText(s);
        s=m.getFOOD_WEIGHT()+" KG";
        t4.setText(s);
        s=m.getFOOD_TIME();
        t5.setText(s);
        s=m.getFOOD_DATE();
        t6.setText(s);

        //  Real Time Food Order Left Update
        OrderLeft(documentSnapshot);

//        s="Food info : "+m.getFOOD_INFO();
//        t8.setText(s);

        i = findViewById(R.id.act_s_c_foodphoto);

        Glide.with(this)
                .load(m.getPHOTO_URL())
                .into(i);


        tiffinShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, m.getTIFFIN_LINK());
                sendIntent.setType("text/plain");
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(sendIntent, "Hello World"));
            }
        });

        MakeFinalOrderForUser(m);

    }

    private void OrderLeft(DocumentSnapshot documentSnapshot) {
        listenerRegistration_of_orderLeft = documentSnapshot.getReference().addSnapshotListener((value, error) -> {
            if(error!=null){
                Toast.makeText(SellConform.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }else if(value==null){
                Toast.makeText(SellConform.this, "No Order Left", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Log.d(Mtag.Tag, "\n\n\n\nOrderLeft: "+value.get("left_ORDER")+"\n\n\n\n");

                currentLeftOrder = (long)value.get("left_ORDER");
                String s = currentLeftOrder+" Order Left";
                t7.setText(s);
            }

        });
    }


    void MakeFinalOrderForUser(final MARKET market){

        button.setOnClickListener(view -> {
            final String s2;
            s2= orderFoodNumbersET.getText().toString();
            if(s2.isEmpty()){
                orderFoodNumbersET.setError("Please Put Number Of Order");
                orderFoodNumbersET.requestFocus();
            }else if((currentLeftOrder-(Integer.parseInt(s2))<0)||((Integer.parseInt(s2))<=0)){
                orderFoodNumbersET.setError("Sorry Not Much Order  ");
                orderFoodNumbersET.requestFocus();
            }else if(Time_Flag){
                TimeSelectForOrder();
                Toast.makeText(SellConform.this,"Old Time "+Food_time,Toast.LENGTH_SHORT).show();

            }else{



                DELIVERY_BOY deliveryBoy=new DELIVERY_BOY();
                assert user != null;
                deliveryBoy.setBUYER_ID(user.getUid());
                deliveryBoy.setFOOD_ID(FIREBASE_DOCUMENT_ID);
                deliveryBoy.setFOOD_NAME(market.getFOOD_NAME());
                deliveryBoy.setDELIVERY_ADDRESS(geoPoint);
                deliveryBoy.setDELIVERY_TIME(Food_time);
                deliveryBoy.setFOOD_MONEY(market.getFOOD_PRICE());
                deliveryBoy.setFOOD_POINT(market.getFOOD_POINT());
                deliveryBoy.setTOTAL_ORDER(Integer.parseInt(s2));
                deliveryBoy.setCONFORM_DELIVERY(false);
                deliveryBoy.setBUYER_HISTORY_ID(" ");

                FIRE_db.collection(FireTag.FireUser).
                        document(market.getRETAILER_ID()).collection(FireTag.FireDelivery).
                        add(deliveryBoy)
                        .addOnSuccessListener(
                                new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        AfterDelivery(documentReference,market,s2);
                                    }
                                }
                        );

                            }
        });

    }

    void AfterDelivery(DocumentReference DeliveryDoc,MARKET market,String s2){

        String DeliveryId= DeliveryDoc.getId();

        Map<String,Object> map=new HashMap<>();
        map.put("left_ORDER",FieldValue.increment(-Integer.parseInt(s2)));

        if(market.getLEFT_ORDER()-(Integer.parseInt(s2))<=0)
            map.put("no_FOOD_LEFT",true);

        FIRE_db.collection(FireTag.FireMarket).document(FIREBASE_DOCUMENT_ID)
                .update(map);


        FIRE_db.collection(FireTag.FireUser).document(market.getRETAILER_ID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        SellConform.this.HistoryCreateForUserOrder(DeliveryDoc,documentSnapshot, market, s2, DeliveryId);
                    }
                });

    }

    void HistoryCreateForUserOrder(DocumentReference DeliveryDoc,DocumentSnapshot RetailerDoc,MARKET market,String ORDER,String DeliveryId){

        final BUY_HISTORY buyHistory=new BUY_HISTORY();

        // User is Retailer because we want ot set retailer name is buyer History

        FOOD_USERS foodUsers=RetailerDoc.toObject(FOOD_USERS.class);
        assert foodUsers != null;
        Log.d(Mtag.Tag, "HISTORY_RECORD: "+foodUsers.getUSER_NAME());

        buyHistory.setFOOD_ID(FIREBASE_DOCUMENT_ID);
        buyHistory.setFOOD_NAME(market.getFOOD_NAME());
        buyHistory.setFOOD_WEIGHT(market.getFOOD_WEIGHT());
        buyHistory.setTOTAL_FOOD(Integer.parseInt(ORDER));
        buyHistory.setRETAILER_ID(foodUsers.getID());
        buyHistory.setFOOD_INFO(market.getFOOD_INFO());
        buyHistory.setFOOD_POINT(market.getFOOD_POINT());
        buyHistory.setFOOD_PRICE(market.getFOOD_PRICE());
        buyHistory.setORDER_STATUS(BUY_HISTORY.ORDER_PLACED);
        buyHistory.setRATING_FOOD(0);


        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireHistory).add(buyHistory)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Map<String,Object> map=new HashMap<>();
                map.put("buyer_HISTORY_ID",documentReference.getId());
                DeliveryDoc.update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                GetFCMToken(foodUsers.getFCM_TOKEN(),foodUsers.getUSER_NAME(),DeliveryId);
                            }
                        });
            }
        });



    }

    void GetFCMToken(String token,String Retailer,String DeliveryId){
        SendNotify sendNotify =new SendNotify(this);
        sendNotify.StartNotify(token,"New Order... "+Retailer,"Check All Details",DeliveryId);
        Toast.makeText(SellConform.this,Food_time,Toast.LENGTH_SHORT).show();
        finish();

    }

    public void TimeSelectForOrder(){

        TimePickerDialog timePickerDialog=new TimePickerDialog(this, (timePicker, i, i1) -> {
            Time_Flag=(i<Hour)||(i==Hour&&i1<Minute);
            Calendar calendar1=Calendar.getInstance();
            calendar1.set(Calendar.HOUR,(i+12)%24);
            calendar1.set(Calendar.MINUTE,i1);

            CharSequence charSequence= DateFormat.format("hh:mm a",calendar1);
            Food_time=(String)charSequence;
            Log.d(Mtag.Tag,"2->"+Food_time);

        },Hour,Minute,true);
        timePickerDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration_of_orderLeft!=null)
        listenerRegistration_of_orderLeft.remove();
    }

    private void setPermissionControl(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "Please Grant Location Permission", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,Flash.class));
            finish();
        }
    }

}