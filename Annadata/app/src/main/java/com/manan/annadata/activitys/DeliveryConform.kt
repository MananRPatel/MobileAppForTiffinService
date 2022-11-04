package com.manan.annadata.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.BUY_HISTORY
import com.manan.annadata.dbhelpers.DELIVERY_BOY
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import com.manan.annadata.notify.SendNotify
import java.util.*

class DeliveryConform constructor() : AppCompatActivity(), OnMapReadyCallback {
    var DELIVERY_DOC_ID: String? = null
    var t1: TextView? = null
    var t2: TextView? = null
    var t3: TextView? = null
    var t4: TextView? = null
    var t5: TextView? = null
    var t6: TextView? = null
    var conformDeliveryButton: Button? = null
    var cancelDeliveryButton: Button? = null
    var FIRE_db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var mapFragment: MapFragment? = null
    var geoPoint: GeoPoint? = null
    var scrollView: ScrollView? = null
    var backButton: ImageView? = null
    private fun IdSaver() {
        t1 = findViewById(R.id.act_dc_foodname)
        t2 = findViewById(R.id.act_dc_foodweight)
        t3 = findViewById(R.id.act_dc_totalorder)
        t4 = findViewById(R.id.aact_dc_sellerfoodinfo)
        t5 = findViewById(R.id.act_dc_deliverytime)
        t6 = findViewById(R.id.act_dc_buyername)
        conformDeliveryButton = findViewById(R.id.act_dc_conform)
        cancelDeliveryButton = findViewById(R.id.act_dc_cancel)
        backButton = findViewById(R.id.act_dc_back_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_conform)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        val intent: Intent = getIntent()
        DELIVERY_DOC_ID = intent.getStringExtra("DELIVERY_DOC_ID")
        FIRE_db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().getCurrentUser()
        IdSaver()
        backButton!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })
        FIRE_db!!.collection(FireTag.FireUser).document(user!!.getUid())
            .collection(FireTag.FireDelivery).document((DELIVERY_DOC_ID)!!).get()
            .addOnSuccessListener(OnSuccessListener({ documentSnapshot: DocumentSnapshot ->
                setDELIVERY(
                    documentSnapshot
                )
            }))
    }

    private val geoPoints: Unit
        private get() {
            mapFragment = getFragmentManager().findFragmentById(R.id.act_dc_map) as MapFragment?
            if (mapFragment != null) {
                mapFragment!!.getMapAsync(this)
            }
        }

    public override fun onMapReady(googleMap: GoogleMap) {
        scrollView = findViewById(R.id.act_dc_scroll)
        googleMap.setOnCameraMoveListener(object : OnCameraMoveListener {
            public override fun onCameraMove() {
                scrollView?.requestDisallowInterceptTouchEvent(true)
            }
        })
        googleMap.setTrafficEnabled(true)
        Log.d(Mtag.Tag, "onMapReady: ")
        googleMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    geoPoint!!.getLatitude(), geoPoint!!.getLongitude()
                )
            )
        )
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    geoPoint!!.getLatitude(), geoPoint!!.getLongitude()
                ), 15f
            )
        )
    }

    private fun setDELIVERY(documentSnapshot: DocumentSnapshot) {
        val deliveryBoy: DELIVERY_BOY? = documentSnapshot.toObject(DELIVERY_BOY::class.java)
        assert(deliveryBoy != null)
        geoPoint = deliveryBoy!!.DELIVERY_ADDRESS
        geoPoints
        val s: String = "Food : " + deliveryBoy.FOOD_NAME
        t1!!.setText(s)
        FIRE_db!!.collection(FireTag.FireUser)
            .document((deliveryBoy.BUYER_ID)!!)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    val s1: String = "Buyer : " + documentSnapshot.get("user_NAME")
                    t6!!.setText(s1)
                }
            })
        val s3: String = "Delivery Time : " + deliveryBoy.DELIVERY_TIME
        t5!!.setText(s3)
        val s4: String = "No. Order  :  " + deliveryBoy.TOTAL_ORDER
        t3!!.setText(s4)
        FIRE_db!!.collection(FireTag.FireMarket).document((deliveryBoy.FOOD_ID)!!).get()
            .addOnSuccessListener(OnSuccessListener({ documentSnapshot1: DocumentSnapshot ->
                val s6: String = "Food Info: " + documentSnapshot1.get("food_INFO")
                t4!!.setText(s6)
                val s7: String = "Weight: " + documentSnapshot1.get("food_WEIGHT") + " KG"
                t2!!.setText(s7)
            }))
        val HISTORY_DOC_REF: DocumentReference = FIRE_db!!.collection(FireTag.FireUser)
            .document((Objects.requireNonNull(documentSnapshot.get("buyer_ID")) as String?)!!)
            .collection(FireTag.FireHistory)
            .document((Objects.requireNonNull(documentSnapshot.get("buyer_HISTORY_ID")) as String?)!!)
        conformDeliveryButton!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                documentSnapshot.getReference()
                    .update("conform_DELIVERY", true)
                FIRE_db!!.collection(FireTag.FireUser)
                    .document((Objects.requireNonNull(documentSnapshot.get("buyer_ID")) as String?)!!)
                    .get()
                    .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                        public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                            HistoryStatusChange(
                                BUY_HISTORY.ORDER_CONFORM,
                                HISTORY_DOC_REF,
                                documentSnapshot.get("fcm_TOKEN") as String?,
                                ""
                            )
                        }
                    })
            }
        })
        cancelDeliveryButton!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                documentSnapshot.getReference()
                    .update("cancel_DELIVERY", true)
                val layoutInflater: LayoutInflater = LayoutInflater.from(this@DeliveryConform)
                val v: View = layoutInflater.inflate(R.layout.dial_cancelorder, null)
                val alertDialog: AlertDialog
                alertDialog = AlertDialog.Builder(this@DeliveryConform)
                    .setView(v)
                    .create()
                alertDialog.show()
                val cancelReason: EditText = v.findViewById(R.id.dial_co_cancelreason)
                val sendQuery: Button = v.findViewById(R.id.dial_co_sendquery)
                sendQuery.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(view: View) {
                        alertDialog.dismiss()
                        FIRE_db!!.collection(FireTag.FireUser)
                            .document((Objects.requireNonNull(documentSnapshot.get("buyer_ID")) as String?)!!)
                            .get()
                            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                                public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                                    HistoryStatusChange(
                                        BUY_HISTORY.ORDER_CANCEL,
                                        HISTORY_DOC_REF,
                                        documentSnapshot.get("fcm_TOKEN") as String?,
                                        cancelReason.getText().toString()
                                    )
                                }
                            })
                    }
                })

                /*     i1=v.findViewById(R.id.);
                i1.setOnClickListener(view -> alertDialog.dismiss());

                i2=v.findViewById(R.id.dial_fp_foodpic);

                Glide.with(context)
                        .load(snapshot.get("photo_URL"))
                        .into(i2);
*/
            }
        })
    }

    private fun ConformOrderNotify(token: String?) {
        val sendNotify: SendNotify = SendNotify(this@DeliveryConform)
        sendNotify.StartNotify(token, "Order Conform..", "Your Order is conform", "No Need")
        finish()
    }

    private fun CancelOrderNotify(token: String?, DisplayMessage: String) {
        val sendNotify: SendNotify = SendNotify(this@DeliveryConform)
        sendNotify.StartNotify(
            token, "Order Cancel..", "Your Order is Canceled Reason is " + DisplayMessage, "No Need"
        )
        finish()
    }

    private fun HistoryStatusChange(
        code: Int,
        HistoryOrderDocId: DocumentReference,
        token: String?,
        DisplayMessage: String
    ) {
        HistoryOrderDocId
            .update("order_STATUS", code)
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                public override fun onSuccess(unused: Void?) {
                    when (code) {
                        BUY_HISTORY.ORDER_CONFORM -> ConformOrderNotify(token)
                        BUY_HISTORY.ORDER_CANCEL -> CancelOrderNotify(token, DisplayMessage)
                    }
                }
            })
    }
}