package com.manan.annadata.notify

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import com.manan.annadata.idtag.Mtag
import org.json.JSONException
import org.json.JSONObject

class SendNotify(var context: Context) {
   fun StartNotify(token: String?, title: String?, message: String?, Id: String?) {
        if (token != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("Customers")
            val to = JSONObject()
            val data = JSONObject()
            try {
                data.put("title", title)
                data.put("message", message)
                data.put("id", Id)
                to.put("to", token)
                to.put("data", data)
                NowSend(to)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            Log.d(Mtag.Tag, "StartNotify is null : $token")
        }
    }

    private fun NowSend(to: JSONObject) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URL,
            to,
            Response.Listener { response -> Log.d(Mtag.Tag, "NowSend: $response") },
            Response.ErrorListener { error -> Log.d(Mtag.Tag, "NowSend: $error") }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["Authorization"] = "key=$serverKey"
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }
}