package com.manan.annadata.fragments

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
import com.manan.annadata.foodcycle.MarketAdapter
import android.os.Bundle
import android.util.Log
import com.manan.annadata.fragments.PersonalFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.manan.annadata.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.manan.annadata.idtag.FireTag
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.manan.annadata.dbhelpers.MARKET
import com.manan.annadata.idtag.Mtag

class BuyMarketFragment : Fragment() {
    var query: Query? = null
    var recyclerView: RecyclerView? = null
    var FIRE_db: FirebaseFirestore? = null
    var marketAdapter: MarketAdapter? = null
    var user: FirebaseUser? = null
    var v: View? = null
    private var SellerBannerId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) SellerBannerId = requireArguments().getString("TiffinLinkSellerId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_buy_market, container, false)

        recyclerView = v!!.findViewById(R.id.rcycle_fb_marketcycle)
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
        Log.d(Mtag.Tag, "onCreateView BuyFragment: 93 ")
        user = FirebaseAuth.getInstance().currentUser
        FIRE_db = FirebaseFirestore.getInstance()
        FIRE_db!!.collection(FireTag.FireUser)
            .document(user!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                SetQuery(
                    documentSnapshot["address"] as String?
                )
            }
        return v
    }

    private fun SetQuery(address: String?) {
        Log.d(Mtag.Tag, "SetQuery: 113 $address")
        query = if (SellerBannerId != null) {
            FirebaseFirestore.getInstance()
                .collection(FireTag.FireMarket)
                .whereEqualTo("retailer_ID", SellerBannerId)
        } else {
            FirebaseFirestore.getInstance()
                .collection(FireTag.FireMarket)
                .whereEqualTo("city_NAME", address)
                .whereNotEqualTo("retailer_ID", user!!.uid)
                .whereEqualTo("no_FOOD_LEFT", false)
        }
        SetAdapter()
    }

    private fun SetAdapter() {
        val options = FirestoreRecyclerOptions.Builder<MARKET>()
            .setQuery(query!!, MARKET::class.java)
            .build()
        Log.d(Mtag.Tag, "onCreateView BuyFragment: 128 ")
        marketAdapter = context?.let { MarketAdapter(options, it) }
        recyclerView!!.adapter = marketAdapter
        marketAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (marketAdapter != null) {
            marketAdapter!!.stopListening()
        }
    }
}