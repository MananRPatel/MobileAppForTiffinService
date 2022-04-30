package com.mananhirak.annadata.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {


    FirebaseUser user;
    FirebaseFirestore FIRE_db;

    Query query;
    RecyclerView recyclerView;
    MarketAdapter marketAdapter;
    View v;
    Context context;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment_personal, container, false);

        context =getContext();
        recyclerView=v.findViewById(R.id.rcycle_fb_personal_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

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

        query = FirebaseFirestore.getInstance()
                .collection(FireTag.FireMarket)
                .whereEqualTo("retailer_ID",user.getUid());
        SetAdapter();

    }

    private void SetAdapter(){
        FirestoreRecyclerOptions<MARKET> options= new FirestoreRecyclerOptions.Builder<MARKET>()
                .setQuery(query,MARKET.class)
                .build();

        marketAdapter=new MarketAdapter(options,context);
        marketAdapter.isPersonalUse=true;
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