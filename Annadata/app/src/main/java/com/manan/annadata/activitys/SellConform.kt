package com.manan.annadata.activitys

import android.Manifest
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.manan.annadata.R
import com.manan.annadata.activityshelpers.HeyToaster
import com.manan.annadata.dbhelpers.BUY_HISTORY
import com.manan.annadata.dbhelpers.DELIVERY_BOY
import com.manan.annadata.dbhelpers.FOOD_USERS
import com.manan.annadata.dbhelpers.MARKET
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import com.manan.annadata.notify.SendNotify
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SellConform constructor() : AppCompatActivity(), OnMapReadyCallback {
    var plus: TextView? = null
    var minus: TextView? = null
    var show: TextView? = null
    var FIREBASE_DOCUMENT_ID: String? = null
    var Time_Flag: Boolean = false
    var Food_time: String? = null
    var Hour: Int = 0
    var Minute: Int = 0
    var calendar: Calendar? = null
    var FIRE_db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var t1: TextView? = null
    var t2: TextView? = null
    var t3: TextView? = null
    var t4: TextView? = null
    var t7: TextView? = null
    var t9: TextView? = null
    var i: ImageView? = null
    var vegNonveg: ImageView? = null
    var button: Button? = null
    var button2: Button? = null
    var close_dialog: Button? = null
    var locationAddress: Button? = null
    var tiffinShareLink: ImageView? = null
    var dialog: Dialog? = null
    var dialogForLoad: Dialog? = null
    var mapFragment: MapFragment? = null
    var geoPoint: GeoPoint? = null
    var cityName: String? = null
    var currentLeftOrder: Long? = null
    var backButton: ImageView? = null
    var listenerRegistration_of_orderLeft: ListenerRegistration? = null
    private fun IdSaver() {
        t2 = findViewById(R.id.scfoodname)
        t3 = findViewById(R.id.scfoodvalue)
        t4 = findViewById(R.id.scfoodweight)
        t7 = findViewById(R.id.scfoodleft)
        t9 = findViewById(R.id.scdescription)
        vegNonveg = findViewById(R.id.nonvegveg)
        plus = findViewById(R.id.scplus)
        minus = findViewById(R.id.scminus)
        show = findViewById(R.id.scorder)
        button = findViewById(R.id.scconformbutton)
        button2 = findViewById(R.id.scordertime)
        locationAddress = findViewById(R.id.act_sc_location)
        tiffinShareLink = findViewById(R.id.act_sc_sharetiffinlink)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_conform)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        user = FirebaseAuth.getInstance().getCurrentUser()
        val get_intent: Intent = getIntent()
        FIREBASE_DOCUMENT_ID = get_intent.getStringExtra("FIRE_STORE_DOCUMENT")
        FIRE_db = FirebaseFirestore.getInstance()
        IdSaver()
        dialogForLoad = Dialog(this)
        dialogForLoad!!.setContentView(R.layout.dial_set_animation)
        dialogForLoad!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        dialogForLoad!!.setCancelable(false)
        dialogForLoad!!.create()
        val lottieAnimationView: LottieAnimationView =
            dialogForLoad!!.findViewById(R.id.dial_sa_load)
        lottieAnimationView.setVisibility(View.VISIBLE)
        dialogForLoad!!.show()
        setUserCityLocation()
        calendar = Calendar.getInstance()
        Hour =
            if (calendar!!.get(Calendar.AM_PM) == Calendar.PM) calendar!!.get(Calendar.HOUR)
                ?.plus(12) as Int else calendar!!.get(
                Calendar.HOUR
            )
        Minute = calendar!!.get(Calendar.MINUTE)
        val charSequence2: CharSequence = DateFormat.format("hh:mm a", calendar)
        Food_time = charSequence2 as String?
        Log.d(Mtag.Tag, "1->" + Food_time)
        Time_Flag = false
        button2!!.setOnClickListener(View.OnClickListener({ view: View? ->
            TimeSelectForOrder()
            Toast.makeText(this@SellConform, Food_time, Toast.LENGTH_SHORT).show()
        }))
        Log.d(Mtag.Tag, "onCreate: " + FIREBASE_DOCUMENT_ID)
        FIRE_db!!.collection(FireTag.FireMarket).document((FIREBASE_DOCUMENT_ID)!!)
            .get().addOnSuccessListener(OnSuccessListener({ documentSnapshot: DocumentSnapshot ->
                SetMarketItems(documentSnapshot)
            }))
    }

    private fun setUserCityLocation() {
        FIRE_db!!.collection(FireTag.FireUser)
            .document(user!!.getUid())
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    geoPoint = documentSnapshot.get("user_LOCATION") as GeoPoint?
                    cityName = documentSnapshot.get("address") as String?
                    createDialogBox()
                }
            })
    }

    private fun createDialogBox() {
        setPermissionControl()
        HeyToaster.HeyToaster1(
            this,
            "Using Map Icon you can open the map \nand provide accurate information for deliver your order",
            R.layout.toast_info
        )
        dialogForLoad!!.dismiss()
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dial_map)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        close_dialog = dialog!!.findViewById(R.id.dial_m_close)
        locationAddress!!.setOnClickListener(View.OnClickListener({ view: View? -> openMap() }))
        close_dialog!!.setOnClickListener(View.OnClickListener({ view: View? -> closeMap() }))
    }

    private fun openMap() {
        dialog!!.show()
        mapFragment = getFragmentManager().findFragmentById(R.id.dial_m_google_map) as MapFragment?
        if (mapFragment != null) {
            mapFragment!!.getMapAsync(this)
        }
    }

    private fun closeMap() {
        dialog!!.dismiss()
    }

    public override fun onMapReady(googleMap: GoogleMap) {
        Log.d(Mtag.Tag, "onSuccess: 275")
        setPermissionControl()
        googleMap.clear()
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
                ), 10f
            )
        )
        googleMap.setOnMapClickListener(OnMapClickListener { latLng: LatLng ->
            val markerOptions: MarkerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Marker Set On Your City")
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            googleMap.addMarker(markerOptions)
            geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
        })
    }

    fun SetMarketItems(documentSnapshot: DocumentSnapshot) {
        val m: MARKET? = documentSnapshot.toObject(MARKET::class.java)

        vegNonveg?.setImageBitmap(BitmapFactory.decodeResource(resources, if (m?.VEG == true) R.drawable.veg_symbol else R.drawable.non_veg_symbol))

        var s: String?
        assert(m != null)
        s = m!!.FOOD_NAME
        t2!!.setText(s)
        if (m.FOOD_PRICE == 0) {
            s = "Donation Purpose"
        } else {
            s = "₹ " + m.FOOD_PRICE
        }
        t3!!.setText(s)
        s = m.FOOD_WEIGHT.toString() + " Gram"
        t4!!.setText(s)
        t9!!.setText(m.FOOD_INFO.toString())

        //  Real Time Food Order Left Update
        OrderLeft(documentSnapshot)

//        s="Food info : "+m.getFOOD_INFO();
//        t8.setText(s);
        i = findViewById(R.id.act_s_c_foodphoto)
        Glide.with(this)
            .load(m.PHOTO_URL)
            .into(i!!)
        tiffinShareLink!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val sendIntent: Intent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, m.TIFFIN_LINK)
                sendIntent.setType("text/plain")
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(sendIntent, "Hello World"))
            }
        })
        MakeFinalOrderForUser(m)
    }

    private fun OrderLeft(documentSnapshot: DocumentSnapshot) {
        listenerRegistration_of_orderLeft = documentSnapshot.getReference().addSnapshotListener(
            EventListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Toast.makeText(this@SellConform, error.message, Toast.LENGTH_SHORT).show()
                } else if (value == null) {
                    Toast.makeText(this@SellConform, "No Order Left", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.d(Mtag.Tag, "\n\n\n\nOrderLeft: " + value.get("left_ORDER") + "\n\n\n\n")
                    currentLeftOrder = value.get("left_ORDER") as Long
                    val s: String = currentLeftOrder.toString() + " Order Left"
                    t7!!.setText(s)
                    plus!!.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(view: View) {
                            var temp: Long = (show!!.getText().toString().toLong() + 1)
                            temp = if (temp <= currentLeftOrder!!) temp else currentLeftOrder!!
                            show!!.setText("" + (temp))
                        }
                    })
                    minus!!.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(view: View) {
                            var temp: Int = (show!!.getText().toString().toInt() - 1)
                            temp = if (temp > 0) temp else 1
                            show!!.setText("" + (temp))
                        }
                    })
                }
            }
        )
    }

    fun MakeFinalOrderForUser(market: MARKET?) {
        button!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (Time_Flag) {
                TimeSelectForOrder()
                Toast.makeText(this@SellConform, "Old Time " + Food_time, Toast.LENGTH_SHORT).show()
            } else {
                val deliveryBoy: DELIVERY_BOY = DELIVERY_BOY()
                assert(user != null)
                deliveryBoy.BUYER_ID = user!!.getUid()
                deliveryBoy.FOOD_ID = FIREBASE_DOCUMENT_ID
                deliveryBoy.FOOD_NAME = market!!.FOOD_NAME
                deliveryBoy.DELIVERY_ADDRESS = geoPoint
                deliveryBoy.DELIVERY_TIME = Food_time
                deliveryBoy.FOOD_MONEY = market.FOOD_PRICE
                deliveryBoy.FOOD_POINT = market.FOOD_POINT
                deliveryBoy.TOTAL_ORDER = show!!.getText().toString().toInt()
                deliveryBoy.CONFORM_DELIVERY = false
                deliveryBoy.BUYER_HISTORY_ID = " "
                FIRE_db!!.collection(FireTag.FireUser).document((market.RETAILER_ID)!!)
                    .collection(FireTag.FireDelivery).add(deliveryBoy)
                    .addOnSuccessListener(
                        object : OnSuccessListener<DocumentReference> {
                            public override fun onSuccess(documentReference: DocumentReference) {
                                AfterDelivery(
                                    documentReference,
                                    market,
                                    show!!.getText().toString()
                                )
                            }
                        }
                    )
            }
        })
    }

    fun AfterDelivery(DeliveryDoc: DocumentReference, market: MARKET?, s2: String) {
        val DeliveryId: String = DeliveryDoc.getId()
        val map: MutableMap<String, Any> = HashMap()
        map.put("left_ORDER", FieldValue.increment(-s2.toInt().toLong()))
        if (market!!.LEFT_ORDER - (s2.toInt()) <= 0) map.put("no_FOOD_LEFT", true)
        FIRE_db!!.collection(FireTag.FireMarket).document((FIREBASE_DOCUMENT_ID)!!)
            .update(map)
        FIRE_db!!.collection(FireTag.FireUser).document((market.RETAILER_ID)!!)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    HistoryCreateForUserOrder(DeliveryDoc, documentSnapshot, market, s2, DeliveryId)
                }
            })
    }

    fun HistoryCreateForUserOrder(
        DeliveryDoc: DocumentReference,
        RetailerDoc: DocumentSnapshot,
        market: MARKET?,
        ORDER: String,
        DeliveryId: String?
    ) {
        val buyHistory: BUY_HISTORY = BUY_HISTORY()

        // User is Retailer because we want ot set retailer name is buyer History
        val foodUsers: FOOD_USERS? = RetailerDoc.toObject(FOOD_USERS::class.java)
        assert(foodUsers != null)
        Log.d(Mtag.Tag, "HISTORY_RECORD: " + foodUsers!!.USER_NAME)
        buyHistory.FOOD_ID = FIREBASE_DOCUMENT_ID
        buyHistory.FOOD_NAME = market!!.FOOD_NAME
        buyHistory.FOOD_WEIGHT = market.FOOD_WEIGHT
        buyHistory.TOTAL_FOOD = ORDER.toInt()
        buyHistory.RETAILER_ID = foodUsers.ID
        buyHistory.FOOD_INFO = market.FOOD_INFO
        buyHistory.FOOD_POINT = market.FOOD_POINT
        buyHistory.FOOD_PRICE = market.FOOD_PRICE
        buyHistory.ORDER_STATUS = BUY_HISTORY.ORDER_PLACED
        buyHistory.RATING_FOOD = 0
        FIRE_db!!.collection(FireTag.FireUser).document(user!!.getUid())
            .collection(FireTag.FireHistory).add(buyHistory)
            .addOnSuccessListener(object : OnSuccessListener<DocumentReference> {
                public override fun onSuccess(documentReference: DocumentReference) {
                    val map: MutableMap<String, Any> = HashMap()
                    map.put("buyer_HISTORY_ID", documentReference.getId())
                    DeliveryDoc.update(map)
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            public override fun onSuccess(unused: Void?) {
                                val dataJson: JSONObject = JSONObject()
                                try {
                                    dataJson.put(
                                        "userName",
                                        "Hello " + user!!.getDisplayName() + "<br/>Thank you for buying tiffin from<br/>" + foodUsers.USER_NAME
                                    )
                                    dataJson.put("sender", user!!.getEmail())
                                    dataJson.put("tiffinName", market.FOOD_NAME)
                                    dataJson.put(
                                        "price",
                                        ORDER + "x" + market.FOOD_PRICE + " Rupee"
                                    )
                                    dataJson.put(
                                        "total",
                                        "" + (ORDER.toInt() * market.FOOD_PRICE) + "Rupee"
                                    )
                                    Log.d(Mtag.Tag, "onSuccess: json no " + dataJson)
                                } catch (e1: JSONException) {
                                    Log.d(Mtag.Tag, "onSuccess: json eeroor" + e1)
                                }
                                val request: JsonObjectRequest = object : JsonObjectRequest(
                                    Method.POST,
                                    "link",
                                    dataJson,
                                    Response.Listener { response: JSONObject ->
                                        Log.d(Mtag.Tag, "onResponse Backend: $response")

                                    },
                                    Response.ErrorListener { error: VolleyError ->
                                        GetFCMToken(
                                            foodUsers.FCM_TOKEN,
                                            foodUsers.USER_NAME,
                                            DeliveryId
                                        )
                                    }
                                ) {
                                    public override fun getHeaders(): Map<String, String> {
                                        val map: MutableMap<String, String> = HashMap()
                                        map.put("Content-type", "application/json")
                                        return map
                                    }

                                    public override fun getBodyContentType(): String {
                                        return "application/json"
                                    }
                                }
                                val requestQueue: RequestQueue =
                                    Volley.newRequestQueue(this@SellConform)
                                request.setRetryPolicy(
                                    DefaultRetryPolicy(
                                        30000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                    )
                                )
                                requestQueue.add(request)
                            }
                        })
                }
            })
    }

    fun GetFCMToken(token: String?, Retailer: String?, DeliveryId: String?) {
        val sendNotify: SendNotify = SendNotify(this)
        sendNotify.StartNotify(token, "New Order... " + Retailer, "Check All Details", DeliveryId)
        Toast.makeText(this@SellConform, Food_time, Toast.LENGTH_SHORT).show()
        finish()
    }

    fun TimeSelectForOrder() {
        val timePickerDialog: TimePickerDialog =
            TimePickerDialog(this, OnTimeSetListener({ timePicker: TimePicker?, i: Int, i1: Int ->
                Time_Flag = (i < Hour) || (i == Hour && i1 < Minute)
                val calendar1: Calendar = Calendar.getInstance()
                calendar1.set(Calendar.HOUR, (i + 12) % 24)
                calendar1.set(Calendar.MINUTE, i1)
                val charSequence: CharSequence = DateFormat.format("hh:mm a", calendar1)
                Food_time = charSequence as String?
                Log.d(Mtag.Tag, "2->" + Food_time)
            }), Hour, Minute, true)
        timePickerDialog.show()
    }

    override fun onStop() {
        super.onStop()
        if (listenerRegistration_of_orderLeft != null) listenerRegistration_of_orderLeft!!.remove()
    }

    private fun setPermissionControl() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Please Grant Location Permission", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Flash::class.java))
            finish()
        }
    }
}