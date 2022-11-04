package com.manan.annadata.activitys

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.DELIVERY_BOY
import com.manan.annadata.foodcycle.DeliveryBoyAdapter
import com.manan.annadata.idtag.FireTag
import java.util.*

class FoodDelivery constructor() : AppCompatActivity(), AuthStateListener {
    var recyclerView: RecyclerView? = null
    var deliveryBoyAdapter: DeliveryBoyAdapter? = null
    var ivBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_delivery)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(R.color.food))
        }
        ivBack = findViewById(R.id.ivBack)
        ivBack?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })
        IdSaver()
    }

    public override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            firebaseAuth.getCurrentUser()?.let { MarketCycle(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        if (deliveryBoyAdapter != null) {
            deliveryBoyAdapter!!.stopListening()
        }
    }

    private fun MarketCycle(user: FirebaseUser) {
        val query: Query = FirebaseFirestore.getInstance()
            .collection(FireTag.FireUser)
            .document(user.getUid())
            .collection(FireTag.FireDelivery)
            .whereEqualTo("cancel_DELIVERY", false)
            .whereEqualTo("conform_DELIVERY", false)
        val options: FirestoreRecyclerOptions<DELIVERY_BOY> =
            FirestoreRecyclerOptions.Builder<DELIVERY_BOY>()
                .setQuery(query, DELIVERY_BOY::class.java)
                .build()
        deliveryBoyAdapter = DeliveryBoyAdapter(options, this)
        recyclerView!!.setAdapter(deliveryBoyAdapter)
        deliveryBoyAdapter!!.startListening()
    }

    private fun IdSaver() {
        recyclerView = findViewById(R.id.rcycle_fd_deliverycycle)
    }
}