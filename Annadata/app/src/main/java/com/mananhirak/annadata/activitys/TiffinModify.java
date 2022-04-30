package com.mananhirak.annadata.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TiffinModify extends AppCompatActivity {

    String TIFFIN_DOC_ID;
    TextView t1,t2,t3,t4,t5,t6,allReview;
    ImageButton reviewPhoto;
    Button acceptingOrder, notAcceptingOrder,setOrder,closeReview;
    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    ImageView backButton,foodImage;
    EditText editText;
    ListenerRegistration listenerRegistration_of_orderLeft;
    Dialog dialog;
    ConstraintLayout constraintLayout;
    private void IdSaver() {

        editText =findViewById(R.id.act_tm_neworder);

        t1=findViewById(R.id.act_tm_foodname);
        t2=findViewById(R.id.act_tm_foodweight);
        t3=findViewById(R.id.act_tm_totalorder);
        t4=findViewById(R.id.act_tm_sellerfoodinfo);
        t5=findViewById(R.id.act_tm_deliverytime);
        t6=findViewById(R.id.act_tm_avg_rating);
        reviewPhoto=findViewById(R.id.act_tm_ratingreview);
        constraintLayout =findViewById(R.id.act_tm_layout_rating);

        acceptingOrder = findViewById(R.id.act_tm_conform);
        notAcceptingOrder = findViewById(R.id.act_tm_cancel);
        setOrder = findViewById(R.id.act_tm_set_order);

        backButton = findViewById(R.id.act_tm_back_button);

        foodImage = findViewById(R.id.act_tm_foodphoto);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiffin_modify);

        Intent intent=getIntent();
        TIFFIN_DOC_ID=intent.getStringExtra("TIFFIN_DOC_ID");

        FIRE_db=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        IdSaver();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FIRE_db.collection(FireTag.FireMarket).document(TIFFIN_DOC_ID)
                .get().addOnSuccessListener(this::setDELIVERY);

    }

    private void setDELIVERY(DocumentSnapshot documentSnapshot) {

        MARKET market = documentSnapshot.toObject(MARKET.class);
        DocumentReference documentSnapshotReference = documentSnapshot.getReference();
        assert market != null;
        String s = "Food : " + market.getFOOD_NAME();
        t1.setText(s);
        s = "Delivery Time : " + market.getFOOD_TIME();
        t5.setText(s);
        s = "No. Order  :  " + market.getLEFT_ORDER();
        OrderLeft(documentSnapshot);
        s = market.getFOOD_WEIGHT() + " KG";
        t2.setText(s);
        s="Food Info : "+market.getFOOD_INFO();
        t4.setText(s);

        SetAvgRating(market.getALL_RATING(),market.getALL_REVIEW());

        boolean isNoFoodLeft =market.getNO_FOOD_LEFT();

        if(isNoFoodLeft){
            notAcceptingOrder.setVisibility(View.INVISIBLE);
        }else{
            acceptingOrder.setVisibility(View.INVISIBLE);
        }

        if((market.getALL_REVIEW())==null){
            reviewPhoto.setVisibility(View.GONE);
        }

        reviewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeeReview(market.getALL_REVIEW());
            }
        });

        acceptingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(market.getLEFT_ORDER()!=0) {
                    documentSnapshotReference.update("no_FOOD_LEFT", false);
                    acceptingOrder.setVisibility(View.INVISIBLE);
                    notAcceptingOrder.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(TiffinModify.this, "Please increase Food Order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        notAcceptingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentSnapshotReference.update("no_FOOD_LEFT", true);
                acceptingOrder.setVisibility(View.VISIBLE);
                notAcceptingOrder.setVisibility(View.INVISIBLE);
            }
        });

        Glide.with(this)
                .load(market.getPHOTO_URL())
                .into(foodImage);

        setOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetOrder(market.getTOTAL_FOOD(),market.getLEFT_ORDER(),documentSnapshotReference);
            }
        });

    }

    private void SeeReview(List<String> allRatingReview){



        dialog =new Dialog(this);
        dialog.setContentView(R.layout.dial_reviews);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();

        allReview=dialog.findViewById(R.id.dial_r_allreview);

        StringBuilder review= new StringBuilder();

        for(String r : allRatingReview ){
            review.append(r).append(" \n ");
        }

        allReview.setText(review.toString());

        closeReview=dialog.findViewById(R.id.dial_r_close);

        closeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    private void OrderLeft(DocumentSnapshot documentSnapshot) {
        listenerRegistration_of_orderLeft = documentSnapshot.getReference().addSnapshotListener((value, error) -> {
            if(error!=null){
                Toast.makeText(TiffinModify.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }else if(value==null){
                Toast.makeText(TiffinModify.this, "No Order Left", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Log.d(Mtag.Tag, "\n\n\n\nOrderLeft: "+value.get("left_ORDER")+"\n\n\n\n");
                String s=value.get("left_ORDER")+" Order Left";
                t3.setText(s);
            }

        });
    }

    private void SetAvgRating(List<Integer> rating,List<String> review){
        float avg=0;
        if(review==null){
            constraintLayout.setVisibility(View.INVISIBLE);
            return;
        }
        //TODO
        for (int rate:rating) {
            avg+=rate;
        }
        avg/=rating.size();
        String s =GetEmoji((int) Math.floor(avg))+" < Rating < "+GetEmoji((int)Math.ceil(avg));
        t6.setText(s);

    }

    private String GetEmoji(int value){
        switch (value){
            case 1:
                return "\uD83D\uDE1F";
            case 2:
                return "\uD83D\uDE41";
            case 3:
                return "\uD83D\uDE10";
            case 4:
                return "\uD83D\uDE42";
            default:
                return "\uD83D\uDE00";


        }
    }

    private  void SetOrder(int totalOrder,int leftOrder,DocumentReference documentReference){
       String newOrderString = editText.getText().toString();
        if(newOrderString.isEmpty()){
            editText.requestFocus();
            editText.setError("Please Enter new order number");
        }else{
           int newOrder = Integer.parseInt(newOrderString);
            Map<String,Object> map = new HashMap<>();
            map.put("left_ORDER",newOrder);
            map.put("total_FOOD",(totalOrder+newOrder-leftOrder));
            documentReference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void unused) {
                   Toast.makeText(TiffinModify.this, "Order updated Successfully", Toast.LENGTH_SHORT).show();
               }
           })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TiffinModify.this, "Order not updated!", Toast.LENGTH_SHORT).show();
                }
            });
           }

        }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration_of_orderLeft!=null)
            listenerRegistration_of_orderLeft.remove();
    }


}