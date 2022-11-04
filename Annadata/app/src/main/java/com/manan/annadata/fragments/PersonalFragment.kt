package com.manan.annadata.fragments

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
import com.manan.annadata.foodcycle.MarketAdapter
import android.os.Bundle
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

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalFragment : Fragment() {
    var user: FirebaseUser? = null
    var FIRE_db: FirebaseFirestore? = null
    var query: Query? = null
    var recyclerView: RecyclerView? = null
    var marketAdapter: MarketAdapter? = null
    var v: View? = null


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_personal, container, false)
        recyclerView = v!!.findViewById(R.id.rcycle_fb_personal_order)
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
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
        query = FirebaseFirestore.getInstance()
            .collection(FireTag.FireMarket)
            .whereEqualTo("retailer_ID", user!!.uid)
        SetAdapter()
    }

    private fun SetAdapter() {
        val options = FirestoreRecyclerOptions.Builder<MARKET>()
            .setQuery(query!!, MARKET::class.java)
            .build()
        marketAdapter = context?.let { MarketAdapter(options, it) }
        marketAdapter!!.isPersonalUse = true
        recyclerView!!.adapter = marketAdapter
        marketAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (marketAdapter != null) {
            marketAdapter!!.stopListening()
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): PersonalFragment {
            val fragment = PersonalFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}