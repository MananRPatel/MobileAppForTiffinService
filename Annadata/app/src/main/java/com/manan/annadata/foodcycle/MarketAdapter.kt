package com.manan.annadata.foodcycle

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.manan.annadata.R
import com.manan.annadata.activitys.SellConform
import com.manan.annadata.activitys.TiffinModify
import com.manan.annadata.dbhelpers.MARKET
import com.manan.annadata.foodcycle.MarketAdapter.MarketHolder
import com.manan.annadata.idtag.Mtag

class MarketAdapter(options: FirestoreRecyclerOptions<MARKET?>, var context: Context) :
    FirestoreRecyclerAdapter<MARKET, MarketHolder>(options) {
    @JvmField
    var isPersonalUse = false
    override fun onBindViewHolder(holder: MarketHolder, position: Int, model: MARKET) {
        var s: String
        holder.constraintLayout.visibility = View.GONE
        s = "Food Name : " + model.FOOD_NAME
        holder.t1.text = s
        s = if (model.FOOD_PRICE == 0) "For Donate Purpose" else "Food price : " + model.FOOD_PRICE
        holder.t2.text = s
        s = "Food Time : " + model.FOOD_TIME
        holder.t3.text = s
        s = "Food Date : " + model.FOOD_DATE
        holder.t4.text = s
        s = "Weight : " + model.FOOD_WEIGHT
        holder.t5.text = s
        s = "Order Left : " + model.LEFT_ORDER
        holder.t6.text = s
        s = "Food info : " + model.FOOD_INFO
        holder.t7.text = s

        holder.i3?.setImageBitmap(BitmapFactory.decodeResource(context.resources, if (model.VEG == true) R.drawable.veg_symbol else R.drawable.non_veg_symbol))


        Glide.with(context)
            .load(model.PHOTO_URL)
            .into(holder.i)
        holder.shareLink.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, model.TIFFIN_LINK)
            sendIntent.type = "text/plain"
            sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(Intent.createChooser(sendIntent, "Hello World"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.card_food_sell, parent, false)
        return MarketHolder(view)
    }

    inner class MarketHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var t1: TextView
        var t2: TextView
        var t3: TextView
        var t4: TextView
        var t5: TextView
        var t6: TextView
        var t7: TextView
        var shareLink: ImageButton
        var i: ImageView
        var i1: ImageView? = null
        var i2: ImageView? = null
        var i3: ImageView? = null
        var dialog: Dialog? = null
        var constraintLayout: ConstraintLayout
        private fun ShowFoodPic(snapshot: DocumentSnapshot) {
            dialog = Dialog(context)
            dialog!!.setContentView(R.layout.dial_foodphoto)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.create()
            dialog!!.show()
            i1 = dialog!!.findViewById(R.id.dial_fp_cancel)
            i1!!.setOnClickListener(View.OnClickListener { view: View? -> dialog!!.dismiss() })
            i2 = dialog!!.findViewById(R.id.dial_fp_foodpic)
            i3 = dialog!!.findViewById(R.id.nonvegveg2)
            Glide.with(context)
                .load(snapshot["photo_URL"])
                .into(i2!!)
        }

        init {
            t1 = itemView.findViewById(R.id.card_fs_food)
            t2 = itemView.findViewById(R.id.card_fs_value)
            t3 = itemView.findViewById(R.id.card_fs_time)
            t4 = itemView.findViewById(R.id.card_fs_fooddate)
            t5 = itemView.findViewById(R.id.card_fs_weight)
            t6 = itemView.findViewById(R.id.card_fs_foodleft)
            t7 = itemView.findViewById(R.id.card_fs_foodinfo)
            shareLink = itemView.findViewById(R.id.card_fs_sharelink)
            constraintLayout = itemView.findViewById(R.id.card_fs_extend)
            constraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            i = itemView.findViewById(R.id.card_fs_foodphoto)
            i.setOnClickListener { view: View? ->
                Log.d(Mtag.Tag, "onClick: $adapterPosition")
                ShowFoodPic(snapshots.getSnapshot(adapterPosition))
            }
            itemView.setOnLongClickListener { view: View? ->
                TransitionManager.beginDelayedTransition(constraintLayout, AutoTransition())
                val v = if (constraintLayout.visibility == View.GONE) View.VISIBLE else View.GONE
                constraintLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.card_bg2)
                constraintLayout.visibility = v
                true
            }
            itemView.setOnClickListener { view: View? ->
                Log.d(Mtag.Tag, "DD$adapterPosition")
                val documentSnapshot = snapshots.getSnapshot(adapterPosition)
                val str = documentSnapshot.id
                Log.d(Mtag.Tag, "DD$str")
                val intent: Intent
                if (isPersonalUse) {
                    intent = Intent(context, TiffinModify::class.java)
                    intent.putExtra("TIFFIN_DOC_ID", str)
                } else {
                    intent = Intent(context, SellConform::class.java)
                    intent.putExtra("FIRE_STORE_DOCUMENT", str)
                }
                context.startActivity(intent)
            }
        }
    }
}