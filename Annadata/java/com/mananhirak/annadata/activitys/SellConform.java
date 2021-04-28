package com.mananhirak.annadata.activitys;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mananhirak.annadata.R;
import com.mananhirak.annadata.dbhelpers.BUY_HISTORY;
import com.mananhirak.annadata.dbhelpers.DELIVERY_BOY;
import com.mananhirak.annadata.dbhelpers.FOOD_USERS;
import com.mananhirak.annadata.dbhelpers.MARKET;
import com.mananhirak.annadata.idtag.FireTag;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class SellConform extends AppCompatActivity  {

    private static final String TAG ="MANANANANAN" ;
    EditText ed1,ed2;
    String FIREBASE_DOCUMENT_ID;
    boolean Time_Flag;
    String Food_time;
    int Hour,Minute;
    int Day,Month,Year;
    Calendar calendar;
    FirebaseFirestore FIRE_db;
    FirebaseUser user;
    TextView t1,t2,t3,t4,t5,t6,t7,t8;
    ImageView i,schedule_image;
    Button button,button1,button2;
    long ScheduleFood;
    ConstraintLayout constraintLayout;
    //Animation
    private boolean switchOn =false;
    LottieAnimationView lottieSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_conform);
        Objects.requireNonNull(getSupportActionBar()).hide();

        FIRE_db= FirebaseFirestore.getInstance();

        Intent get_intent=getIntent();
        FIREBASE_DOCUMENT_ID=get_intent.getStringExtra("FIRE_STORE_DOCUMENT");

//        t1=findViewById(R.id.scusername);
        t2=findViewById(R.id.scfoodname);
        t3=findViewById(R.id.scfoodvalue);
        t4=findViewById(R.id.scfoodweight);
        t5=findViewById(R.id.scfoodtime);
        t6=findViewById(R.id.scfooddate);
        t7=findViewById(R.id.scfoodleft);
//        t8=findViewById(R.id.scfoodinfo);

        ed1=findViewById(R.id.sccurrentaddress);
        ed2=findViewById(R.id.scnumberoforder);

        button=findViewById(R.id.scconformbutton);
        button1=findViewById(R.id.act_s_c__schedulebutton);
        button2 = findViewById(R.id.scordertime);

//        aSwitch=findViewById(R.id.act_s_c__switch);
        lottieSwitch=findViewById(R.id.act_s_c__switch);

        constraintLayout=findViewById(R.id.act_s_c__schedulelayout);

        //hirak
        schedule_image=findViewById(R.id.schedule_image);
//        sche_image2=findViewById(R.id.layour7);


        calendar=Calendar.getInstance();

        Hour=calendar.get(Calendar.AM_PM)==Calendar.PM?calendar.get(Calendar.HOUR)+12:calendar.get(Calendar.HOUR);
        Minute=calendar.get(Calendar.MINUTE);

        CharSequence charSequence2= DateFormat.format("hh:mm a",calendar);
        Food_time=(String)charSequence2;
        Log.d("MANANANANAN","1->"+Food_time);
        Time_Flag=false;


        ScheduleFood=11;
        button1.setVisibility(View.GONE);
       constraintLayout.setVisibility(View.GONE);

        //hirak
        schedule_image.setVisibility(View.GONE);
//        sche_image2.setVisibility(View.GONE);


        lottieSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchOn){
                    lottieSwitch.setMinAndMaxProgress(0.5f,1.0f);
                    lottieSwitch.setSpeed(2);
                    lottieSwitch.playAnimation();
                    button1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button1.setVisibility(View.GONE);
                        }
                    },730);
                    ScheduleFood=11;

                }else {
                    lottieSwitch.setMinAndMaxProgress(0.0f,0.5f);
                    lottieSwitch.setSpeed(2);
                    lottieSwitch.playAnimation();
                    ScheduleFood=0;
                    button1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button1.setVisibility(View.VISIBLE);
                        }
                    },730);
                }
                switchOn = !switchOn;
            }
        });

//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    ScheduleFood=0;
//                    button1.setVisibility(View.VISIBLE);
//                }else{
//                    button1.setVisibility(View.GONE);
//                    ScheduleFood=11;
//
//                }
//            }
//        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleFood();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeSelectForOrder();
                Toast.makeText(SellConform.this,Food_time,Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "onCreate: "+FIREBASE_DOCUMENT_ID);

        FIRE_db.collection(FireTag.FireMarket).document(FIREBASE_DOCUMENT_ID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long schedule_time_doc=(long)Objects.requireNonNull(documentSnapshot.get("schedule_DATE"));
                if(schedule_time_doc!=-1111){
                    constraintLayout.setVisibility(View.VISIBLE);

                    //hirak
                    schedule_image.setVisibility(View.VISIBLE);
//                    sche_image2.setVisibility(View.VISIBLE);

                    YearMonthDayMaker(schedule_time_doc);
                }

                SetMarketItems(documentSnapshot);
            }
        });

    }



    void SetMarketItems(DocumentSnapshot documentSnapshot){

        MARKET m=documentSnapshot.toObject(MARKET.class);

        String s;
        assert m != null;
        s="Food Name : "+m.getFOOD_NAME();
        t2.setText(s);
        if(m.getFOOD_PRICE()==0){
            s="Donation Purpose";
        }else{
            s="₹ " +m.getFOOD_PRICE();
        }
        t3.setText(s);
        s=m.getFOOD_WEIGHT()+" KG";
        t4.setText(s);
        s=m.getFOOD_TIME();
        t5.setText(s);
        s=m.getFOOD_DATE();
        t6.setText(s);
        s=m.getTOTAL_FOOD()+" Order Left";
        t7.setText(s);
//        s="Food info : "+m.getFOOD_INFO();
//        t8.setText(s);

        i = findViewById(R.id.act_s_c_foodphoto);

        Glide.with(this)
                .load(m.getPHOTO_URL())
                .into(i);


//        FIRE_db.collection(FireTag.FireUser).document(m.getRETAILER_ID()).get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        String s="User : "+documentSnapshot.get("user_NAME");
//                        t1.setText(s);
//                    }
//                });

        MakeFinalOrderForUser(m);

    }


    void MakeFinalOrderForUser(final MARKET market){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String s1,s2;
                s1=ed1.getText().toString();
                s2=ed2.getText().toString();
                if(s1.isEmpty()){
                    ed1.setError("Please Put Address..");
                    ed1.requestFocus();
                }else if(s2.isEmpty()){
                    ed2.setError("Please Put Number Of Order");
                    ed2.requestFocus();
                }else if((market.getTOTAL_FOOD()-(Integer.parseInt(s2))<0)||((Integer.parseInt(s2))<=0)){
                    ed2.setError("Sorry Not Much Order  ");
                    ed2.requestFocus();
                }else if(Time_Flag){
                    TimeSelectForOrder();
                    Toast.makeText(SellConform.this,"Old Time "+Food_time,Toast.LENGTH_SHORT).show();

                }else if(ScheduleFood==0){
                    Toast.makeText(SellConform.this,"Please select date",Toast.LENGTH_SHORT).show();
                }else{

                    user= FirebaseAuth.getInstance().getCurrentUser();

                    DELIVERY_BOY deliveryBoy=new DELIVERY_BOY();
                    assert user != null;
                    deliveryBoy.setBUYER_ID(user.getUid());
                    deliveryBoy.setBUYER_NAME(user.getDisplayName());
                    deliveryBoy.setFOOD_ID(FIREBASE_DOCUMENT_ID);
                    deliveryBoy.setFOOD_NAME(market.getFOOD_NAME());
                    deliveryBoy.setDELIVERY_ADDRESS(s1);
                    deliveryBoy.setDELIVERY_TIME(Food_time);
                    deliveryBoy.setFOOD_MONEY(market.getFOOD_PRICE());
                    deliveryBoy.setFOOD_POINT(market.getFOOD_POINT());
                    deliveryBoy.setTOTAL_ORDER(Integer.parseInt(s2));
                    deliveryBoy.setCONFORM_DELIVERY(false);
                    deliveryBoy.setNO_TIME_LEFT(false);
                    if(ScheduleFood!=11){
                        deliveryBoy.setLAST_SCHEDULE_DATE(FutureDateFormat());
                        deliveryBoy.setSCHEDULE_ORDER(ScheduleFood);

                    }else{
                        deliveryBoy.setSCHEDULE_ORDER(-1111);
                    }


                    FIRE_db.collection(FireTag.FireUser).
                            document(market.getRETAILER_ID()).collection(FireTag.FireDelivery).
                            add(deliveryBoy);


                    Map<String,Object> map=new HashMap<>();

                    map.put("total_FOOD",FieldValue.increment(-Integer.parseInt(s2)));

                    if(market.getTOTAL_FOOD()-(Integer.parseInt(s2))<=0)
                        map.put("no_FOOD_LEFT",true);

                    FIRE_db.collection(FireTag.FireMarket).document(FIREBASE_DOCUMENT_ID)
                            .update(map);



                    FIRE_db.collection(FireTag.FireUser).document(user.getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    HistoryCreateForUserOrder(documentSnapshot,market, s2);
                                }
                            });

                }
            }
        });

    }

    void HistoryCreateForUserOrder(DocumentSnapshot documentSnapshot,MARKET market,String ORDER){

        final BUY_HISTORY buyHistory=new BUY_HISTORY();

        FOOD_USERS foodUsers=documentSnapshot.toObject(FOOD_USERS.class);
        assert foodUsers != null;
        Log.d(TAG, "HISTORY_RECORD: "+foodUsers.getUSER_NAME());

        buyHistory.setFOOD_ID(FIREBASE_DOCUMENT_ID);
        buyHistory.setFOOD_NAME(market.getFOOD_NAME());
        buyHistory.setFOOD_WEIGHT(market.getFOOD_WEIGHT());
        buyHistory.setTOTAL_FOOD(Integer.parseInt(ORDER));
        buyHistory.setRETAILER_NAME(foodUsers.getUSER_NAME());
        buyHistory.setFOOD_INFO(market.getFOOD_INFO());
        buyHistory.setFOOD_POINT(market.getFOOD_POINT());
        buyHistory.setFOOD_PRICE(market.getFOOD_PRICE());


        FIRE_db.collection(FireTag.FireUser).document(user.getUid())
                .collection(FireTag.FireHistory).add(buyHistory);

        Toast.makeText(SellConform.this,Food_time,Toast.LENGTH_SHORT).show();
        finish();

    }

    public void TimeSelectForOrder(){

        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Time_Flag=(i<Hour)||(i==Hour&&i1<Minute);
                Calendar calendar1=Calendar.getInstance();
                calendar1.set(Calendar.HOUR,(i+12)%24);
                calendar1.set(Calendar.MINUTE,i1);

                CharSequence charSequence= DateFormat.format("hh:mm a",calendar1);
                Food_time=(String)charSequence;
                Log.d(TAG,"2->"+Food_time);

            }
        },Hour,Minute,true);
        timePickerDialog.show();
    }


    public void ScheduleFood(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                ScheduleFood=  10000*i+100*(1+i1)+i2;
                Log.d(TAG, "onDateSet: "+ScheduleFood);

            }
        },Year,Month,Day);
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,Year);
        c.set(Calendar.MONTH,Month-1);
        c.set(Calendar.DATE,Day);
        Log.d(TAG, "ScheduleFood: "+c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+1000*60*60*24*2);
        datePickerDialog.show();

    }

    void YearMonthDayMaker(long FoodTime){
        Log.d(TAG, "YearMonthDayMaker: "+FoodTime);
        Year=(int)(FoodTime/10000);
        Month=(int)(FoodTime/100-Year*100);
        Day=(int)(FoodTime%100);
        Log.d(TAG, "YearMonthDayMaker: "+Year+Month+Day);
    }

    public long FutureDateFormat(){

        Calendar calendar1=Calendar.getInstance();
        return 10000*calendar1.get(Calendar.YEAR)+100*(1+calendar1.get(Calendar.MONTH))+calendar1.get(Calendar.DATE);
    }

}