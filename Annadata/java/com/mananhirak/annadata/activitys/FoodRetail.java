package com.mananhirak.annadata.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.mananhirak.annadata.R;
import java.util.Objects;

public class FoodRetail extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_retail);
        Objects.requireNonNull(getSupportActionBar()).hide();

        CardView c1=findViewById(R.id.rsellcard);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FoodRetail.this,FoodSell.class);
                startActivity(intent);
                finish();
            }
        });

        CardView c2=findViewById(R.id.rdonatecard);
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FoodRetail.this,FoodDonate.class);
                startActivity(intent);
                finish();
            }
        });
    }



}