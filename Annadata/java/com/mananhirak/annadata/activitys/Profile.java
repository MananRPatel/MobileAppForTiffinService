package com.mananhirak.annadata.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.foodcycle.HistoryAdapter;
import com.mananhirak.annadata.idtag.FireTag;
import java.util.List;
import java.util.Objects;

public class Profile extends AppCompatActivity implements FirebaseAuth.AuthStateListener{

    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    TextView t1,t2,t3;
    HistoryAdapter historyAdapter;
    RecyclerView recyclerView;
    ImageView imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();

        recyclerView=findViewById(R.id.act_pro_hcycle);

        FIRE_db=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();


        t1=findViewById(R.id.procartmoney);
        t2=findViewById(R.id.prodonate);
        t3=findViewById(R.id.prouser);

        button = findViewById(R.id.prologoutb);

        imageView=findViewById(R.id.act_p_userpic);

     FIRE_db.collection(FireTag.FireUser)
             .document(user.getUid())
             .get()
             .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                 @Override
                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                     Glide.with(Profile.this)
                             .load(documentSnapshot.get("photo_URL"))
                             .into(imageView);
                 }
             });


        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireDelivery)
                .whereEqualTo("conform_DELIVERY",true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                int TotalMoney=0;
                int TotalPoint=0;
                for (DocumentSnapshot snap:snapshotList){
                    DELIVERY_BOY deliveryBoy=snap.toObject(DELIVERY_BOY.class);
                    TotalMoney+=( deliveryBoy.getFOOD_MONEY() ) * ( deliveryBoy.getTOTAL_ORDER() );
                    TotalPoint+=( deliveryBoy.getFOOD_POINT() ) * ( deliveryBoy.getTOTAL_ORDER() );
                }

                String s1="₹"+ TotalMoney;
                t1.setText(s1);

                String s2=""+TotalPoint;
                t2.setText(s2);

            }
        });

        t3.setText(user.getDisplayName());


      /*   ImageView i=findViewById(R.id.prohistorybuttton);
       i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Profile.this,UserHistory.class);
                startActivity(intent);
            }
        });*/


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(Profile.this);
            }
        });

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            Intent intent = new Intent(this,  Flash.class);
            startActivity(intent);
            finish();
        }else{
            HistoryCycle(firebaseAuth.getCurrentUser());
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
        if(historyAdapter!=null){
            historyAdapter.stopListening();
        }
    }

    private void HistoryCycle(FirebaseUser user){

        Query query= FirebaseFirestore.getInstance()
                .collection(FireTag.FireUser)
                .document(user.getUid())
                .collection(FireTag.FireHistory);


        FirestoreRecyclerOptions<BUY_HISTORY> options= new FirestoreRecyclerOptions.Builder<BUY_HISTORY>()
                .setQuery(query, BUY_HISTORY.class)
                .build();

        historyAdapter=new HistoryAdapter(options);
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.startListening();
    }


}