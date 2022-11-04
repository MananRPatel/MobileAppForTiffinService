package com.manan.annadata.activitys

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.MARKET
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag

class TiffinModify constructor() : AppCompatActivity() {
    var TIFFIN_DOC_ID: String? = null
    var t1: TextView? = null
    var t2: TextView? = null
    var t3: TextView? = null
    var t4: TextView? = null
    var t5: TextView? = null
    var t6: TextView? = null
    var allReview: TextView? = null
    var reviewPhoto: ImageButton? = null
    var acceptingOrder: Button? = null
    var notAcceptingOrder: Button? = null
    var setOrder: Button? = null
    var closeReview: Button? = null
    var FIRE_db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var backButton: ImageView? = null
    var foodImage: ImageView? = null
    var editText: EditText? = null
    var listenerRegistration_of_orderLeft: ListenerRegistration? = null
    var dialog: Dialog? = null
    var constraintLayout: ConstraintLayout? = null
    private fun IdSaver() {
        editText = findViewById(R.id.act_tm_neworder)
        t1 = findViewById(R.id.act_tm_foodname)
        t2 = findViewById(R.id.act_tm_foodweight)
        t3 = findViewById(R.id.act_tm_totalorder)
        t4 = findViewById(R.id.act_tm_sellerfoodinfo)
        t5 = findViewById(R.id.act_tm_deliverytime)
        t6 = findViewById(R.id.act_tm_avg_rating)
        reviewPhoto = findViewById(R.id.act_tm_ratingreview)
        constraintLayout = findViewById(R.id.act_tm_layout_rating)
        acceptingOrder = findViewById(R.id.act_tm_conform)
        notAcceptingOrder = findViewById(R.id.act_tm_cancel)
        setOrder = findViewById(R.id.act_tm_set_order)
        backButton = findViewById(R.id.act_tm_back_button)
        foodImage = findViewById(R.id.act_tm_foodphoto)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiffin_modify)
        val intent: Intent = getIntent()
        TIFFIN_DOC_ID = intent.getStringExtra("TIFFIN_DOC_ID")
        FIRE_db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().getCurrentUser()
        IdSaver()
        backButton!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })
        FIRE_db!!.collection(FireTag.FireMarket).document((TIFFIN_DOC_ID)!!)
            .get().addOnSuccessListener(OnSuccessListener({ documentSnapshot: DocumentSnapshot ->
                setDELIVERY(documentSnapshot)
            }))
    }

    private fun setDELIVERY(documentSnapshot: DocumentSnapshot) {
        val market: MARKET? = documentSnapshot.toObject(MARKET::class.java)
        val documentSnapshotReference: DocumentReference = documentSnapshot.getReference()
        assert(market != null)
        var s: String? =market!!.FOOD_NAME
        t1!!.setText(s)
        s = "Delivery Time : " + market.FOOD_TIME
        t5!!.setText(s)
        s = "No. Order  :  " + market.LEFT_ORDER
        OrderLeft(documentSnapshot)
        s = market.FOOD_WEIGHT.toString() + " KG"
        t2!!.setText(s)
        s = market.FOOD_INFO
        t4!!.setText(s)
        SetAvgRating(market.ALL_RATING, market.ALL_REVIEW)
        val isNoFoodLeft: Boolean = market.NO_FOOD_LEFT
        if (isNoFoodLeft) {
            notAcceptingOrder!!.setVisibility(View.INVISIBLE)
        } else {
            acceptingOrder!!.setVisibility(View.INVISIBLE)
        }
        if ((market.ALL_REVIEW) == null) {
            reviewPhoto!!.setVisibility(View.GONE)
        }
        reviewPhoto!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                SeeReview(market.ALL_REVIEW)
            }
        })
        acceptingOrder!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                if (market.LEFT_ORDER != 0) {
                    documentSnapshotReference.update("no_FOOD_LEFT", false)
                    acceptingOrder!!.setVisibility(View.INVISIBLE)
                    notAcceptingOrder!!.setVisibility(View.VISIBLE)
                } else {
                    Toast.makeText(
                        this@TiffinModify,
                        "Please increase Food Order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        notAcceptingOrder!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                documentSnapshotReference.update("no_FOOD_LEFT", true)
                acceptingOrder!!.setVisibility(View.VISIBLE)
                notAcceptingOrder!!.setVisibility(View.INVISIBLE)
            }
        })
        Glide.with(this)
            .load(market.PHOTO_URL)
            .into((foodImage)!!)
        setOrder!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                SetOrder(market.TOTAL_FOOD, market.LEFT_ORDER, documentSnapshotReference)
            }
        })
    }

    private fun SeeReview(allRatingReview: List<String>?) {
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dial_reviews)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.create()
        dialog!!.show()
        allReview = dialog!!.findViewById(R.id.dial_r_allreview)
        val review: StringBuilder = StringBuilder()
        for (r: String in allRatingReview!!) {
            review.append(r).append(" \n ")
        }
        allReview!!.setText(review.toString())
        closeReview = dialog!!.findViewById(R.id.dial_r_close)
        closeReview!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                dialog!!.cancel()
            }
        })
    }

    private fun OrderLeft(documentSnapshot: DocumentSnapshot) {
        listenerRegistration_of_orderLeft = documentSnapshot.getReference().addSnapshotListener(
            EventListener({ value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Toast.makeText(this@TiffinModify, error.message, Toast.LENGTH_SHORT).show()
                } else if (value == null) {
                    Toast.makeText(this@TiffinModify, "No Order Left", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.d(Mtag.Tag, "\n\n\n\nOrderLeft: " + value.get("left_ORDER") + "\n\n\n\n")
                    val s: String = value.get("left_ORDER").toString() + " Order Left"
                    t3!!.setText(s)
                }
            })
        )
    }

    private fun SetAvgRating(rating: List<Int>?, review: List<String>?) {
        var avg: Float = 0f
        if (review == null) {
            constraintLayout!!.setVisibility(View.INVISIBLE)
            return
        }
        //TODO
        for (rate: Int in rating!!) {
            avg += rate.toFloat()
        }
        avg /= rating.size.toFloat()
        val s: String = GetEmoji(Math.floor(avg.toDouble()).toInt()) + " < Rating < " + GetEmoji(
            Math.ceil(avg.toDouble()).toInt()
        )
        t6!!.setText(s)
    }

    private fun GetEmoji(value: Int): String {
        when (value) {
            1 -> return "\uD83D\uDE1F"
            2 -> return "\uD83D\uDE41"
            3 -> return "\uD83D\uDE10"
            4 -> return "\uD83D\uDE42"
            else -> return "\uD83D\uDE00"
        }
    }

    private fun SetOrder(totalOrder: Int, leftOrder: Int, documentReference: DocumentReference) {
        val newOrderString: String = editText!!.getText().toString()
        if (newOrderString.isEmpty()) {
            editText!!.requestFocus()
            editText!!.setError("Please Enter new order number")
        } else {
            val newOrder: Int = newOrderString.toInt()
            val map: MutableMap<String, Any> = HashMap()
            map.put("left_ORDER", newOrder)
            map.put("total_FOOD", (totalOrder + newOrder - leftOrder))
            documentReference.update(map).addOnSuccessListener(object : OnSuccessListener<Void?> {
                public override fun onSuccess(unused: Void?) {
                    Toast.makeText(
                        this@TiffinModify,
                        "Order updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        Toast.makeText(this@TiffinModify, "Order not updated!", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    override fun onStop() {
        super.onStop()
        if (listenerRegistration_of_orderLeft != null) listenerRegistration_of_orderLeft!!.remove()
    }
}