package com.mananhirak.annadata.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.idtag.FireTag;

import java.util.Objects;

public class DeliveryConform extends AppCompatActivity {

    String DELIVERY_BOY_ID;
    TextView t1,t2,t3,t4,t5,t6,t7;
    FirebaseFirestore FIRE_db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_conform);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent=getIntent();
        DELIVERY_BOY_ID=intent.getStringExtra("DELIVERY_BOY_ID");

        FIRE_db=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();


        t1=findViewById(R.id.adcfoodname);
        t2=findViewById(R.id.adcfoodweight);
        t3=findViewById(R.id.adctotalorder);
        t4=findViewById(R.id.adcsellerfoodinfo);
        t5=findViewById(R.id.adcdelivertime);
        t6=findViewById(R.id.adcbuyername);
        t7=findViewById(R.id.adcbuyeraddress);

        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireDelivery).document(DELIVERY_BOY_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setDELIVERY(documentSnapshot);
                    }
                });

    }

     void setDELIVERY(DocumentSnapshot documentSnapshot) {

         DELIVERY_BOY deliveryBoy=documentSnapshot.toObject(DELIVERY_BOY.class);

        String s="Food : "+deliveryBoy.getFOOD_NAME();
        t1.setText(s);

         String s1="Buyer : "+deliveryBoy.getBUYER_NAME();
         t6.setText(s1);

         String s3="Delivery Time : "+deliveryBoy.getDELIVERY_TIME();
         t5.setText(s3);

         String s4="Order no.  :  "+deliveryBoy.getTOTAL_ORDER();
         t3.setText(s4);

         String s5="To : "+deliveryBoy.getDELIVERY_ADDRESS();
         t7.setText(s5);

         FIRE_db.collection(FireTag.FireMarket).document(deliveryBoy.getFOOD_ID()).get()
                 .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                         String s6="Food Info: "+documentSnapshot.get("food_INFO");
                         t4.setText(s6);

                         String s7= "Weight: "+documentSnapshot.get("food_WEIGHT")+" KG";
                         t2.setText(s7);

                     }
                 });

         DELIVERY_CONFORM(DELIVERY_BOY_ID);


    }

     void DELIVERY_CONFORM(final String DeliveryBoyId) {

         Button button=findViewById(R.id.adcconformbutton);

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                         .collection(FireTag.FireDelivery).document(DeliveryBoyId).
                         update("conform_DELIVERY",true);
                 finish();
             }
         });

    }

}