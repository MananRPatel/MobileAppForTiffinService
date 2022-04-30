package com.mananhirak.annadata.activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FoodSell extends AppCompatActivity {


    EditText et_foodname,ed_foodweight,ed_foodprice,ed_totalfood,ed_foodinfo;
    Button btn_submit,btn_time,btn_date,button4;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Calendar calendar;
    int Hour,Minute;
    String Food_Time;
    boolean Time_Flag;
    int Day,Month,Year;
    String Food_Date;
    boolean Date_Flag;
    FirebaseFirestore FIRE_db;
    FirebaseStorage FIRE_ST;
    FirebaseUser user;
    StorageReference storageReference;
    Bitmap bitmapMain;
    ImageView imageView,photo;
    Dialog dialog;
    LinearLayout linearLayout_time, linearLayout_image;
    MARKET marketMain;



    private void IdSaver(){

        et_foodname =findViewById(R.id.act_f_s_foodnamename);
        ed_foodweight=findViewById(R.id.act_f_s_foodweightname);
        ed_foodprice=findViewById(R.id.act_f_s_foodpricename);
        ed_totalfood=findViewById(R.id.act_f_s_total_foodname);
        ed_foodinfo=findViewById(R.id.act_f_s_foodallinfoname);

        btn_submit=findViewById(R.id.act_f_s_submit);
        btn_time=findViewById(R.id.act_f_s_foodtimename);
        btn_date=findViewById(R.id.act_f_s_fooddatename);

        photo=findViewById(R.id.act_f_s__photobutton);

        linearLayout_time=findViewById(R.id.act_f_s_timedateb);
        linearLayout_image=findViewById(R.id.act_f_s_layout);
        imageView = findViewById(R.id.act_f_s__foodphoto);


    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_sell);

        Objects.requireNonNull(getSupportActionBar()).hide();
        // Remove after Testing App
        bitmapMain= BitmapFactory.decodeResource(this.getResources(), R.drawable.bg3);

        FIRE_db= FirebaseFirestore.getInstance();
        FIRE_ST=FirebaseStorage.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        IdSaver();

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        imageView.setVisibility(View.GONE);

        calendar=Calendar.getInstance();
        Hour=calendar.get(Calendar.AM_PM)==Calendar.PM?calendar.get(Calendar.HOUR)+12:calendar.get(Calendar.HOUR);
        Minute=calendar.get(Calendar.MINUTE);


        CharSequence charSequence= DateFormat.format("hh:mm a",calendar);
        Food_Time=(String)charSequence;
        Log.d(Mtag.Tag,"1->"+Food_Time);

        Day=calendar.get(Calendar.DATE);
        Month=calendar.get(Calendar.MONTH);
        Year=calendar.get(Calendar.YEAR);

        CharSequence charSequence1= DateFormat.format("EEE, dd MMM  yyyy",calendar);
        Food_Date=(String)charSequence1;

        Time_Flag=false;
        Date_Flag=true;


        btn_time.setOnClickListener(view -> Time_Create());

        btn_date.setOnClickListener(view -> Date_Create());

        photo.setOnClickListener(view -> {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null){
                startActivityForResult(intent,2721);
            }
        });



        btn_submit.setOnClickListener(view -> {
            String s1= et_foodname.getText().toString();
            String s2=ed_foodweight.getText().toString();
            String s3=ed_foodprice.getText().toString();
            String s4=ed_totalfood.getText().toString();
            String s5=ed_foodinfo.getText().toString();

            if(s1.isEmpty()){
                et_foodname.setError("Please Enter Food Name..");
                et_foodname.requestFocus();
            }else if(s2.isEmpty()){
                ed_foodweight.setError("Please Enter Food Weight..");
                ed_foodweight.requestFocus();
            }else if(s3.isEmpty()){
                ed_foodprice.setError("Please Enter Food Value..");
                ed_foodprice.requestFocus();
            }else if(s4.isEmpty()){
                ed_totalfood.setError("Please Put Number Of Order");
                ed_totalfood.requestFocus();
            } else if(s5.isEmpty()){
                ed_foodinfo.setError("Please Enter Food Information..");
                ed_foodinfo.requestFocus();
            }else if(Time_Flag&&Date_Flag){
                Time_Create();
                Toast.makeText(FoodSell.this,"Future Time"+Food_Time+"  "+Food_Date,Toast.LENGTH_SHORT).show();
            }else if(bitmapMain==null){
                Toast.makeText(FoodSell.this, "Please Insert Photo", Toast.LENGTH_SHORT).show();
            }else{

                imageView.setVisibility(View.GONE);

                btn_submit.setVisibility(View.GONE);
                linearLayout_time.setVisibility(View.GONE);
                linearLayout_image.setVisibility(View.GONE);


                dialog = new Dialog(this);
                dialog.setContentView(R.layout.dial_set_animation);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.create();
                LottieAnimationView lottieAnimationView=dialog.findViewById(R.id.dial_sa_upload);
                lottieAnimationView.setVisibility(View.VISIBLE);
                dialog.show();


                marketMain = new MARKET();
                marketMain.setRETAILER_ID(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                marketMain.setFOOD_NAME(s1);
                marketMain.setFOOD_POINT(0);
                marketMain.setFOOD_PRICE(Integer.parseInt(s3));
                marketMain.setFOOD_WEIGHT(Integer.parseInt(s2));
                marketMain.setTOTAL_FOOD(Integer.parseInt(s4));
                marketMain.setFOOD_INFO(s5);
                marketMain.setFOOD_DATE(Food_Date);
                marketMain.setFOOD_TIME(Food_Time);
                marketMain.setNO_FOOD_LEFT(false);
                marketMain.setLEFT_ORDER(marketMain.getTOTAL_FOOD());

                Log.d(Mtag.Tag, "onCreate: "+"START WORK");
                FIRE_db.collection(FireTag.FireUser).document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Log.d(Mtag.Tag, "onCreate: "+"START 2 WORK");
                            marketMain.setCITY_NAME((String) documentSnapshot.get("address"));
                            Log.d(Mtag.Tag, "onCreate: "+"START 3 WORK");
                            FIRE_db.collection(FireTag.FireMarket).add(marketMain)
                                    .addOnSuccessListener(this::SetLinkAndTiffin);
                        });
            }
        });
    }

    public  void SetLinkAndTiffin(DocumentReference documentReference){
        FoodPhotoUpload(documentReference);
        SetShortLink(documentReference);
    }

    public void SetShortLink(DocumentReference documentReference){

        Log.d(Mtag.Tag, "FoodSell: 259");
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://sites.google.com/view/annadata/?productid="+documentReference.getId()))
                .setDomainUriPrefix("https://annadata.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://github.com/MananRPatel"))
                        .build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Manan R Patel")
                        .setDescription("Annnadata - Tiffin Service App \nThis is Tiffin Service Application to Sell and Buy Tiffin Online")
                        .setImageUrl(Uri.parse("https://avatars.githubusercontent.com/u/67188104?v=4"))
                        .build()
                )
                .buildDynamicLink();
        Uri SellerTiffinLink = dynamicLink.getUri();

        JSONObject longlink = new JSONObject();
        JSONObject suffix = new JSONObject();
        try {
            longlink.put("longDynamicLink",(SellerTiffinLink));
            suffix.put("option","SHORT");
            longlink.put("suffix",suffix);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Mtag.Tag, "onSuccessjson: "+longlink+"\n"+ SellerTiffinLink);

        String dynamicToShortURL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=addyourkey";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,dynamicToShortURL,longlink, response -> {
            Log.d(Mtag.Tag, "onResponse: "+response);
            try {
                documentReference.update("tiffin_LINK",(String)response.get("shortLink"));

            } catch (JSONException e) {
                Log.d(Mtag.Tag, "onResponse: "+response);
                e.printStackTrace();
            }
        }, error -> Log.d(Mtag.Tag, "NowSuiiuiuend: " + error)){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String>  map =new HashMap<>();
                map.put("Content-type","application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(FoodSell.this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    public void Time_Create(){
        TimePickerDialog timePickerDialog=new TimePickerDialog(FoodSell.this, (timePicker, i, i1) -> {
            Time_Flag= (Hour < i) || (Hour == i && Minute < i1);

            Calendar calendar1=Calendar.getInstance();
            calendar1.set(Calendar.HOUR,(i+12)%24);
            calendar1.set(Calendar.MINUTE,i1);

            CharSequence charSequence= DateFormat.format("hh:mm a",calendar1);
            Food_Time=(String)charSequence;
            Log.d(Mtag.Tag,"2->"+Food_Time);

        },Hour,Minute,true);
        timePickerDialog.show();
    }

    public void Date_Create(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, (datePicker, i, i1, i2) -> {

            Date_Flag=!(Day>i2);

            Calendar calendar1=Calendar.getInstance();
            calendar1.set(Calendar.YEAR,i);
            calendar1.set(Calendar.MONTH,i1);
            calendar1.set(Calendar.DATE,i2);
            CharSequence charSequence= DateFormat.format("EEE, dd MMM  yyyy",calendar1);
            Food_Date=(String)charSequence;

        },Year,Month,Day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000*60*60*24);
        datePickerDialog.show();

    }


    public long FutureDateFormat(){

        Calendar calendar1=Calendar.getInstance();
        return 10000L *calendar1.get(Calendar.YEAR)+100*(1+calendar1.get(Calendar.MONTH))+calendar1.get(Calendar.DATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2721){
            switch(resultCode){
                case RESULT_OK:
                    assert data != null;
                    bitmapMain= (Bitmap) data.getExtras().get("data");
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmapMain);
                    Log.d(Mtag.Tag, "onActivityResult: ");
                    break;
                case RESULT_CANCELED:
                    Log.d(Mtag.Tag, "onActivityResult: "+"NO PHOTO");
                    break;
                default:
                    Log.d(Mtag.Tag, "onActivityResult: "+"NOT");
            }
        }
    }


    void FoodPhotoUpload(final DocumentReference marketDocREF){
        Log.d(Mtag.Tag, "onCreate: "+"START 4 WORK");
        Toast.makeText(FoodSell.this,Food_Date+"  "+Food_Time,Toast.LENGTH_SHORT).show();

        storageReference = FIRE_ST.getReference()
                .child(user.getUid())
                .child("account"+System.currentTimeMillis()+"food.jpg");

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmapMain.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);

        storageReference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(taskSnapshot -> GetPhotoURL(storageReference,marketDocREF));
    }

    void GetPhotoURL(StorageReference storageReference, final DocumentReference marketDocREF){
        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                   marketDocREF
                           .update("photo_URL",uri.toString());
                    Log.d(Mtag.Tag, "onSuccess: "+uri);
                    Toast.makeText(FoodSell.this, "Thanks For Uploading Pic", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Intent intent =new Intent(FoodSell.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                });
    }




}