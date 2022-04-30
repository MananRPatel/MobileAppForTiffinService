package com.mananhirak.annadata.activityshelpers;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mananhirak.annadata.R;

public class HeyToaster {

   public static void HeyToaster1(Context context, String text, int Layout){
      Toast toast= new Toast(context);
      View view = LayoutInflater.from(context).inflate(Layout,null);
      toast.setView(view);
      toast.setGravity(Gravity.TOP,0,45);
      TextView textView=view.findViewById(R.id.toast_i_text);
      textView.setText(text);
      toast.setDuration(Toast.LENGTH_LONG);
      toast.show();
   }


}
