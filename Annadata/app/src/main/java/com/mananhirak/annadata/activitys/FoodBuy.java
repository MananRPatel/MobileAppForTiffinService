package com.mananhirak.annadata.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.foodcycle.FRAGMENT_ADAPTER;

import java.util.Objects;

public class FoodBuy extends AppCompatActivity implements FirebaseAuth.AuthStateListener {


    ImageView ivBack;
    String SellerIdBySharableLink;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FRAGMENT_ADAPTER fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_buy);
        Objects.requireNonNull(getSupportActionBar()).hide();

        IdSaver();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
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
    }

    private void MarketCycle(FirebaseUser user){

        fragmentAdapter = new FRAGMENT_ADAPTER(this);
        Intent get_intent=getIntent();
        SellerIdBySharableLink=get_intent.getStringExtra("SellerIDFromSharableLink");
        if(SellerIdBySharableLink!=null) {

            fragmentAdapter.TiffinLinkSellerId=SellerIdBySharableLink;
        }

        SetUpCards();

    }
    public  void SetUpCards(){

        viewPager2.setAdapter(fragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position==0) tab.setText("Market");
                        else tab.setText("Personal");
                    }
                }).attach();
    }

    private void IdSaver(){
        ivBack = findViewById(R.id.ivBack);
        tabLayout= findViewById(R.id.act_fd_tablayout);
        viewPager2=findViewById(R.id.act_fd_viewpager2);
    }

}