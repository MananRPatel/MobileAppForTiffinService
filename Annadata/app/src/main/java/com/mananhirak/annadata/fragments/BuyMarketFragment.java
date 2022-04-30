package com.mananhirak.annadata.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.foodcycle.MarketAdapter;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;


public class BuyMarketFragment extends Fragment {

    private Context context;
    Query query;
    RecyclerView recyclerView;
    FirebaseFirestore FIRE_db;
    MarketAdapter marketAdapter;
    FirebaseUser user;
    View v;
    private String SellerBannerId;

    public BuyMarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            SellerBannerId = getArguments().getString("TiffinLinkSellerId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.fragment_buy_market, container, false);

        context =getContext();
        recyclerView=v.findViewById(R.id.rcycle_fb_marketcycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Log.d(Mtag.Tag, "onCreateView BuyFragment: 93 ");

        user = FirebaseAuth.getInstance().getCurrentUser();
        FIRE_db=FirebaseFirestore.getInstance();

        FIRE_db.collection(FireTag.FireUser)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        SetQuery((String)documentSnapshot.get("address"));
                    }
                });

        return v;
    }

    private  void SetQuery(String address){

        Log.d(Mtag.Tag, "SetQuery: 113 "+address);


        if(SellerBannerId!=null){
            query = FirebaseFirestore.getInstance()
                    .collection(FireTag.FireMarket)
                    .whereEqualTo("retailer_ID",SellerBannerId);
        }else{
            query = FirebaseFirestore.getInstance()
                    .collection(FireTag.FireMarket)
                    .whereEqualTo("city_NAME",address)
                    .whereNotEqualTo("retailer_ID",user.getUid())
                    .whereEqualTo("no_FOOD_LEFT",false);

        }
        SetAdapter();

    }

    private void SetAdapter(){
        FirestoreRecyclerOptions<MARKET> options= new FirestoreRecyclerOptions.Builder<MARKET>()
                .setQuery(query,MARKET.class)
                .build();
        Log.d(Mtag.Tag, "onCreateView BuyFragment: 128 ");

        marketAdapter=new MarketAdapter(options,context);
        recyclerView.setAdapter(marketAdapter);
        marketAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(marketAdapter!=null){
            marketAdapter.stopListening();
        }
    }
}