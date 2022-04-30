package com.mananhirak.annadata.activitys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activityshelpers.CityName;
import com.mananhirak.annadata.activityshelpers.HeyToaster;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.foodcycle.HistoryAdapter;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    TextView showMoney, t2, showUserName,showCityName, dial_cityName;
    HistoryAdapter historyAdapter;
    RecyclerView recyclerView;
    ImageView imageView, ivLogout, dial_userPic;
    ImageButton myTiffinLink, myTiffinBanner, settingProfile, dial_camera, dial_location;
    ImageView ivBack;
    Button dial_close_button, dial_submit_button;
    MapFragment dial_mapFragment;
    Dialog dialogToRating, dialogToChangeProfile;
    EditText dial_username;
    boolean geocodingISon = false;
    GeoPoint geoPoint;
    Bitmap dial_bitmap = null;
    DocumentSnapshot userData;
    FOOD_USERS foodUsers;
    String cityName = "";
    FirebaseStorage Fire_ST;
    StorageReference storageReference;
    boolean shouldIFinish = true;


    private void IdSaver() {
        recyclerView = findViewById(R.id.rcycle_p_historycycle);
        showMoney = findViewById(R.id.act_p_money);
//        t2 = findViewById(R.id.prodonate);
        showUserName = findViewById(R.id.act_p_user);
        showCityName = findViewById(R.id.act_p_user_location);

        ivLogout = findViewById(R.id.logout);
        imageView = findViewById(R.id.act_p_userpic);

        myTiffinLink = findViewById(R.id.act_p_user_tiffinlink);
        myTiffinBanner = findViewById(R.id.act_p_user_tiffinbannerdownload);
        settingProfile = findViewById(R.id.act_p_setting);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();

        FIRE_db = FirebaseFirestore.getInstance();
        Fire_ST = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // hirak
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(view -> finish());

        IdSaver();

        dialogToChangeProfile = new Dialog(Profile.this);
        dialogToChangeProfile.setContentView(R.layout.dial_changeprofile);
        dialogToChangeProfile.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogToChangeProfile.setCancelable(false);
        dialogToChangeProfile.create();


        FIRE_db.collection(FireTag.FireUser)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    userData = documentSnapshot;

                    showCityName.setText((String)userData.get("address"));

                    foodUsers = documentSnapshot.toObject(FOOD_USERS.class);

                    myTiffinLink.setOnClickListener(view -> SETSellerLink());

                    myTiffinBanner.setOnClickListener(view -> SETSellerBanner());


                    Glide.with(Profile.this)
                            .load(foodUsers.getPHOTO_URL())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    settingProfile.setOnClickListener(view -> {
                                        dialogToChangeProfile.show();
                                        SetUpProfileData();
                                    });
                                    return false;
                                }
                            })
                            .into(imageView);
                });

        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireDelivery)
                .whereEqualTo("conform_DELIVERY", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                    int TotalMoney = 0;
                    int TotalPoint = 0;
                    for (DocumentSnapshot snap : snapshotList) {
                        DELIVERY_BOY deliveryBoy = snap.toObject(DELIVERY_BOY.class);
                        assert deliveryBoy != null;
                        TotalMoney += (deliveryBoy.getFOOD_MONEY()) * (deliveryBoy.getTOTAL_ORDER());
                        TotalPoint += (deliveryBoy.getFOOD_POINT()) * (deliveryBoy.getTOTAL_ORDER());
                    }

                    String s1 = "₹" + TotalMoney;
                    showMoney.setText(s1);

                    String s2 = "" + TotalPoint;
//                    t2.setText(s2);

                });

        showUserName.setText(user.getDisplayName());






        ivLogout.setOnClickListener(view -> showLogoutAlert());

    }

    void SetUpProfileData() {

        dial_userPic = dialogToChangeProfile.findViewById(R.id.dial_cp_userpic);
        dial_username = dialogToChangeProfile.findViewById(R.id.dial_cp_username);
        dial_cityName = dialogToChangeProfile.findViewById(R.id.dial_cp_cityname);
        dial_camera = dialogToChangeProfile.findViewById(R.id.dial_change_photo);
        dial_location = dialogToChangeProfile.findViewById(R.id.dial_cp_sign_map);
        dial_close_button = dialogToChangeProfile.findViewById(R.id.dial_cp_close);
        dial_submit_button = dialogToChangeProfile.findViewById(R.id.dial_cp_submit);

        SetProfileChangeData();

    }

    private void SetProfileChangeData() {

        dial_bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.fooddoante);

        Glide.with(Profile.this)
                .load(foodUsers.getPHOTO_URL())
                .into(dial_userPic);

        dial_username.setText(foodUsers.getUSER_NAME());

        String s = "Current City : " + foodUsers.getADDRESS();
        showCityName.setText(foodUsers.getADDRESS());
        dial_cityName.setText(s);
        cityName= foodUsers.getADDRESS();

        geoPoint = foodUsers.getUSER_LOCATION();

        dial_mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.dial_cp_google_map);
        if (dial_mapFragment != null) {
            dial_mapFragment.getMapAsync(googleMap -> {
                while (geocodingISon) ;

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), 10));

                googleMap.setOnMapClickListener(latLng -> {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Marker Set On Your City");

                    googleMap.clear();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    googleMap.addMarker(markerOptions);
                    geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
                    if (!geoPoint.equals(foodUsers.getUSER_LOCATION())) {
                        LATLANGtoCITYName();
                    }

                });
            });
        }

        dial_close_button.setOnClickListener(view -> dialogToChangeProfile.dismiss());

        dial_camera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(Profile.this.getPackageManager()) != null)
                (Profile.this).startActivityForResult(intent, 7145);
        });


        dial_submit_button.setOnClickListener(view -> SetProfileData());


    }

    private void SetProfileData() {

        if (dial_username.getText().toString().isEmpty()) {
            dial_username.setFocusable(true);
            dial_username.setError("Please Add UserName");
            return;
        }
        if (citySearch(cityName)) {
            Toast.makeText(this, "Sorry We Don't Work on this Area\nPlease Select Other City", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();

        if (!geoPoint.equals(foodUsers.getUSER_LOCATION())) {
            map.put("user_LOCATION", geoPoint);
        }
        if (!cityName.equals(foodUsers.getADDRESS())) {
            map.put("address", cityName);
        }
        if (!foodUsers.getUSER_NAME().equals(dial_username.getText().toString())) {
            map.put("user_NAME", dial_username.getText().toString());
            shouldIFinish = false;
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(dial_username.getText().toString())
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnSuccessListener(unused -> {
                        shouldIFinish = true;
                        if (dial_bitmap != null) {
                            shouldIFinish = false;
                            SetProfileImage();
                        } else {
                            shouldIFinish = false;
                            dialogToChangeProfile.dismiss();
                            Profile.this.finish();
                        }
                    });
        }
        userData.getReference().update(map)
                .addOnSuccessListener(unused -> {
                    if (shouldIFinish) {
                        shouldIFinish = false;
                        dialogToChangeProfile.dismiss();
                        Profile.this.finish();
                    }
                });

    }

    private void SetProfileImage() {

        storageReference = Fire_ST.getReference()
                .child(user.getUid())
                .child("User_Profile_Pic");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        dial_bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);

        storageReference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(Profile.this, "Thanks For Uploading Pic", Toast.LENGTH_SHORT).show();
                    GetUrlOfUserPic();
                });

    }

    private void GetUrlOfUserPic() {
        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> userData.getReference()
                        .update("photo_URL", uri.toString())
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(Profile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            dialogToChangeProfile.dismiss();
                            Profile.this.finish();
                        }));
    }

    private void SETSellerLink() {
        Log.d(Mtag.Tag, "SETSellerLink: 162");

        String tiffinLink = foodUsers.getTIFFIN_LINK();

        if (tiffinLink != null)

            SetSellerTiffinLink(Uri.parse(tiffinLink));

        else {

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://sites.google.com/view/annadata/?sellerid=" + user.getUid() + ""))
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
                longlink.put("longDynamicLink", (SellerTiffinLink));
                suffix.put("option", "SHORT");
                longlink.put("suffix", suffix);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(Mtag.Tag, "onSuccessjson: " + longlink + "\n" + SellerTiffinLink);

            String dynamicToShortURL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=addkey";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, dynamicToShortURL, longlink, response -> {
                Log.d(Mtag.Tag, "onResponse: " + response);
                try {
                    String shortlink = (String) response.get("shortLink");
                    userData.getReference().update("tiffin_LINK", shortlink)
                            .addOnSuccessListener(unused -> SetSellerTiffinLink(Uri.parse(shortlink)));


                } catch (JSONException e) {
                    Log.d(Mtag.Tag, "onResponse: " + response);
                    e.printStackTrace();
                }

            }, error -> Log.d(Mtag.Tag, "NowSuiiuiuend: " + error)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> map = new HashMap<>();

                    map.put("Content-type", "application/json");
                    return map;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
            request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);


        }


    }


    private void SETSellerBanner() {

        File path = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + File.separator + "Annadata" + File.separator + "Annadata_Seller_" + user.getUid() + ".jpg");

        try {
            FileInputStream streamIn;
            //For Check file is present in Folder or Not
            streamIn = new FileInputStream(path);
            BitmapFactory.decodeStream(streamIn);

            Uri screenshotUri = FileProvider.getUriForFile(Profile.this, Profile.this.getApplicationContext().getPackageName() + ".provider", path);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            sendIntent.setType("image/*");
            sendIntent.setClipData(ClipData.newRawUri("", screenshotUri));
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sendIntent, "Hello World"));

        } catch (FileNotFoundException e) {

            Log.d(Mtag.Tag, "SETSELLERBANNER: 162");

            String tiffinLink = foodUsers.getTIFFIN_LINK();

            if (tiffinLink != null)
                CreateImage(Uri.parse(tiffinLink));

            else {

                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://sites.google.com/view/annadata/?sellerid=" + user.getUid() + ""))
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
                    longlink.put("longDynamicLink", (SellerTiffinLink));
                    suffix.put("option", "SHORT");
                    longlink.put("suffix", suffix);
                } catch (JSONException e1) {
                    e.printStackTrace();
                }

                Log.d(Mtag.Tag, "onSuccessjson: " + longlink + "\n" + SellerTiffinLink);

                String dynamicToShortURL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=addyourkey";

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, dynamicToShortURL, longlink, response -> {
                    Log.d(Mtag.Tag, "onResponse: " + response);
                    try {
                        String shortlink = (String) response.get("shortLink");
                        userData.getReference().update("tiffin_LINK", shortlink)
                                .addOnSuccessListener(unused -> CreateImage(Uri.parse(shortlink)));

                    } catch (JSONException e1) {
                        Log.d(Mtag.Tag, "onResponse: " + response);
                        e.printStackTrace();
                    }

                }, error -> Log.d(Mtag.Tag, "NowSuiiuiuend: " + error)) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> map = new HashMap<>();

                        map.put("Content-type", "application/json");


                        return map;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
                request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(request);

            }

        }


    }

    private void SetSellerTiffinLink(Uri shortLink) {

        Log.d(Mtag.Tag, "Short link12 " + shortLink);


        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sendIntent, "Hello World"));


    }

    private void CreateImage(Uri shortLink) {

        dialogToRating = new Dialog(this);
        dialogToRating.setContentView(R.layout.banner_qrcode_sellertiffin);
        dialogToRating.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogToRating.create();
        dialogToRating.show();

        TextView sellername = dialogToRating.findViewById(R.id.banner_s_sellername);
        TextView tiffinlink = dialogToRating.findViewById(R.id.banner_s_link_extra);
        ConstraintLayout constraintLayout = dialogToRating.findViewById(R.id.banner_layout);
        ImageView tiffinqrcode = dialogToRating.findViewById(R.id.banner_s_qrcode);
        Button qrbutton = dialogToRating.findViewById(R.id.banner_s_button);

        qrbutton.setVisibility(View.GONE);
        sellername.setText(user.getDisplayName());
        tiffinlink.setText(shortLink.toString());

        Uri qrcodelink = Uri.parse("https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl=" + shortLink + "&choe=UTF-8");

        Glide.with(this)
                .load(qrcodelink)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(Mtag.Tag, "onLoadFailed: " + "QRFAIL");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        qrbutton.setVisibility(View.VISIBLE);
                        Log.d(Mtag.Tag, "onLoadFailed: " + "QRSUCESS");
                        return false;
                    }
                })
                .into(tiffinqrcode);


        qrbutton.setOnClickListener(view -> {
            Log.d(Mtag.Tag, "CreateImage: " + constraintLayout.getHeight() + " " + constraintLayout.getWidth());

            Bitmap returnedBitmap = Bitmap.createBitmap(constraintLayout.getWidth(), constraintLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = constraintLayout.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            constraintLayout.draw(canvas);


            File path = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + File.separator + "Annadata" + File.separator + "Annadata_Seller_" + user.getUid() + ".jpg");

            try {
                FileInputStream streamIn;
                //For Check file is present in Folder or Not
                streamIn = new FileInputStream(path);
                BitmapFactory.decodeStream(streamIn);

                Uri screenshotUri = FileProvider.getUriForFile(Profile.this, Profile.this.getApplicationContext().getPackageName() + ".provider", path);

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                sendIntent.setType("image/*");
                sendIntent.setClipData(ClipData.newRawUri("", screenshotUri));
                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(sendIntent, "Hello World"));

            } catch (FileNotFoundException e1) {
                SendImage(returnedBitmap);
            }

        });

    }


    private void SendImage(Bitmap banner) {

        try {
            OutputStream outputStream;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Annadata_Seller_" + user.getUid());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Annadata");
                Uri imageStore = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageStore));
                banner.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                outputStream.flush();
                outputStream.close();
                HeyToaster.HeyToaster1(this, "This Banner Location in Phone is \nInternal Storage>Pictures>Annadata", R.layout.toast_info);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageStore);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));

            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Profile.this, "Error During Store Image", Toast.LENGTH_SHORT).show();
        }


    }

    private void showLogoutAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Do you want to logout from the application?")
                .setCancelable(false)
                .setPositiveButton("Logout", (dialog, i) -> {

                    FIRE_db.collection(FireTag.FireUser)
                            .document(user.getUid())
                            .update("fcm_TOKEN", FieldValue.delete())
                            .addOnSuccessListener(unused -> AuthUI.getInstance().signOut(Profile.this));
                    dialog.dismiss();
                }).setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());
        dialogBuilder.create().show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, Flash.class);
            startActivity(intent);
            finish();
        } else {
            HistoryCycle(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
        }
    }

    String SellerId;
    String ProductId;

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        if(deepLink!=null) {
                            SellerId = deepLink.getQueryParameter("sellerid");
                            ProductId = deepLink.getQueryParameter("productid");
                            if(SellerId!=null) {
                                Log.d(Mtag.Tag, "onSu96ccess: " + SellerId);
                                Intent intent1 = new Intent(Profile.this, FoodBuy.class);
                                intent1.putExtra("SellerIDFromSharableLink", SellerId);
                                startActivity(intent1);
                                finish();
                            }else if(ProductId!=null){
                                Log.d(Mtag.Tag, "onSu96ccess: " + ProductId);
                                Intent intent1 = new Intent(Profile.this, SellConform.class);
                                intent1.putExtra("FIRE_STORE_DOCUMENT", ProductId);
                                startActivity(intent1);
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w(Mtag.Tag, "getDynamicLink:onFailure", e));


        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (historyAdapter != null) {
            historyAdapter.stopListening();
        }
    }

    private void HistoryCycle(FirebaseUser user) {

        Query query = FirebaseFirestore.getInstance()
                .collection(FireTag.FireUser)
                .document(user.getUid())
                .collection(FireTag.FireHistory);


        FirestoreRecyclerOptions<BUY_HISTORY> options = new FirestoreRecyclerOptions.Builder<BUY_HISTORY>()
                .setQuery(query, BUY_HISTORY.class)
                .build();

        historyAdapter = new HistoryAdapter(options, this, FIRE_db);
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.startListening();
    }


    private boolean citySearch(String s) {
        Log.d(Mtag.Tag, "citySearch: "+s);
        for (String city : CityName.list) {
            if (s.equals(city)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7145) {
            switch (resultCode) {
                case RESULT_OK:
                    assert data != null;
                    dial_bitmap = (Bitmap) data.getExtras().get("data");
                    dial_userPic.setImageBitmap(dial_bitmap);
                    break;
                case RESULT_CANCELED:
                    Log.d(Mtag.Tag, "onActivityResult: " + "NO PHOTO");
                    break;
                default:

            }
        }
    }

    void LATLANGtoCITYName() {

        geocodingISon = true;
        dial_submit_button.setVisibility(View.GONE);
        Toast.makeText(this, "Please Wait we Update City", Toast.LENGTH_SHORT).show();
        dial_cityName.setText("Please Wait We Update City");
        String key = "add your key";
        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + geoPoint.getLongitude() + "," + geoPoint.getLatitude() + ".json?access_token=" + key + "&types=district&limit=1";
        Log.d(Mtag.Tag, url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, response -> {
                    try {

                        JSONArray arr = response.getJSONArray("features");
                        Log.d(Mtag.Tag, "12333onResponse: " + arr);
                        cityName = arr.getJSONObject(0).getString("text");
                        if (cityName.equals("")) {
                            Toast.makeText(Profile.this, "Please Select city correctly", Toast.LENGTH_SHORT).show();
                        } else {
                            String s ="Updated City : " + cityName;
                            dial_cityName.setText(s);
                        }
                        Log.d(Mtag.Tag, "12333onResponse: " + arr);

                    } catch (JSONException e) {

                        Log.d(Mtag.Tag, "onResponse: 287");
                        Toast.makeText(Profile.this, "Please Select city correctly", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    geocodingISon = false;
                    dial_submit_button.setVisibility(View.VISIBLE);

                },
                error -> {
                    Log.d(Mtag.Tag, "onErrorResponse: " + error.toString());
                    geocodingISon = false;
                    dial_submit_button.setVisibility(View.VISIBLE);
                });
        requestQueue.add(jsonObjectRequest);
    }


}