package com.mananhirak.annadata.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.idtag.FireTag;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AdditionInfo extends AppCompatActivity {


    private static final String TAG ="MANANANANAN" ;
    EditText ed1;
    FirebaseUser user;
    FirebaseFirestore FIRE_db;
    FirebaseStorage Fire_ST;
    StorageReference storageReference;
    ImageView imageView;
    Button button,button1;
    Bitmap bitmap;
    LottieAnimationView lottieAnimationView,lottieAnimationView1,lottieAnimationView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        Objects.requireNonNull(getSupportActionBar()).hide();

        user=FirebaseAuth.getInstance().getCurrentUser();
        FIRE_db=FirebaseFirestore.getInstance();
        Fire_ST = FirebaseStorage.getInstance();


        TextView textView1=findViewById(R.id.act_sign_idn);
        String s="Hello "+ user.getDisplayName();
        textView1.setText(s);

        ed1=findViewById(R.id.signuseraddress);

        lottieAnimationView=findViewById(R.id.act_a_i_firework2);
        lottieAnimationView1=findViewById(R.id.act_a_i_firework1);
        lottieAnimationView2=findViewById(R.id.act_a_i_probutton);

        lottieAnimationView.setVisibility(View.GONE);
        lottieAnimationView1.setVisibility(View.GONE);


        button = findViewById(R.id.signsignbutton);
        //button1 = findViewById(R.id.act_a_i_probutton);

        imageView=findViewById(R.id.act_a_i_image);

        lottieAnimationView2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent,7145);
                }
            }
        });

//        button1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String s1=ed1.getText().toString();

                if(s1.isEmpty()){
                    ed1.setError("Please Enter Address..");
                    ed1.requestFocus();
                }else if(bitmap==null){
                    Toast.makeText(AdditionInfo.this, "Put Profile Picture", Toast.LENGTH_SHORT).show();
                }else{

                    lottieAnimationView2.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView1.setVisibility(View.VISIBLE);


                    FOOD_USERS foodUsers = new FOOD_USERS();

                    foodUsers.setUSER_NAME(user.getDisplayName());
                    foodUsers.setID(user.getUid());
                    foodUsers.setADDRESS(s1);


                    FIRE_db.collection(FireTag.FireUser).document(user.getUid()).set(foodUsers)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            UploadPicForUser(bitmap);
                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7145){
            switch (resultCode){
                case RESULT_OK:
                    assert data != null;
                    bitmap= (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG, "onActivityResult: "+"NO PHOTO");
                    break;
                default:

            }
        }
    }

     void UploadPicForUser(Bitmap bitmap) {

         storageReference=Fire_ST.getReference()
                 .child(user.getUid())
                 .child("User_Profile_Pic");

         ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

         bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

         storageReference.putBytes(byteArrayOutputStream.toByteArray())
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         Toast.makeText(AdditionInfo.this, "Thanks For Uploading Pic", Toast.LENGTH_SHORT).show();
                         GetUrlOfUserPic(storageReference);
                     }
                 });


     }

     void GetUrlOfUserPic(StorageReference storageReference) {
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FIRE_db.collection(FireTag.FireUser)
                                .document(user.getUid())
                                .update("photo_URL",uri.toString());
                        Intent intent=new Intent(AdditionInfo.this,MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
    }

}