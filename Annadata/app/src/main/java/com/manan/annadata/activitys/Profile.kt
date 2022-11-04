package com.manan.annadata.activitys

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.manan.annadata.R
import com.manan.annadata.activityshelpers.CityName
import com.manan.annadata.activityshelpers.HeyToaster
import com.manan.annadata.dbhelpers.BUY_HISTORY
import com.manan.annadata.dbhelpers.DELIVERY_BOY
import com.manan.annadata.dbhelpers.FOOD_USERS
import com.manan.annadata.foodcycle.HistoryAdapter
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

class Profile constructor() : AppCompatActivity(), AuthStateListener {
    var FIRE_db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var showMoney: TextView? = null
    var t2: TextView? = null
    var showUserName: TextView? = null
    var showCityName: TextView? = null
    var dial_cityName: TextView? = null
    var historyAdapter: HistoryAdapter? = null
    var recyclerView: RecyclerView? = null
    var imageView: ImageView? = null
    var ivLogout: ImageView? = null
    var dial_userPic: ImageView? = null
    var myTiffinLink: ImageButton? = null
    var myTiffinBanner: ImageButton? = null
    var settingProfile: ImageButton? = null
    var dial_camera: ImageButton? = null
    var dial_location: ImageButton? = null
    var ivBack: ImageView? = null
    var dial_close_button: Button? = null
    var dial_submit_button: Button? = null
    var dial_mapFragment: MapFragment? = null
    var dialogToRating: Dialog? = null
    var dialogToChangeProfile: Dialog? = null
    var dial_username: EditText? = null
    var geocodingISon: Boolean = false
    var geoPoint: GeoPoint? = null
    var dial_bitmap: Bitmap? = null
    var userData: DocumentSnapshot? = null
    var foodUsers: FOOD_USERS? = null
    var cityName: String? = ""
    var Fire_ST: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var shouldIFinish: Boolean = true
    private fun IdSaver() {
        recyclerView = findViewById(R.id.rcycle_p_historycycle)
        showMoney = findViewById(R.id.act_p_money)
        //        t2 = findViewById(R.id.prodonate);
        showUserName = findViewById(R.id.act_p_user)
        showCityName = findViewById(R.id.act_p_user_location)
        ivLogout = findViewById(R.id.logout)
        imageView = findViewById(R.id.act_p_userpic)
        myTiffinLink = findViewById(R.id.act_p_user_tiffinlink)
        myTiffinBanner = findViewById(R.id.act_p_user_tiffinbannerdownload)
        settingProfile = findViewById(R.id.act_p_setting)
    }


    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            dial_bitmap = it.data?.extras?.get("data") as Bitmap?
            dial_userPic!!.setImageBitmap(dial_bitmap)

        }
    }
    private val requestCamera =
        registerForActivityResult((ActivityResultContracts.RequestPermission())) {
            if (it) {
                activityResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                Toast.makeText(
                    this@Profile,
                    "Please give Camera Access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        FIRE_db = FirebaseFirestore.getInstance()
        Fire_ST = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().getCurrentUser()

        // hirak
        ivBack = findViewById(R.id.ivBack)
        ivBack?.setOnClickListener(View.OnClickListener({ view: View? -> finish() }))
        IdSaver()
        dialogToChangeProfile = Dialog(this@Profile)
        dialogToChangeProfile!!.setContentView(R.layout.dial_changeprofile)
        dialogToChangeProfile!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        dialogToChangeProfile!!.setCancelable(false)
        dialogToChangeProfile!!.create()
        FIRE_db!!.collection(FireTag.FireUser)
            .document(user!!.getUid())
            .get()
            .addOnSuccessListener(OnSuccessListener({ documentSnapshot: DocumentSnapshot ->
                userData = documentSnapshot
                showCityName!!.setText(userData!!.get("address") as String?)
                foodUsers = documentSnapshot.toObject(FOOD_USERS::class.java)
                myTiffinLink!!.setOnClickListener(View.OnClickListener({ view: View? -> SETSellerLink() }))
                myTiffinBanner!!.setOnClickListener(View.OnClickListener({ view: View? -> SETSellerBanner() }))
                Glide.with(this@Profile)
                    .load(foodUsers!!.PHOTO_URL)
                    .listener(object : RequestListener<Drawable?> {
                        public override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        public override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            settingProfile!!.setOnClickListener(View.OnClickListener({ view: View? ->
                                dialogToChangeProfile!!.show()
                                SetUpProfileData()
                            }))
                            return false
                        }
                    })
                    .into((imageView)!!)
            }))
        FIRE_db!!.collection(FireTag.FireUser).document(user!!.getUid())
            .collection(FireTag.FireDelivery)
            .whereEqualTo("conform_DELIVERY", true)
            .get()
            .addOnSuccessListener(OnSuccessListener({ queryDocumentSnapshots: QuerySnapshot ->
                val snapshotList: List<DocumentSnapshot> = queryDocumentSnapshots.getDocuments()
                var TotalMoney: Int = 0
                var TotalPoint: Int = 0
                for (snap: DocumentSnapshot in snapshotList) {
                    val deliveryBoy: DELIVERY_BOY? = snap.toObject(DELIVERY_BOY::class.java)
                    assert(deliveryBoy != null)
                    TotalMoney += (deliveryBoy!!.FOOD_MONEY) * (deliveryBoy.TOTAL_ORDER)
                    TotalPoint += (deliveryBoy.FOOD_POINT) * (deliveryBoy.TOTAL_ORDER)
                }
                val s1: String = "₹" + TotalMoney
                showMoney!!.setText(s1)
                val s2: String = "" + TotalPoint
            }))
        showUserName!!.setText(user!!.getDisplayName())
        ivLogout!!.setOnClickListener(View.OnClickListener({ view: View? -> showLogoutAlert() }))
    }

    fun SetUpProfileData() {
        dial_userPic = dialogToChangeProfile!!.findViewById(R.id.dial_cp_userpic)
        dial_username = dialogToChangeProfile!!.findViewById(R.id.dial_cp_username)
        dial_cityName = dialogToChangeProfile!!.findViewById(R.id.dial_cp_cityname)
        dial_camera = dialogToChangeProfile!!.findViewById(R.id.dial_change_photo)
        dial_location = dialogToChangeProfile!!.findViewById(R.id.dial_cp_sign_map)
        dial_close_button = dialogToChangeProfile!!.findViewById(R.id.dial_cp_close)
        dial_submit_button = dialogToChangeProfile!!.findViewById(R.id.dial_cp_submit)
        SetProfileChangeData()
    }

    private fun SetProfileChangeData() {
        dial_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fooddoante)
        Glide.with(this@Profile)
            .load(foodUsers!!.PHOTO_URL)
            .into((dial_userPic)!!)
        dial_username!!.setText(foodUsers!!.USER_NAME)
        val s: String = "Current City : " + foodUsers!!.ADDRESS
        showCityName!!.setText(foodUsers!!.ADDRESS)
        dial_cityName!!.setText(s)
        cityName = foodUsers!!.ADDRESS
        geoPoint = foodUsers!!.USER_LOCATION
        dial_mapFragment =
            getFragmentManager().findFragmentById(R.id.dial_cp_google_map) as MapFragment?
        if (dial_mapFragment != null) {
            dial_mapFragment!!.getMapAsync(OnMapReadyCallback { googleMap: GoogleMap ->
                while (geocodingISon);
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
                googleMap.setOnMapClickListener(object : OnMapClickListener {
                    public override fun onMapClick(latLng: LatLng) {
                        val markerOptions: MarkerOptions = MarkerOptions()
                        markerOptions.position(latLng)
                        markerOptions.title("Marker Set On Your City")
                        googleMap.clear()
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        googleMap.addMarker(markerOptions)
                        geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                        if (!(geoPoint == foodUsers!!.USER_LOCATION)) {
                            LATLANGtoCITYName()
                        }
                    }
                })
            })
        }
        dial_close_button!!.setOnClickListener(View.OnClickListener({ view: View? -> dialogToChangeProfile!!.dismiss() }))
        dial_camera!!.setOnClickListener(View.OnClickListener({ view: View? ->
            requestCamera.launch((android.Manifest.permission.CAMERA))
        }))
        dial_submit_button!!.setOnClickListener(View.OnClickListener({ view: View? -> SetProfileData() }))
    }

    private fun SetProfileData() {
        if (dial_username!!.getText().toString().isEmpty()) {
            dial_username!!.setFocusable(true)
            dial_username!!.setError("Please Add UserName")
            return
        }
        if (citySearch(cityName)) {
            Toast.makeText(
                this,
                "Sorry We Don't Work on this Area\nPlease Select Other City",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val map: MutableMap<String, Any?> = HashMap()
        if (!(geoPoint == foodUsers!!.USER_LOCATION)) {
            map.put("user_LOCATION", geoPoint)
        }
        if (!(cityName == foodUsers!!.ADDRESS)) {
            map.put("address", cityName)
        }
        if (!(foodUsers!!.USER_NAME == dial_username!!.getText().toString())) {
            map.put("user_NAME", dial_username!!.getText().toString())
            shouldIFinish = false
            val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(dial_username!!.getText().toString())
                .build()
            user!!.updateProfile(profileUpdates)
                .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                    shouldIFinish = true
                    if (dial_bitmap != null) {
                        shouldIFinish = false
                        SetProfileImage()
                    } else {
                        shouldIFinish = false
                        dialogToChangeProfile!!.dismiss()
                        finish()
                    }
                }))
        }
        userData!!.getReference().update(map)
            .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                if (shouldIFinish) {
                    shouldIFinish = false
                    dialogToChangeProfile!!.dismiss()
                    finish()
                }
            }))
    }

    private fun SetProfileImage() {
        storageReference = Fire_ST!!.getReference()
            .child(user!!.getUid())
            .child("User_Profile_Pic")
        val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        dial_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
        storageReference!!.putBytes(byteArrayOutputStream.toByteArray())
            .addOnSuccessListener(OnSuccessListener({ taskSnapshot: UploadTask.TaskSnapshot? ->
                Toast.makeText(this@Profile, "Thanks For Uploading Pic", Toast.LENGTH_SHORT).show()
                GetUrlOfUserPic()
            }))
    }

    private fun GetUrlOfUserPic() {
        storageReference!!.getDownloadUrl()
            .addOnSuccessListener(OnSuccessListener({ uri: Uri ->
                userData!!.getReference()
                    .update("photo_URL", uri.toString())
                    .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                        Toast.makeText(
                            this@Profile,
                            "Data Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogToChangeProfile!!.dismiss()
                        finish()
                    }))
            }))
    }

    private fun SETSellerLink() {
        Log.d(Mtag.Tag, "SETSellerLink: 162")
        val tiffinLink: String? = foodUsers!!.TIFFIN_LINK
        if (tiffinLink != null) SetSellerTiffinLink(Uri.parse(tiffinLink)) else {
            val dynamicLink: DynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://sites.google.com/view/annadata/?sellerid=" + user!!.getUid() + ""))
                .setDomainUriPrefix("https://annadatatiffin.page.link")
                .setAndroidParameters(
                    AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://github.com/MananRPatel"))
                        .build()
                )
                .setSocialMetaTagParameters(
                    SocialMetaTagParameters.Builder()
                        .setTitle("Manan R Patel")
                        .setDescription("Annnadata - Tiffin Service App \nThis is Tiffin Service Application to Sell and Buy Tiffin Online")
                        .setImageUrl(Uri.parse("https://avatars.githubusercontent.com/u/67188104?v=4"))
                        .build()
                )
                .buildDynamicLink()
            val SellerTiffinLink: Uri = dynamicLink.getUri()
            val longlink: JSONObject = JSONObject()
            val suffix: JSONObject = JSONObject()
            try {
                longlink.put("longDynamicLink", (SellerTiffinLink))
                suffix.put("option", "SHORT")
                longlink.put("suffix", suffix)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.d(Mtag.Tag, "onSuccessjson: " + longlink + "\n" + SellerTiffinLink)
           val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                dynamicToShortURL,
                longlink,
                Response.Listener({ response: JSONObject ->
                    Log.d(Mtag.Tag, "onResponse: " + response)
                    try {
                        val shortlink: String = response.get("shortLink") as String
                        userData!!.getReference().update("tiffin_LINK", shortlink)
                            .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                                SetSellerTiffinLink(
                                    Uri.parse(shortlink)
                                )
                            }))
                    } catch (e: JSONException) {
                        Log.d(Mtag.Tag, "onResponse: " + response)
                        e.printStackTrace()
                    }
                }),
                Response.ErrorListener({ error: VolleyError ->
                    Log.d(
                        Mtag.Tag,
                        "NowSuiiuiuend1: " + error.toString()
                    )
                })
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
            val requestQueue: RequestQueue = Volley.newRequestQueue(this@Profile)
            request.setRetryPolicy(
                DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            requestQueue.add(request)
        }
    }

    private fun SETSellerBanner() {
        val path: File = File(
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_PICTURES + File.separator + "Annadata" + File.separator + "Annadata_Seller_" + user!!.getUid() + ".jpg"
        )
        try {
            val streamIn: FileInputStream
            //For Check file is present in Folder or Not
            streamIn = FileInputStream(path)
            BitmapFactory.decodeStream(streamIn)
            val screenshotUri: Uri = FileProvider.getUriForFile(
                this@Profile,
                getApplicationContext().getPackageName() + ".provider",
                path
            )
            val sendIntent: Intent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            sendIntent.setType("image/*")
            sendIntent.setClipData(ClipData.newRawUri("", screenshotUri))
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(sendIntent, "Hello World"))
        } catch (e: FileNotFoundException) {
            Log.d(Mtag.Tag, "SETSELLERBANNER: 162")
            val tiffinLink: String? = foodUsers!!.TIFFIN_LINK
            if (tiffinLink != null) CreateImage(Uri.parse(tiffinLink)) else {
                val dynamicLink: DynamicLink =
                    FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://sites.google.com/view/annadata/?sellerid=" + user!!.getUid() + ""))
                        .setDomainUriPrefix("https://annadatatiffin.page.link")
                        .setAndroidParameters(
                            AndroidParameters.Builder()
                                .setFallbackUrl(Uri.parse("https://github.com/MananRPatel"))
                                .build()
                        )
                        .setSocialMetaTagParameters(
                            SocialMetaTagParameters.Builder()
                                .setTitle("Manan R Patel")
                                .setDescription("Annnadata - Tiffin Service App \nThis is Tiffin Service Application to Sell and Buy Tiffin Online")
                                .setImageUrl(Uri.parse("https://avatars.githubusercontent.com/u/67188104?v=4"))
                                .build()
                        )
                        .buildDynamicLink()
                val SellerTiffinLink: Uri = dynamicLink.getUri()
                val longlink: JSONObject = JSONObject()
                val suffix: JSONObject = JSONObject()
                try {
                    longlink.put("longDynamicLink", (SellerTiffinLink))
                    suffix.put("option", "SHORT")
                    longlink.put("suffix", suffix)
                } catch (e1: JSONException) {
                    e.printStackTrace()
                }
                Log.d(Mtag.Tag, "onSuccessjson: " + longlink + "\n" + SellerTiffinLink)
                val request: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    dynamicToShortURL,
                    longlink,
                    Response.Listener { response: JSONObject ->
                        Log.d(Mtag.Tag, "onResponse: " + response)
                        try {
                            val shortlink: String = response.get("shortLink") as String
                            userData!!.getReference().update("tiffin_LINK", shortlink)
                                .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                                    CreateImage(
                                        Uri.parse(shortlink)
                                    )
                                }))
                        } catch (e1: JSONException) {
                            Log.d(Mtag.Tag, "onResponse: " + response)
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener({ error: VolleyError ->
                        Log.d(
                            Mtag.Tag,
                            "NowSuiiuiuend2: " + error
                        )
                    })
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
                val requestQueue: RequestQueue = Volley.newRequestQueue(this@Profile)
                request.setRetryPolicy(
                    DefaultRetryPolicy(
                        30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                requestQueue.add(request)
            }
        }
    }

    private fun SetSellerTiffinLink(shortLink: Uri) {
        Log.d(Mtag.Tag, "Short link12 " + shortLink)
        val sendIntent: Intent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
        sendIntent.setType("text/plain")
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(sendIntent, "Hello World"))
    }

    private fun CreateImage(shortLink: Uri) {
        dialogToRating = Dialog(this)
        dialogToRating!!.setContentView(R.layout.banner_qrcode_sellertiffin)
        dialogToRating!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        dialogToRating!!.create()
        dialogToRating!!.show()
        val sellername: TextView = dialogToRating!!.findViewById(R.id.banner_s_sellername)
        val tiffinlink: TextView = dialogToRating!!.findViewById(R.id.banner_s_link_extra)
        val constraintLayout: ConstraintLayout = dialogToRating!!.findViewById(R.id.banner_layout)
        val tiffinqrcode: ImageView = dialogToRating!!.findViewById(R.id.banner_s_qrcode)
        val qrbutton: Button = dialogToRating!!.findViewById(R.id.banner_s_button)
        qrbutton.setVisibility(View.GONE)
        sellername.setText(user!!.getDisplayName())
        tiffinlink.setText(shortLink.toString())
        val qrcodelink: Uri =
            Uri.parse("https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl=" + shortLink + "&choe=UTF-8")
        Glide.with(this)
            .load(qrcodelink)
            .listener(object : RequestListener<Drawable?> {
                public override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(Mtag.Tag, "onLoadFailed: " + "QRFAIL")
                    return false
                }

                public override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    qrbutton.setVisibility(View.VISIBLE)
                    Log.d(Mtag.Tag, "onLoadFailed: " + "QRSUCESS")
                    return false
                }
            })
            .into(tiffinqrcode)
        qrbutton.setOnClickListener(View.OnClickListener { view: View? ->
            Log.d(
                Mtag.Tag,
                "CreateImage: " + constraintLayout.getHeight() + " " + constraintLayout.getWidth()
            )
            val returnedBitmap: Bitmap = Bitmap.createBitmap(
                constraintLayout.getWidth(),
                constraintLayout.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            val canvas: Canvas = Canvas(returnedBitmap)
            val bgDrawable: Drawable? = constraintLayout.getBackground()
            if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
            constraintLayout.draw(canvas)
            val path: File = File(
                Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_PICTURES + File.separator + "Annadata" + File.separator + "Annadata_Seller_" + user!!.getUid() + ".jpg"
            )
            try {
                val streamIn: FileInputStream
                //For Check file is present in Folder or Not
                streamIn = FileInputStream(path)
                BitmapFactory.decodeStream(streamIn)
                val screenshotUri: Uri = FileProvider.getUriForFile(
                    this@Profile,
                    getApplicationContext().getPackageName() + ".provider",
                    path
                )
                val sendIntent: Intent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                sendIntent.setType("image/*")
                sendIntent.setClipData(ClipData.newRawUri("", screenshotUri))
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(sendIntent, "Hello World"))
            } catch (e1: FileNotFoundException) {
                SendImage(returnedBitmap)
            }
        })
    }

    private fun SendImage(banner: Bitmap) {
        try {
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = getContentResolver()
                val contentValues: ContentValues = ContentValues()
                contentValues.put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "Annadata_Seller_" + user!!.getUid()
                )
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "Annadata"
                )
                val imageStore: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = imageStore?.let { resolver.openOutputStream(it) }
                banner.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                Objects.requireNonNull(outputStream)
                outputStream!!.flush()
                outputStream.close()
                HeyToaster.HeyToaster1(
                    this,
                    "This Banner Location in Phone is \nInternal Storage>Pictures>Annadata",
                    R.layout.toast_info
                )
                val sharingIntent: Intent = Intent(Intent.ACTION_SEND)
                sharingIntent.setType("image/jpeg")
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageStore)
                startActivity(Intent.createChooser(sharingIntent, "Share image using"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@Profile, "Error During Store Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutAlert() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to logout from the application?")
            .setCancelable(false)
            .setPositiveButton(
                "Logout",
                DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                    FIRE_db!!.collection(FireTag.FireUser)
                        .document(user!!.getUid())
                        .update("fcm_TOKEN", FieldValue.delete())
                        .addOnSuccessListener(OnSuccessListener({ unused: Void? ->
                            AuthUI.getInstance().signOut(this@Profile)
                        }))
                    dialog.dismiss()
                }
            ).setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener({ dialog: DialogInterface, i: Int -> dialog.dismiss() })
            )
        dialogBuilder.create().show()
    }

    public override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            val intent: Intent = Intent(this, Flash::class.java)
            startActivity(intent)
            finish()
        } else {
            firebaseAuth.getCurrentUser()?.let { HistoryCycle(it) }
        }
    }

    var SellerId: String? = null
    var ProductId: String? = null
    override fun onStart() {
        super.onStart()
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(getIntent())
            .addOnSuccessListener(
                this,
                OnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                    // Get deep link from result (may be null if no link is found)
                    val deepLink: Uri?
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink()
                        if (deepLink != null) {
                            SellerId = deepLink.getQueryParameter("sellerid")
                            ProductId = deepLink.getQueryParameter("productid")
                            if (SellerId != null) {
                                Log.d(Mtag.Tag, "onSu96ccess: " + SellerId)
                                val intent1: Intent = Intent(this@Profile, FoodBuy::class.java)
                                intent1.putExtra("SellerIDFromSharableLink", SellerId)
                                startActivity(intent1)
                                finish()
                            } else if (ProductId != null) {
                                Log.d(Mtag.Tag, "onSu96ccess: " + ProductId)
                                val intent1: Intent = Intent(this@Profile, SellConform::class.java)
                                intent1.putExtra("FIRE_STORE_DOCUMENT", ProductId)
                                startActivity(intent1)
                                finish()
                            }
                        }
                    }
                }
            )
            .addOnFailureListener(
                this,
                OnFailureListener({ e: Exception? ->
                    Log.w(
                        Mtag.Tag,
                        "getDynamicLink:onFailure",
                        e
                    )
                })
            )
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        if (historyAdapter != null) {
            historyAdapter!!.stopListening()
        }
    }

    private fun HistoryCycle(user: FirebaseUser) {
        val query: Query = FirebaseFirestore.getInstance()
            .collection(FireTag.FireUser)
            .document(user.getUid())
            .collection(FireTag.FireHistory)
        val options: FirestoreRecyclerOptions<BUY_HISTORY> =
            FirestoreRecyclerOptions.Builder<BUY_HISTORY>()
                .setQuery(query, BUY_HISTORY::class.java)
                .build()
        historyAdapter = FIRE_db?.let { HistoryAdapter(options, this, it) }
        recyclerView!!.setAdapter(historyAdapter)
        historyAdapter!!.startListening()
    }

    private fun citySearch(s: String?): Boolean {
        Log.d(Mtag.Tag, "citySearch: " + s)
        for (city: String in CityName.list) {
            if ((s == city)) {
                return false
            }
        }
        return true
    }


    fun LATLANGtoCITYName() {
        geocodingISon = true
        dial_submit_button!!.setVisibility(View.GONE)
        Toast.makeText(this, "Please Wait we Update City", Toast.LENGTH_SHORT).show()
        dial_cityName!!.setText("Please Wait We Update City")
        Log.d(Mtag.Tag, url)
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null, { response: JSONObject ->
                try {
                    val arr: JSONArray = response.getJSONArray("features")
                    Log.d(Mtag.Tag, "12333onResponse: " + arr)
                    cityName = arr.getJSONObject(0).getString("text")
                    if ((cityName == "")) {
                        Toast.makeText(
                            this@Profile,
                            "Please Select city correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val s: String = "Updated City : $cityName"
                        dial_cityName!!.setText(s)
                    }
                    Log.d(Mtag.Tag, "12333onResponse: $arr")
                } catch (e: JSONException) {
                    Log.d(Mtag.Tag, "onResponse: 287")
                    Toast.makeText(this@Profile, "Please Select city correctly", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
                geocodingISon = false
                dial_submit_button!!.setVisibility(View.VISIBLE)
            },
            { error: VolleyError ->
                Log.d(Mtag.Tag, "onErrorResponse: " + error.toString())
                geocodingISon = false
                dial_submit_button!!.setVisibility(View.VISIBLE)
            }
        )
        requestQueue.add(jsonObjectRequest)
    }
}