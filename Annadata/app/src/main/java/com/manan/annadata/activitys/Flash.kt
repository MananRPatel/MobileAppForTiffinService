package com.manan.annadata.activitys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.FOOD_USERS
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import java.util.*

class Flash : AppCompatActivity() {
    var LOCATION_PERMISSION_CODE = 44
    var checkToken = false
    var SellerId: String? = null
    var ProductId: String? = null

    // hirak
    var textView: TextView? = null
    var charSequence: CharSequence? = null
    var index = 0
    var delay: Long = 200
    var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)
        Objects.requireNonNull(supportActionBar)?.hide()


        // hirak
        textView = findViewById(R.id.textView)
        animateText("Annadata")
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                checkPermissionIsGivenOrNot()
            }
        }, 900)
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {
            textView!!.text = charSequence!!.subSequence(0, index++)
            if (index <= charSequence!!.length) {
                handler.postDelayed(this, delay)
            }
        }
    }

    fun animateText(cs: CharSequence?) {
        charSequence = cs
        index = 0
        textView!!.text = ""
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, delay)
    }

    private fun SECURITY_CHECK() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseFirestore.getInstance().collection(FireTag.FireUser)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().currentUser)!!.uid)
                .get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    val foodUsers = documentSnapshot.toObject(
                        FOOD_USERS::class.java
                    )
                    if (foodUsers == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else if (foodUsers.PHOTO_URL == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else if (foodUsers.ADDRESS == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else if (foodUsers.USER_LOCATION == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else if (foodUsers.USER_NAME == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else if (foodUsers.ID == null) {
                        intent = Intent(this@Flash, AdditionInfo::class.java)
                    } else {
                        intent = Intent(this@Flash, MainActivity::class.java)
                        FirebaseDynamicLinks.getInstance()
                            .getDynamicLink(getIntent())
                            .addOnSuccessListener(
                                this,
                                OnSuccessListener { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
                                    var deepLink: Uri? = null
                                    if (pendingDynamicLinkData != null) {
                                        deepLink = pendingDynamicLinkData.link
                                        if (deepLink != null) {
                                            SellerId = deepLink.getQueryParameter("sellerid")
                                            ProductId = deepLink.getQueryParameter("productid")
                                            if (SellerId != null) {
                                                Log.d(Mtag.Tag, "onSu96ccess: $SellerId")
                                                val intent1 =
                                                    Intent(this@Flash, FoodBuy::class.java)
                                                intent1.putExtra(
                                                    "SellerIDFromSharableLink",
                                                    SellerId
                                                )
                                                startActivity(intent1)
                                                finish()
                                            } else if (ProductId != null) {
                                                Log.d(Mtag.Tag, "onSu96ccess: $ProductId")
                                                val intent1 =
                                                    Intent(this@Flash, SellConform::class.java)
                                                intent1.putExtra("FIRE_STORE_DOCUMENT", ProductId)
                                                startActivity(intent1)
                                                finish()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(this, object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    Log.w(Mtag.Tag, "getDynamicLink:onFailure", e)
                                }
                            })
                    }
                    startActivity(intent)
                    finish()
                }
        } else {
            val provider = Arrays.asList(
                EmailBuilder().build() /*  , new AuthUI.IdpConfig.PhoneBuilder()
                    .setDefaultCountryIso("IN")
                    .build()*/
            )
            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build()
            startActivityForResult(intent, 7144)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val user = FirebaseAuth.getInstance().currentUser
        if (requestCode == 7144) {
            when (resultCode) {
                RESULT_OK -> {
                    assert(user != null)
                    Log.d(Mtag.Tag, "onActivityResult: " + user!!.displayName)
                    if (Objects.requireNonNull(user.metadata)?.creationTimestamp == user.metadata!!
                            .lastSignInTimestamp
                    ) {
                        val intent = Intent(this, AdditionInfo::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        FirebaseFirestore.getInstance()
                            .collection(FireTag.FireUser)
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot["fcm_TOKEN"] == null) {
                                    Log.d(Mtag.Tag, "SECURITY_CHECK: 656gfhf")
                                    SetFCMToken(documentSnapshot)
                                } else if (CheckOldFCM(documentSnapshot["fcm_TOKEN"] as String?)) {
                                    Log.d(Mtag.Tag, "SECURITY_CHECK:dfdfdfdfdf 656gfhf")
                                    SetFCMToken(documentSnapshot)
                                } else {
                                    val intent = Intent(this@Flash, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }
                }
                RESULT_CANCELED -> {
                    Toast.makeText(this, "Sign-In must Required ", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
    }

    private fun SetFCMToken(documentSnapshot: DocumentSnapshot) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { s: String? ->
                documentSnapshot.reference.update("fcm_TOKEN", s)
                    .addOnSuccessListener({ unused: Void? ->
                        intent = Intent(this@Flash, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
            }
    }

    fun CheckOldFCM(token: String?): Boolean {
        checkToken = true
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { s: String -> checkToken = token != s }
        return checkToken
    }

    fun checkPermissionIsGivenOrNot() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else SECURITY_CHECK()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SECURITY_CHECK()
            } else {
                Toast.makeText(this, "Without Permission can't Use App", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}