package com.manan.annadata.activitys

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.MARKET
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class FoodSell  : AppCompatActivity() {
    var et_foodname: EditText? = null
    var ed_foodweight: EditText? = null
    var ed_foodprice: EditText? = null
    var ed_totalfood: EditText? = null
    var ed_foodinfo: EditText? = null
    var btn_submit: Button? = null
    var btn_time: Button? = null
    var btn_date: Button? = null

    var radioGroup:RadioGroup?=null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    var calendar: Calendar? = null
    var Hour: Int = 0
    var Minute: Int = 0
    var Food_Time: String? = null
    var Time_Flag: Boolean = false
    var Day: Int = 0
    var Month: Int = 0
    var Year: Int = 0
    var Food_Date: String? = null
    var Date_Flag: Boolean = false
    var FIRE_db: FirebaseFirestore? = null
    var FIRE_ST: FirebaseStorage? = null
    var user: FirebaseUser? = null
    var storageReference: StorageReference? = null
    var bitmapMain: Bitmap? = null
    var imageView: ImageView? = null
    var dialog: Dialog? = null
    var linearLayout_time: LinearLayout? = null
    var photo: LinearLayout? = null
    var marketMain: MARKET? = null
    private fun IdSaver() {
        et_foodname = findViewById(R.id.act_f_s_foodnamename)
        ed_foodweight = findViewById(R.id.act_f_s_foodweightname)
        ed_foodprice = findViewById(R.id.act_f_s_foodpricename)
        ed_totalfood = findViewById(R.id.act_f_s_total_foodname)
        ed_foodinfo = findViewById(R.id.act_f_s_foodallinfoname)
        btn_submit = findViewById(R.id.act_f_s_submit)
        btn_time = findViewById(R.id.act_f_s_foodtimename)
        btn_date = findViewById(R.id.act_f_s_fooddatename)
        photo = findViewById(R.id.act_f_s__photobutton)
        linearLayout_time = findViewById(R.id.act_f_s_timedateb)
        imageView = findViewById(R.id.act_f_s__foodphoto)
        radioGroup = findViewById(R.id.sellerAct_radio_group)
    }

    private var activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            imageView!!.setImageBitmap( it.data?.extras?.get("data") as Bitmap?)
            imageView!!.visibility = (View.VISIBLE)

        }
    }
    private val requestCamera =
        registerForActivityResult((ActivityResultContracts.RequestPermission())) {
            if (it) {
                activityResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                Toast.makeText(
                    this@FoodSell,
                    "Please give Camera Access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_sell)
        Objects.requireNonNull(getSupportActionBar())?.hide()
        // Remove after Testing App
        bitmapMain = BitmapFactory.decodeResource(getResources(), R.drawable.bg3)
        FIRE_db = FirebaseFirestore.getInstance()
        FIRE_ST = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().getCurrentUser()
        IdSaver()
        val ivBack: ImageView = findViewById(R.id.ivBack)
        ivBack.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                finish()
            }
        })

        imageView!!.setVisibility(View.GONE)
        calendar = Calendar.getInstance()
        Hour =
            if (calendar!!.get(Calendar.AM_PM) == Calendar.PM) calendar!!.get(Calendar.HOUR).plus(12) else calendar!!.get(
                Calendar.HOUR
            )
        Minute = calendar!!.get(Calendar.MINUTE)
        val charSequence: CharSequence = DateFormat.format("hh:mm a", calendar)
        Food_Time = charSequence as String?
        Log.d(Mtag.Tag, "1->$Food_Time")
        Day = calendar!!.get(Calendar.DATE)
        Month = calendar!!.get(Calendar.MONTH)
        Year = calendar!!.get(Calendar.YEAR)
        val charSequence1: CharSequence = DateFormat.format("EEE, dd MMM  yyyy", calendar)
        Food_Date = charSequence1 as String?
        Time_Flag = false
        Date_Flag = true
        btn_time!!.setOnClickListener(View.OnClickListener { view: View? -> Time_Create() })
        btn_date!!.setOnClickListener(View.OnClickListener { view: View? -> Date_Create() })
        photo!!.setOnClickListener(View.OnClickListener { requestCamera.launch((android.Manifest.permission.CAMERA)) })
        btn_submit!!.setOnClickListener(View.OnClickListener { view: View? ->
            val s1: String = et_foodname!!.getText().toString()
            val s2: String = ed_foodweight!!.getText().toString()
            val s3: String = ed_foodprice!!.getText().toString()
            val s4: String = ed_totalfood!!.getText().toString()
            val s5: String = ed_foodinfo!!.getText().toString()
            if (s1.isEmpty()) {
                et_foodname!!.setError("Please Enter Food Name..")
                et_foodname!!.requestFocus()
            } else if (s2.isEmpty()) {
                ed_foodweight!!.setError("Please Enter Food Weight..")
                ed_foodweight!!.requestFocus()
            } else if (s3.isEmpty()) {
                ed_foodprice!!.setError("Please Enter Food Value..")
                ed_foodprice!!.requestFocus()
            } else if (s4.isEmpty()) {
                ed_totalfood!!.setError("Please Put Number Of Order")
                ed_totalfood!!.requestFocus()
            } else if (s5.isEmpty()) {
                ed_foodinfo!!.setError("Please Enter Food Information..")
                ed_foodinfo!!.requestFocus()
            } else if (Time_Flag && Date_Flag) {
                Time_Create()
                Toast.makeText(
                    this@FoodSell,
                    "Future Time" + Food_Time + "  " + Food_Date,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (bitmapMain == null) {
                Toast.makeText(this@FoodSell, "Please Insert Photo", Toast.LENGTH_SHORT).show()
            } else {
                imageView!!.setVisibility(View.GONE)
                btn_submit!!.setVisibility(View.GONE)
                linearLayout_time!!.setVisibility(View.GONE)
                photo!!.setVisibility(View.GONE)
                dialog = Dialog(this)
                dialog!!.setContentView(R.layout.dial_set_animation)
                dialog!!.setCancelable(false)
                dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
                dialog!!.create()
                val lottieAnimationView: LottieAnimationView =
                    dialog!!.findViewById(R.id.dial_sa_upload)
                lottieAnimationView.setVisibility(View.VISIBLE)
                dialog!!.show()
                marketMain = MARKET()
                marketMain!!.RETAILER_ID =
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())?.getUid()
                marketMain!!.FOOD_NAME = s1
                marketMain!!.FOOD_POINT = 0
                marketMain!!.FOOD_PRICE = s3.toInt()
                marketMain!!.FOOD_WEIGHT = s2.toInt()
                marketMain!!.TOTAL_FOOD = s4.toInt()
                marketMain!!.FOOD_INFO = s5
                marketMain!!.FOOD_DATE = Food_Date
                marketMain!!.FOOD_TIME = Food_Time
                marketMain!!.NO_FOOD_LEFT = false
                marketMain!!.LEFT_ORDER = marketMain!!.TOTAL_FOOD


                marketMain!!.VEG = when (radioGroup?.checkedRadioButtonId) {
                    R.id.sellerAct_veg -> true
                    else -> false
                }

                Log.d(Mtag.Tag, "onCreate: " + "START WORK")
                FIRE_db!!.collection(FireTag.FireUser).document(user!!.getUid()).get()
                    .addOnSuccessListener(OnSuccessListener { documentSnapshot: DocumentSnapshot ->
                        Log.d(Mtag.Tag, "onCreate: " + "START 2 WORK")
                        marketMain!!.CITY_NAME = documentSnapshot.get("address") as String?
                        Log.d(Mtag.Tag, "onCreate: " + "START 3 WORK")
                        FIRE_db!!.collection(FireTag.FireMarket).add(marketMain!!)
                            .addOnSuccessListener(OnSuccessListener({ documentReference: DocumentReference ->
                                SetLinkAndTiffin(
                                    documentReference
                                )
                            }))
                    })
            }
        })
    }

    fun SetLinkAndTiffin(documentReference: DocumentReference) {
        SetShortLink(documentReference)
    }

    fun SetShortLink(documentReference: DocumentReference) {
        Log.d(Mtag.Tag, "FoodSell: 259")
        val dynamicLink: DynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://sites.google.com/view/annadata/?productid=" + documentReference.getId()))
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
            Response.Listener { response: JSONObject ->
                Log.d(Mtag.Tag, "onResponse LInk: " + response)
                try {
                    Log.d(Mtag.Tag, "onResponse LInk: " + response)
                    documentReference.update("tiffin_LINK", response.get("shortLink") as String?)
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            public override fun onSuccess(unused: Void?) {
                                FoodPhotoUpload(documentReference)
                            }
                        })
                } catch (e: JSONException) {
                    Log.d(Mtag.Tag, "onResponse: " + response)
                    e.printStackTrace()
                }
            },
            Response.ErrorListener({ error: VolleyError ->
                Log.d(
                    Mtag.Tag,
                    "NowSuiiuiuend: " + error
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
        val requestQueue: RequestQueue = Volley.newRequestQueue(this@FoodSell)
        request.setRetryPolicy(
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        requestQueue.add(request)
    }

    fun Time_Create() {
        val timePickerDialog: TimePickerDialog = TimePickerDialog(
            this@FoodSell,
            OnTimeSetListener { timePicker: TimePicker?, i: Int, i1: Int ->
                Time_Flag = (Hour < i) || (Hour == i && Minute < i1)
                val calendar1: Calendar = Calendar.getInstance()
                calendar1.set(Calendar.HOUR, (i + 12) % 24)
                calendar1.set(Calendar.MINUTE, i1)
                val charSequence: CharSequence = DateFormat.format("hh:mm a", calendar1)
                Food_Time = charSequence as String?
                Log.d(Mtag.Tag, "2->" + Food_Time)
            },
            Hour,
            Minute,
            true
        )
        timePickerDialog.show()
    }

    fun Date_Create() {
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            this,
            OnDateSetListener { datePicker: DatePicker?, i: Int, i1: Int, i2: Int ->
                Date_Flag = !(Day > i2)
                val calendar1: Calendar = Calendar.getInstance()
                calendar1.set(Calendar.YEAR, i)
                calendar1.set(Calendar.MONTH, i1)
                calendar1.set(Calendar.DATE, i2)
                val charSequence: CharSequence = DateFormat.format("EEE, dd MMM  yyyy", calendar1)
                Food_Date = charSequence as String?
            },
            Year,
            Month,
            Day
        )
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())
        datePickerDialog.getDatePicker()
            .setMinDate(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
        datePickerDialog.show()
    }

    fun FutureDateFormat(): Long {
        val calendar1: Calendar = Calendar.getInstance()
        return (10000L * calendar1.get(Calendar.YEAR)) + (100 * (1 + calendar1.get(Calendar.MONTH))) + calendar1.get(
            Calendar.DATE
        )
    }



    fun FoodPhotoUpload(marketDocREF: DocumentReference) {
        Log.d(Mtag.Tag, "onCreate: " + "START 4 WORK")
        Toast.makeText(this@FoodSell, Food_Date + "  " + Food_Time, Toast.LENGTH_SHORT).show()
        storageReference = FIRE_ST!!.getReference()
            .child(user!!.getUid())
            .child("account" + System.currentTimeMillis() + "food.jpg")
        val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmapMain!!.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        storageReference!!.putBytes(byteArrayOutputStream.toByteArray())
            .addOnSuccessListener(OnSuccessListener({ taskSnapshot: UploadTask.TaskSnapshot? ->
                GetPhotoURL(
                    storageReference!!, marketDocREF
                )
            }))
    }

    fun GetPhotoURL(storageReference: StorageReference, marketDocREF: DocumentReference) {
        storageReference.getDownloadUrl()
            .addOnSuccessListener(OnSuccessListener({ uri: Uri ->
                marketDocREF
                    .update("photo_URL", uri.toString())
                Log.d(Mtag.Tag, "onSuccess: " + uri)
                Toast.makeText(this@FoodSell, "Thanks For Uploading Pic", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
                val intent: Intent = Intent(this@FoodSell, MainActivity::class.java)
                startActivity(intent)
                finish()
            }))
    }
}