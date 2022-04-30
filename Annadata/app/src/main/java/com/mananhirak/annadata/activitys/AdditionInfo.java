package com.mananhirak.annadata.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.activityshelpers.CityName;
import com.mananhirak.annadata.activityshelpers.HeyToaster;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.idtag.FireTag;
import com.mananhirak.annadata.idtag.Mtag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AdditionInfo extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseUser user;
    FirebaseFirestore FIRE_db;
    FirebaseStorage Fire_ST;
    StorageReference storageReference;
    TextView helloUser,myCity;
    ImageView imageView;
    Dialog dialog;
    Button close_dialog;
    Button conform;
    Bitmap bitmap;
    ImageButton cameraButton, locationButton;
    MapFragment mapFragment;
    LottieAnimationView lottieAnimationView, lottieAnimationView1;
    String cityName;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean geocodingISon=true;
    GeoPoint geoPoint;

//********************************************************************************

    // Assign All IDs

    private void IdSaver() {
        helloUser = findViewById(R.id.act_sign_name);
        lottieAnimationView = findViewById(R.id.act_sign_firework2);
        lottieAnimationView1 = findViewById(R.id.act_sign_firework1);
        cameraButton = findViewById(R.id.act_sign_capture);
        conform = findViewById(R.id.act_sign_submit);
        imageView = findViewById(R.id.act_sign_profile);
        locationButton = findViewById(R.id.act_sign_map);
        myCity=findViewById(R.id.act_sign_city);
    }

//**********************************************************************************

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Remove After Testing App
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg3);

        user = FirebaseAuth.getInstance().getCurrentUser();
        FIRE_db = FirebaseFirestore.getInstance();
        Fire_ST = FirebaseStorage.getInstance();

        IdSaver();

        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView1.setVisibility(View.GONE);

        SetDefaultLocation();

        String s = "Hello " + user.getDisplayName();
        helloUser.setText(s);

        cameraButton.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(AdditionInfo.this.getPackageManager()) != null)
                (AdditionInfo.this).startActivityForResult(intent, 7145);
        });


        conform.setOnClickListener(view -> {


            if (citySearch(cityName)) {
                Toast.makeText(this, "Please Open Map and Set Valid City", Toast.LENGTH_SHORT).show();
            } else if (bitmap == null) {
                Toast.makeText(AdditionInfo.this, "Put Profile Picture", Toast.LENGTH_SHORT).show();
            }else if(geocodingISon){
                HeyToaster.HeyToaster1(AdditionInfo.this, "Please Wait a Second We Computing Location",R.layout.toast_info);
            }else {

                cameraButton.setVisibility(View.GONE);
                conform.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView1.setVisibility(View.VISIBLE);

                FOOD_USERS foodUsers = new FOOD_USERS();
                foodUsers.setUSER_NAME(user.getDisplayName());
                foodUsers.setID(user.getUid());
                foodUsers.setUSER_LOCATION(geoPoint);
                foodUsers.setADDRESS(cityName);

                FIRE_db.collection(FireTag.FireUser).document(user.getUid()).set(foodUsers)
                        .addOnSuccessListener(aVoid -> UploadPicForUser());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7145) {
            switch (resultCode) {
                case RESULT_OK:
                    assert data != null;
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    break;
                case RESULT_CANCELED:
                    Log.d(Mtag.Tag, "onActivityResult: " + "NO PHOTO");
                    break;
                default:

            }
        }
    }

    void UploadPicForUser() {

        storageReference = Fire_ST.getReference()
                .child(user.getUid())
                .child("User_Profile_Pic");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);

        storageReference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(AdditionInfo.this, "Thanks For Uploading Pic", Toast.LENGTH_SHORT).show();
                    GetUrlOfUserPic();
                });
    }

    private void GetUrlOfUserPic() {
        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> FIRE_db.collection(FireTag.FireUser)
                        .document(user.getUid())
                        .update("photo_URL", uri.toString())
                        .addOnSuccessListener(unused -> FirebaseMessaging.getInstance().getToken()
                                .addOnSuccessListener(AdditionInfo.this::GetFCMToken)));
    }

    void GetFCMToken(String token) {

        FIRE_db.collection(FireTag.FireUser)
                .document(user.getUid())
                .update("fcm_TOKEN", token)
                .addOnSuccessListener(unused -> {
                    Intent intent = new Intent(AdditionInfo.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    private boolean citySearch(String s) {
        for (String city : CityName.list) {
            if (s.equals(city)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    void SetDefaultLocation(){
        Log.d(Mtag.Tag, "onSuccess: 227");

        setPermissionControl();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.d(Mtag.Tag, "onSuccess: 233" + location);
            if (location != null) {
                Log.d(Mtag.Tag, "onSuccess: 235");
                geoPoint=new GeoPoint(location.getLatitude(),location.getLongitude());
                HeyToaster.HeyToaster1(AdditionInfo.this,"Using Map Icon you can open the map \n Using camera icon you can open camera",R.layout.toast_info);
                LATLANGtoCITYName();
                CreateDialog();
            }
        });
    }
    void LATLANGtoCITYName(){

        geocodingISon=true;
        String key="addyourkey";
        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/"+geoPoint.getLongitude()+","+geoPoint.getLatitude()+".json?access_token="+key+"&types=district&limit=1";
        Log.d(Mtag.Tag, url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray arr=response.getJSONArray("features");
                    Log.d(Mtag.Tag, "12333onResponse: "+arr);
                    cityName=arr.getJSONObject(0).getString("text");
                    if(cityName.equals("")){
                        Toast.makeText(AdditionInfo.this, "Please Select city correctly", Toast.LENGTH_SHORT).show();
                        openMap();
                    }else{
                        myCity.setText(("My City is "+cityName));
                    }
                    Log.d(Mtag.Tag, "12333onResponse: "+arr);

                } catch (JSONException e) {

                    Log.d(Mtag.Tag, "onResponse: 287");
                    Toast.makeText(AdditionInfo.this, "Please Select city correctly", Toast.LENGTH_SHORT).show();
                    openMap();

                    e.printStackTrace();
                }
                geocodingISon=false;
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Mtag.Tag, "onErrorResponse: "+error.toString());
                        geocodingISon=false;
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    void CreateDialog(){
        setPermissionControl();
        locationButton.setVisibility(View.VISIBLE);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dial_map);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        close_dialog=dialog.findViewById(R.id.dial_m_close);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(geocodingISon)
                    HeyToaster.HeyToaster1(AdditionInfo.this,"Please wait\nWe currently fetch your\nLocation",R.layout.toast_info);
                else
                    AdditionInfo.this.openMap();
            }
        });
        close_dialog.setOnClickListener(view -> closeMap());
    }
    private void openMap() {
        dialog.show();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.dial_m_google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
   private void closeMap(){
        dialog.dismiss();
        myCity.setText(("Please wait we set your City"));
        Log.d(Mtag.Tag, "getLocation: 328"+geoPoint);
       LATLANGtoCITYName();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Log.d(Mtag.Tag, "onSuccess: 272");
        setPermissionControl();

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), 10));

        googleMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Marker Set On Your City");

            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            googleMap.addMarker(markerOptions);
            geoPoint =new GeoPoint(latLng.latitude, latLng.longitude);

        });

    }

    private void setPermissionControl(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "Please Grant Location Permission", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,Flash.class));
            finish();
        }
    }

}