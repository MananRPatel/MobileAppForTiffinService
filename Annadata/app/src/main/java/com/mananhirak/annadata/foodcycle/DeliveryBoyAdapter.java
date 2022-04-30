package com.mananhirak.annadata.foodcycle;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activitys.DeliveryConform;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

public class DeliveryBoyAdapter extends FirestoreRecyclerAdapter<DELIVERY_BOY, DeliveryBoyAdapter.DeliveryHolder> {


    public Context context;
    FirebaseFirestore FIRE_db;

    public DeliveryBoyAdapter(@NonNull FirestoreRecyclerOptions<DELIVERY_BOY> options, Context context) {
        super(options);
        this.context=context;
        this.FIRE_db= FirebaseFirestore.getInstance();
    }


    @Override
    protected void onBindViewHolder(@NonNull DeliveryHolder holder, int position, @NonNull DELIVERY_BOY model) {

        String s;

        s="Food Name : "+model.getFOOD_NAME();
        holder.t1.setText(s);

        FIRE_db.collection(FireTag.FireUser)
                .document(model.getBUYER_ID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String s1="Buyer : "+documentSnapshot.get("user_NAME");
                        holder.t2.setText(s1);
                    }
                });

        s="Order Took : "+model.getTOTAL_ORDER();
        holder.t4.setText(s);
        s="Order Time : "+model.getDELIVERY_TIME();
        holder.t5.setText(s);

    }

    @NonNull
    @Override
    public DeliveryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.card_food_delivery,parent,false);
        return new DeliveryBoyAdapter.DeliveryHolder(view);
    }

    public class DeliveryHolder extends RecyclerView.ViewHolder{

        TextView t1,t2,t4,t5;

        public DeliveryHolder(@NonNull View itemView) {
            super(itemView);

            t1=itemView.findViewById(R.id.card_fd_foodname);
            t2=itemView.findViewById(R.id.card_fd_buyername);
            t4=itemView.findViewById(R.id.card_fd_ordertake);
            t5=itemView.findViewById(R.id.card_fd_ordertime);

            itemView.setOnClickListener(view -> {
                Log.d(Mtag.Tag,"DD"+getAdapterPosition());

                DocumentSnapshot documentSnapshot=getSnapshots().getSnapshot(getAdapterPosition());
                String str=documentSnapshot.getId();
                Log.d(Mtag.Tag,"AA"+str);
                Intent intent=new Intent(context, DeliveryConform.class);
                intent.putExtra("DELIVERY_DOC_ID",str);
                context.startActivity(intent);
            });


        }
    }


}
