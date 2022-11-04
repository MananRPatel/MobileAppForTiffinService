package com.manan.annadata.activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.manan.annadata.R
import java.util.*

class MainActivity constructor() : AppCompatActivity(), AuthStateListener {
    private var c1: CardView? = null
    private var c2: CardView? = null
    private var c3: CardView? = null
    private var c4: CardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        IdSaver()
        c1!!.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(this@MainActivity, Profile::class.java)
            startActivity(intent)
        })
        c2!!.setOnClickListener(View.OnClickListener { view: View? ->
            val intent: Intent = Intent(this@MainActivity, FoodSell::class.java)
            startActivity(intent)
        })
        c3!!.setOnClickListener { view: View? ->
            val intent = Intent(this@MainActivity, FoodBuy::class.java)
            startActivity(intent)
        }
        c4!!.setOnClickListener(View.OnClickListener { view: View? ->
            val intent: Intent = Intent(this@MainActivity, FoodDelivery::class.java)
            startActivity(intent)
        })
    }

    public override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            val intent: Intent = Intent(this, Flash::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    private fun IdSaver() {
        c1 = findViewById(R.id.mainprofile)
        c2 = findViewById(R.id.mainseller)
        c3 = findViewById(R.id.mainbuyer)
        c4 = findViewById(R.id.maindelivery)
    }
}