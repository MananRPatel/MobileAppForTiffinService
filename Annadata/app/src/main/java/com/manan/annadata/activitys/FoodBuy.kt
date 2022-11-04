package com.manan.annadata.activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.manan.annadata.R
import com.manan.annadata.foodcycle.FRAGMENT_ADAPTER
import java.util.*

class FoodBuy constructor() : AppCompatActivity(), AuthStateListener {
    var ivBack: ImageView? = null
    var SellerIdBySharableLink: String? = null
    var tabLayout: TabLayout? = null
    var viewPager2: ViewPager2? = null
    var fragmentAdapter: FRAGMENT_ADAPTER? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_buy)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        IdSaver()
        ivBack!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })
    }

    public override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            firebaseAuth.currentUser?.let { MarketCycle(it) }
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

    private fun MarketCycle(user: FirebaseUser) {
        fragmentAdapter = FRAGMENT_ADAPTER(this)
        val get_intent: Intent = getIntent()
        SellerIdBySharableLink = get_intent.getStringExtra("SellerIDFromSharableLink")
        if (SellerIdBySharableLink != null) {
            fragmentAdapter!!.TiffinLinkSellerId = SellerIdBySharableLink
        }
        SetUpCards()
    }

    fun SetUpCards() {
        viewPager2!!.setAdapter(fragmentAdapter)
        TabLayoutMediator(
            (tabLayout)!!, (viewPager2)!!,
            object : TabConfigurationStrategy {
                public override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    if (position == 0) tab.setText("Market") else tab.setText("Personal")
                }
            }).attach()
    }

    private fun IdSaver() {
        ivBack = findViewById(R.id.ivBack)
        tabLayout = findViewById(R.id.act_fd_tablayout)
        viewPager2 = findViewById(R.id.act_fd_viewpager2)
    }
}