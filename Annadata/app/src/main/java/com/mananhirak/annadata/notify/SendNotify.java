package com.mananhirak.annadata.notify;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mananhirak.annadata.idtag.Mtag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotify {

    Context context;
    String serverKey="Add Server Key";
    String URL ="https://fcm.googleapis.com/fcm/send";

    public SendNotify(Context context) {
        this.context = context;
    }


    public void StartNotify(String token,String title,String message,String Id) {

        if (token != null) {

            FirebaseMessaging.getInstance().subscribeToTopic("Customers");


            JSONObject to = new JSONObject();
            JSONObject data = new JSONObject();

            try {
                data.put("title", title);
                data.put("message", message);
                data.put("id", Id);

                to.put("to", token);
                to.put("data", data);

                NowSend(to);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Log.d(Mtag.Tag, "StartNotify is null : "+token);
        }

    }

    private void NowSend(JSONObject to) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,to, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Mtag.Tag, "NowSend: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Mtag.Tag, "NowSend: " + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>  map =new HashMap<>();

                map.put("Authorization","key="+serverKey);
                map.put("Content-type","application/json");


                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


}
