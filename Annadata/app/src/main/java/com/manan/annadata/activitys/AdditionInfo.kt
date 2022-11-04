package com.manan.annadata.activitys

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.manan.annadata.R
import com.manan.annadata.activityshelpers.CityName
import com.manan.annadata.activityshelpers.HeyToaster
import com.manan.annadata.dbhelpers.FOOD_USERS
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream

class AdditionInfo : AppCompatActivity(), OnMapReadyCallback {
    var user: FirebaseUser? = null
    var FIRE_db: FirebaseFirestore? = null
    private var Fire_ST: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var helloUser: TextView? = null
    var myCity: TextView? = null
    var imageView: ImageView? = null
    private var dialog: Dialog? = null
    private var close_dialog: Button? = null
    private var conform: Button? = null
    private var bitmap: Bitmap? = null
    private var cameraButton: ImageButton? = null
    private var locationButton: ImageButton? = null
    private var mapFragment: MapFragment? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var lottieAnimationView1: LottieAnimationView? = null
    var cityName: String? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var geocodingISon: Boolean = true
    private var geoPoint: GeoPoint? = null

    //********************************************************************************
    // Assign All IDs
    private fun IdSaver() {
        helloUser = findViewById(R.id.act_sign_name)
        lottieAnimationView = findViewById(R.id.act_sign_firework2)
        lottieAnimationView1 = findViewById(R.id.act_sign_firework1)
        cameraButton = findViewById(R.id.act_sign_capture)
        conform = findViewById(R.id.act_sign_submit)
        imageView = findViewById(R.id.act_sign_profile)
        locationButton = findViewById(R.id.act_sign_map)
        myCity = findViewById(R.id.act_sign_city)
    }

    private val requestCamera =
        registerForActivityResult((ActivityResultContracts.RequestPermission())) {
            if (it) {
                activityResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                Toast.makeText(
                    this@AdditionInfo,
                    "Please give Camera Access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    //**********************************************************************************
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)
        supportActionBar?.hide()

        // Remove After Testing App
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg3)
        user = FirebaseAuth.getInstance().currentUser
        FIRE_db = FirebaseFirestore.getInstance()
        Fire_ST = FirebaseStorage.getInstance()
        IdSaver()
        lottieAnimationView!!.visibility = View.GONE
        lottieAnimationView1!!.visibility = View.GONE
        SetDefaultLocation()
        val s: String = "Hello " + user!!.displayName
        helloUser!!.text = s
        cameraButton!!.setOnClickListener {
            requestCamera.launch((Manifest.permission.CAMERA))
        }
        conform!!.setOnClickListener {
            if (citySearch(cityName)) {
                Toast.makeText(this, "Please Open Map and Set Valid City", Toast.LENGTH_SHORT)
                    .show()
            } else if (bitmap == null) {
                Toast.makeText(this@AdditionInfo, "Put Profile Picture", Toast.LENGTH_SHORT).show()
            } else if (geocodingISon) {
                HeyToaster.HeyToaster1(
                    this@AdditionInfo,
                    "Please Wait a Second We Computing Location",
                    R.layout.toast_info
                )
            } else {
                cameraButton!!.visibility = View.GONE
                conform!!.visibility = View.GONE
                lottieAnimationView!!.visibility = View.VISIBLE
                lottieAnimationView1!!.visibility = View.VISIBLE
                val foodUsers = FOOD_USERS()
                foodUsers.USER_NAME = user!!.displayName
                foodUsers.ID = user!!.uid
                foodUsers.USER_LOCATION = geoPoint
                foodUsers.ADDRESS = cityName
                foodUsers.MAIL = user!!.email
                FIRE_db!!.collection(FireTag.FireUser).document(user!!.uid).set(foodUsers)
                    .addOnSuccessListener { UploadPicForUser() }
            }
        }
    }

    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            imageView!!.setImageBitmap( it.data?.extras?.get("data") as Bitmap?)

        }
    }

    private fun UploadPicForUser() {
        storageReference = Fire_ST!!.reference
            .child(user!!.uid)
            .child("User_Profile_Pic")
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
        storageReference!!.putBytes(byteArrayOutputStream.toByteArray())
            .addOnSuccessListener {
                Toast.makeText(this@AdditionInfo, "Thanks For Uploading Pic", Toast.LENGTH_SHORT)
                    .show()
                GetUrlOfUserPic()
            }
    }

    private fun GetUrlOfUserPic() {
        storageReference!!.downloadUrl
            .addOnSuccessListener { uri: Uri ->
                FIRE_db!!.collection(FireTag.FireUser)
                    .document(user!!.uid)
                    .update("photo_URL", uri.toString())
                    .addOnSuccessListener {
                        FirebaseMessaging.getInstance().token
                            .addOnSuccessListener { token: String? ->
                                GetFCMToken(
                                    token
                                )
                            }
                    }
            }
    }

    private fun GetFCMToken(token: String?) {
        FIRE_db!!.collection(FireTag.FireUser)
            .document(user!!.uid)
            .update("fcm_TOKEN", token)
            .addOnSuccessListener {
                val intent = Intent(this@AdditionInfo, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    private fun citySearch(s: String?): Boolean {
        for (city: String in CityName.list) {
            if ((s == city)) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun SetDefaultLocation() {
        Log.d(Mtag.Tag, "onSuccess: 227")
        setPermissionControl()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(Mtag.Tag, "onSuccess: 233$location")
                if (location != null) {
                    Log.d(Mtag.Tag, "onSuccess: 235")
                    geoPoint = GeoPoint(location.latitude, location.longitude)
                    HeyToaster.HeyToaster1(
                        this@AdditionInfo,
                        "Using Map Icon you can open the map \n Using camera icon you can open camera",
                        R.layout.toast_info
                    )
                    LATLANGtoCITYName()
                    CreateDialog()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun LATLANGtoCITYName() {
        geocodingISon = true
       Log.d(Mtag.Tag, url)
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null, { response ->
                try {
                    val arr: JSONArray = response.getJSONArray("features")
                    Log.d(Mtag.Tag, "12333onResponse: $arr")
                    cityName = arr.getJSONObject(0).getString("text")
                    if ((cityName == "")) {
                        Toast.makeText(
                            this@AdditionInfo,
                            "Please Select city correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                        openMap()
                    } else {
                        myCity!!.text = ("My City is $cityName")
                    }
                    Log.d(Mtag.Tag, "12333onResponse: $arr")
                } catch (e: JSONException) {
                    Log.d(Mtag.Tag, "onResponse: 287")
                    Toast.makeText(
                        this@AdditionInfo,
                        "Please Select city correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                    openMap()
                    e.printStackTrace()
                }
                geocodingISon = false
            }
        ) { error ->
            Log.d(Mtag.Tag, "onErrorResponse: $error")
            geocodingISon = false
        }
        requestQueue.add(jsonObjectRequest)
    }

    private fun CreateDialog() {
        setPermissionControl()
        locationButton!!.setVisibility(View.VISIBLE)
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dial_map)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        close_dialog = dialog!!.findViewById(R.id.dial_m_close)
        locationButton!!.setOnClickListener {
            if (geocodingISon) HeyToaster.HeyToaster1(
                this@AdditionInfo,
                "Please wait\nWe currently fetch your\nLocation",
                R.layout.toast_info
            ) else openMap()
        }
        close_dialog?.setOnClickListener { closeMap() }
    }

    private fun openMap() {
        dialog!!.show()
        mapFragment = fragmentManager.findFragmentById(R.id.dial_m_google_map) as MapFragment?
        if (mapFragment != null) {
            mapFragment!!.getMapAsync(this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun closeMap() {
        dialog!!.dismiss()
        myCity!!.text = "Please wait we set your City"
        Log.d(Mtag.Tag, "getLocation: 308 $geoPoint")
        LATLANGtoCITYName()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(Mtag.Tag, "onSuccess: 313")
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
                    geoPoint!!.latitude, geoPoint!!.longitude
                ), 10f
            )
        )
        googleMap.setOnMapClickListener { latLng: LatLng ->
            val markerOptions: MarkerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Marker Set On Your City")
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            googleMap.addMarker(markerOptions)
            geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
        }
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