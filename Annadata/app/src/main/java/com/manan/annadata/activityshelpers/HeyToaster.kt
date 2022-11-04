package com.manan.annadata.activityshelpers

import android.content.Context
import android.widget.Toast
import android.view.LayoutInflater
import android.view.Gravity
import android.widget.TextView
import com.manan.annadata.R

object HeyToaster {
    fun HeyToaster1(context: Context?, text: String?, Layout: Int) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(Layout, null)
        toast.view = view
        toast.setGravity(Gravity.TOP, 0, 45)
        val textView = view.findViewById<TextView>(R.id.toast_i_text)
        textView.text = text
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }
}