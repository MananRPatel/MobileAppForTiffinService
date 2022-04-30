package com.mananhirak.annadata.foodcycle;


import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activitys.SellConform;
import com.mananhirak.annadata.activitys.TiffinModify;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.idtag.Mtag;

public class MarketAdapter extends FirestoreRecyclerAdapter<MARKET,MarketAdapter.MarketHolder> {

    public Context context;
    public boolean isPersonalUse;

    public MarketAdapter(@NonNull FirestoreRecyclerOptions<MARKET> options, Context context) {
        super(options);
        this.context=context;
        this.isPersonalUse=false;

    }


    @Override
    protected void onBindViewHolder(@NonNull MarketHolder holder, int position, @NonNull MARKET model) {

        String s;
        holder.constraintLayout.setVisibility(View.GONE);

        s="Food Name : "+model.getFOOD_NAME();
        holder.t1.setText(s);
        if((model.getFOOD_PRICE())==0)
            s="For Donate Purpose";
        else
            s="Food price : "+model.getFOOD_PRICE();

        holder.t2.setText(s);
        s="Food Time : "+model.getFOOD_TIME();
        holder.t3.setText(s);
        s="Food Date : "+model.getFOOD_DATE();
        holder.t4.setText(s);
        s="Weight : "+model.getFOOD_WEIGHT();
        holder.t5.setText(s);
        s="Order Left : "+model.getLEFT_ORDER();
        holder.t6.setText(s);
        s="Food info : "+model.getFOOD_INFO();
        holder.t7.setText(s);

        Glide.with(context)
                .load(model.getPHOTO_URL())
                .into(holder.i);


        holder.shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, model.getTIFFIN_LINK());
                sendIntent.setType("text/plain");
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(sendIntent, "Hello World"));

            }
        });



    }

    @NonNull
    @Override
    public MarketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.card_food_sell,parent,false);
        return new MarketHolder(view);
    }

    public  class MarketHolder extends RecyclerView.ViewHolder{

        public TextView t1,t2,t3,t4,t5,t6,t7;
        public ImageButton shareLink;
        public ImageView i,i1,i2;
        public Dialog dialog;
        public ConstraintLayout constraintLayout;


        public MarketHolder(@NonNull View itemView) {
            super(itemView);
            t1=itemView.findViewById(R.id.card_fs_food);
            t2=itemView.findViewById(R.id.card_fs_value);
            t3=itemView.findViewById(R.id.card_fs_time);
            t4=itemView.findViewById(R.id.card_fs_fooddate);
            t5=itemView.findViewById(R.id.card_fs_weight);
            t6=itemView.findViewById(R.id.card_fs_foodleft);
            t7=itemView.findViewById(R.id.card_fs_foodinfo);

            shareLink = itemView.findViewById(R.id.card_fs_sharelink);

            constraintLayout=itemView.findViewById(R.id.card_fs_extend);
            constraintLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


            i=itemView.findViewById(R.id.card_fs_foodphoto);

            i.setOnClickListener(view -> {
                Log.d(Mtag.Tag, "onClick: "+getAdapterPosition());
                ShowFoodPic(getSnapshots().getSnapshot(getAdapterPosition()));
            });

            itemView.setOnLongClickListener(view -> {
                TransitionManager.beginDelayedTransition(constraintLayout,new AutoTransition());
                int v=constraintLayout.getVisibility()==View.GONE? View.VISIBLE:View.GONE;
                constraintLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.card_bg2));
                constraintLayout.setVisibility(v);
                return true;
            });


            itemView.setOnClickListener(view -> {
                Log.d(Mtag.Tag,"DD"+getAdapterPosition());

                DocumentSnapshot documentSnapshot=getSnapshots().getSnapshot(getAdapterPosition());
                String str=documentSnapshot.getId();
                Log.d(Mtag.Tag,"DD"+str);
                Intent intent;
                if(isPersonalUse){
                    intent = new Intent(context, TiffinModify.class);
                    intent.putExtra("TIFFIN_DOC_ID",str);
                }else{
                    intent = new Intent(context, SellConform.class);
                    intent.putExtra("FIRE_STORE_DOCUMENT",str);
                }
                context.startActivity(intent);

            });

        }

        private void ShowFoodPic(final DocumentSnapshot snapshot) {

            dialog =new Dialog(context);
            dialog.setContentView(R.layout.dial_foodphoto);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.create();
            dialog.show();

            i1=dialog.findViewById(R.id.dial_fp_cancel);
            i1.setOnClickListener(view -> dialog.dismiss());

            i2=dialog.findViewById(R.id.dial_fp_foodpic);

            Glide.with(context)
                    .load(snapshot.get("photo_URL"))
                    .into(i2);



        }
    }

}
