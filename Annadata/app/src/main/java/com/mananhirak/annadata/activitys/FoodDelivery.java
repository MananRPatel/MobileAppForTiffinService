package com.mananhirak.annadata.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.foodcycle.DeliveryBoyAdapter;
import com.mananhirak.annadata.idtag.FireTag;

import java.util.Objects;

public class FoodDelivery extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    RecyclerView recyclerView;
    DeliveryBoyAdapter deliveryBoyAdapter;
    ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_delivery);
        Objects.requireNonNull(getSupportActionBar()).hide();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.food));
        }

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        IdSaver();


    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            MarketCycle(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
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
        if (deliveryBoyAdapter != null) {
            deliveryBoyAdapter.stopListening();
        }
    }

    private void MarketCycle(FirebaseUser user) {

        Query query = FirebaseFirestore.getInstance()
                .collection(FireTag.FireUser)
                .document(user.getUid())
                .collection(FireTag.FireDelivery)
                .whereEqualTo("cancel_DELIVERY",false)
                .whereEqualTo("conform_DELIVERY", false);

        FirestoreRecyclerOptions<DELIVERY_BOY> options = new FirestoreRecyclerOptions.Builder<DELIVERY_BOY>()
                .setQuery(query, DELIVERY_BOY.class)
                .build();

        deliveryBoyAdapter = new DeliveryBoyAdapter(options, this);
        recyclerView.setAdapter(deliveryBoyAdapter);
        deliveryBoyAdapter.startListening();
    }

    private void IdSaver() {
        recyclerView = findViewById(R.id.rcycle_fd_deliverycycle);
    }
}