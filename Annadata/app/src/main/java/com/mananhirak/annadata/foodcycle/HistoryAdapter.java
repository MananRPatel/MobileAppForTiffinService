package com.mananhirak.annadata.foodcycle;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.idtag.FireTag;
import java.util.HashMap;
import java.util.Map;

public class HistoryAdapter extends FirestoreRecyclerAdapter<BUY_HISTORY, HistoryAdapter.HistoryHolder> {

    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<BUY_HISTORY> options,Context context,FirebaseFirestore FIRE_db) {
        super(options);
        this.context=context;
        this.FIRE_db=FIRE_db;
    }

        public  Context context;
        public FirebaseFirestore FIRE_db;
    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder holder, int position, @NonNull BUY_HISTORY model) {

        holder.constraintLayout.setVisibility(View.GONE);

        if(model.getRATING_FOOD()==0){
            holder.rating_emoji.setVisibility(View.INVISIBLE);
            holder.order_rating.setVisibility(View.VISIBLE);
        }else{
            holder.order_rating.setVisibility(View.INVISIBLE);
            holder.rating_emoji.setVisibility(View.VISIBLE);
            switch (model.getRATING_FOOD()){
                case 1:
                    holder.rating_emoji.setText("\uD83D\uDE1F");
                    break;
                case 2:
                    holder.rating_emoji.setText("\uD83D\uDE41");
                    break;
                case 3:
                    holder.rating_emoji.setText("\uD83D\uDE10");
                    break;
                case 4:
                    holder.rating_emoji.setText("\uD83D\uDE42");
                    break;
                case 5:
                    holder.rating_emoji.setText("\uD83D\uDE00");
                    break;
            }
        }


        FIRE_db.collection(FireTag.FireUser)
                .document(model.getRETAILER_ID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String s="Retailer : "+documentSnapshot.get("user_NAME");
                        holder.t1.setText(s);
                    }
                });


        String s;
        s="Food : "+model.getFOOD_NAME();
        holder.t2.setText(s);
        s="Weight : "+model.getFOOD_WEIGHT();
        holder.t3.setText(s);
        s="Order : "+model.getTOTAL_FOOD();
        holder.t4.setText(s);
        s="Food Info : "+model.getFOOD_INFO();
        holder.t5.setText(s);
        if((model.getFOOD_PRICE())==0) s="For Donate Purpose";
        else s="Food price : "+model.getFOOD_PRICE();
        holder.t6.setText(s);
        if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_PLACED) {

            holder.order_s.setTextColor(context.getColor(R.color.ORDER_PLACED));
            holder.order_rating.setVisibility(View.INVISIBLE);
            holder.rating_emoji.setVisibility(View.INVISIBLE);

         //   holder.order_s_t.setText("Order Placed");

        } else if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_CONFORM) {

            holder.order_s.setTextColor(context.getColor(R.color.ORDER_CONFORM));
        //    holder.order_s_t.setText("Order Conformed");

        } else if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_CANCEL) {

            holder.order_s.setTextColor(context.getColor(R.color.ORDER_CANCEL));
       //     holder.order_s_t.setText("Order Canceled");

        }


        holder.itemView.setOnClickListener(view -> {
            int v= holder.constraintLayout.getVisibility()==View.GONE? View.VISIBLE:View.GONE;
            holder.constraintLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

            TransitionManager.beginDelayedTransition(holder.constraintLayout,new AutoTransition());
            holder.constraintLayout.setVisibility(v);
        });


        holder.order_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_PLACED) Toast.makeText(context, "Order is placed", Toast.LENGTH_SHORT).show();
                else if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_CONFORM) Toast.makeText(context, "Order is confirmed", Toast.LENGTH_SHORT).show();
                else if(model.getORDER_STATUS()==BUY_HISTORY.ORDER_CANCEL) Toast.makeText(context, "Order is canceled", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.card_food_history,parent,false);
        return new HistoryHolder(view);
    }

    public class HistoryHolder extends RecyclerView.ViewHolder{

        public TextView t1,t2,t3,t4,t5,t6, order_s,rating_emoji;//,order_s_t;
        public ImageButton order_rating;
        public ConstraintLayout constraintLayout;
        private int ratingValue=0;
        private TextInputLayout queryLayout;
        private TextInputEditText query;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            t1=itemView.findViewById(R.id.card_fh_retailer);
            t2=itemView.findViewById(R.id.card_fh_foodname);
            t3=itemView.findViewById(R.id.card_fh_foodweight);
            t4=itemView.findViewById(R.id.card_fh_ordernumber);
            t5=itemView.findViewById(R.id.card_fh_foodinfo);
            t6=itemView.findViewById(R.id.card_fh_foodvalue);

            rating_emoji=itemView.findViewById(R.id.card_fh_rating);

           // order_s_t=itemView.findViewById(R.id.card_fh_order_status_text);
            order_s =itemView.findViewById(R.id.card_fh_order_status);
            constraintLayout=itemView.findViewById(R.id.expandcard_his);
            order_rating=itemView.findViewById(R.id.card_fh_ratingbar);


            order_rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dialog dialog =new Dialog(context);
                    dialog.setContentView(R.layout.dial_rating_bar);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.create();
                    dialog.show();
                    Button submit,cancel;

                    submit = dialog.findViewById(R.id.dial_rb_submit);
                    cancel = dialog.findViewById(R.id.dial_rb_cancel);

                    TextView rate_1 = dialog.findViewById(R.id.dial_rb_1);
                    TextView rate_2 = dialog.findViewById(R.id.dial_rb_2);
                    TextView rate_3 = dialog.findViewById(R.id.dial_rb_3);
                    TextView rate_4 = dialog.findViewById(R.id.dial_rb_4);
                    TextView rate_5 = dialog.findViewById(R.id.dial_rb_5);

                    query = dialog.findViewById(R.id.dial_rb_query);
                    queryLayout = dialog.findViewById(R.id.dial_rb_querylayout);

                    rate_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratingValue =1;
                            queryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    rate_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratingValue =2;
                            queryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    rate_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratingValue =3;
                            queryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    rate_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratingValue =4;
                            queryLayout.setVisibility(View.GONE);
                        }
                    });
                    rate_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratingValue =5;
                            queryLayout.setVisibility(View.GONE);
                        }
                    });

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (ratingValue == 0)
                                Toast.makeText(context, "Please Select Rating", Toast.LENGTH_SHORT).show();
                            else {

                                DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());

                                String tiffinId = (String)documentSnapshot.get("food_ID");


                                documentSnapshot.getReference().update("rating_FOOD", ratingValue)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Map<String,Object> map = new HashMap<>();
                                                map.put("all_RATING", FieldValue.arrayUnion(ratingValue));
                                                if(query.getText()!=null){
                                                    map.put("all_REVIEW", FieldValue.arrayUnion(GetEmoji(ratingValue)+" -> "+query.getText().toString()));
                                                }

                                                FIRE_db.collection(FireTag.FireMarket)
                                                        .document(tiffinId)
                                                        .update(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.cancel();
                                                            }
                                                        });


                                            }
                                        });
                                }
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });

                }
            });




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


    }

}
