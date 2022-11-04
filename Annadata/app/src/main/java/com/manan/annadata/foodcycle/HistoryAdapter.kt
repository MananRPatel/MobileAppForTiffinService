package com.manan.annadata.foodcycle

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.manan.annadata.R
import com.manan.annadata.dbhelpers.BUY_HISTORY
import com.manan.annadata.foodcycle.HistoryAdapter.HistoryHolder
import com.manan.annadata.idtag.FireTag

class HistoryAdapter(
    options: FirestoreRecyclerOptions<BUY_HISTORY>,
    var context: Context,
    var FIRE_db: FirebaseFirestore
) : FirestoreRecyclerAdapter<BUY_HISTORY, HistoryHolder>(options) {
    override fun onBindViewHolder(holder: HistoryHolder, position: Int, model: BUY_HISTORY) {
        holder.constraintLayout.visibility = View.GONE
        if (model.RATING_FOOD == 0) {
            holder.rating_emoji.visibility = View.INVISIBLE
            holder.order_rating.visibility = View.VISIBLE
        } else {
            holder.order_rating.visibility = View.INVISIBLE
            holder.rating_emoji.visibility = View.VISIBLE
            when (model.RATING_FOOD) {
                1 -> holder.rating_emoji.text = "\uD83D\uDE1F"
                2 -> holder.rating_emoji.text = "\uD83D\uDE41"
                3 -> holder.rating_emoji.text = "\uD83D\uDE10"
                4 -> holder.rating_emoji.text = "\uD83D\uDE42"
                5 -> holder.rating_emoji.text = "\uD83D\uDE00"
            }
        }
        FIRE_db.collection(FireTag.FireUser)
            .document((model.RETAILER_ID)!!)
            .get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot ->
                val s = "Retailer : " + documentSnapshot["user_NAME"]
                holder.t1.text = s
            })
        var s: String
        s = "Food : " + model.FOOD_NAME
        holder.t2.text = s
        s = "Weight : " + model.FOOD_WEIGHT
        holder.t3.text = s
        s = "Order : " + model.TOTAL_FOOD
        holder.t4.text = s
        s = "Food Info : " + model.FOOD_INFO
        holder.t5.text = s
        if ((model.FOOD_PRICE) == 0) s = "For Donate Purpose" else s =
            "Food price : " + model.FOOD_PRICE
        holder.t6.text = s
        if (model.ORDER_STATUS == BUY_HISTORY.ORDER_PLACED) {
            holder.order_s.setTextColor(context.getColor(R.color.ORDER_PLACED))
            holder.order_s_d.setTextColor(context.getColor(R.color.ORDER_PLACED))
            holder.order_s_d.setText("Order is Placed")
            holder.order_rating.visibility = View.INVISIBLE
            holder.rating_emoji.visibility = View.INVISIBLE

            //   holder.order_s_t.setText("Order Placed");
        } else if (model.ORDER_STATUS == BUY_HISTORY.ORDER_CONFORM) {
            holder.order_s.setTextColor(context.getColor(R.color.ORDER_CONFORM))
            holder.order_s_d.setTextColor(context.getColor(R.color.ORDER_CONFORM))
            holder.order_s_d.setText("Order is Conformed")
            //    holder.order_s_t.setText("Order Conformed");
        } else if (model.ORDER_STATUS == BUY_HISTORY.ORDER_CANCEL) {
            holder.order_s.setTextColor(context.getColor(R.color.ORDER_CANCEL))
            holder.order_s_d.setTextColor(context.getColor(R.color.ORDER_CANCEL))
            holder.order_s_d.setText("Order is Cancel")
            //     holder.order_s_t.setText("Order Canceled");
        }
        holder.itemView.setOnClickListener {
            val v: Int =
                if (holder.constraintLayout.getVisibility() == View.GONE) View.VISIBLE else View.GONE
            holder.constraintLayout.getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING)
            TransitionManager.beginDelayedTransition(holder.constraintLayout, AutoTransition())
            holder.constraintLayout.setVisibility(v)
        }
        holder.order_s.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (model.ORDER_STATUS == BUY_HISTORY.ORDER_PLACED) Toast.makeText(
                    context,
                    "Order is placed",
                    Toast.LENGTH_SHORT
                ).show() else if (model.ORDER_STATUS == BUY_HISTORY.ORDER_CONFORM) Toast.makeText(
                    context, "Order is confirmed", Toast.LENGTH_SHORT
                ).show() else if (model.ORDER_STATUS == BUY_HISTORY.ORDER_CANCEL) Toast.makeText(
                    context, "Order is canceled", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.card_food_history, parent, false)
        return HistoryHolder(view)
    }

    inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var t1: TextView
        var t2: TextView
        var t3: TextView
        var t4: TextView
        var t5: TextView
        var t6: TextView
        var order_s: TextView
        var order_s_d: TextView
        var rating_emoji //,order_s_t;
                : TextView
        var order_rating: ImageButton
        var constraintLayout: ConstraintLayout
        private var ratingValue = 0
        private var queryLayout: TextInputLayout? = null
        private var query: TextInputEditText? = null
        private fun GetEmoji(value: Int): String {
            when (value) {
                1 -> return "\uD83D\uDE1F"
                2 -> return "\uD83D\uDE41"
                3 -> return "\uD83D\uDE10"
                4 -> return "\uD83D\uDE42"
                else -> return "\uD83D\uDE00"
            }
        }

        init {
            t1 = itemView.findViewById(R.id.card_fh_retailer)
            t2 = itemView.findViewById(R.id.card_fh_foodname)
            t3 = itemView.findViewById(R.id.card_fh_foodweight)
            t4 = itemView.findViewById(R.id.card_fh_ordernumber)
            t5 = itemView.findViewById(R.id.card_fh_foodinfo)
            t6 = itemView.findViewById(R.id.card_fh_foodvalue)
            rating_emoji = itemView.findViewById(R.id.card_fh_rating)

            // order_s_t=itemView.findViewById(R.id.card_fh_order_status_text);
            order_s = itemView.findViewById(R.id.card_fh_order_status)
            order_s_d = itemView.findViewById(R.id.card_fh_order_status_des)
            constraintLayout = itemView.findViewById(R.id.expandcard_his)
            order_rating = itemView.findViewById(R.id.card_fh_ratingbar)
            order_rating.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.dial_rating_bar)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                    dialog.create()
                    dialog.show()
                    val submit: Button
                    val cancel: Button
                    submit = dialog.findViewById(R.id.dial_rb_submit)
                    cancel = dialog.findViewById(R.id.dial_rb_cancel)
                    val rate_1 = dialog.findViewById<TextView>(R.id.dial_rb_1)
                    val rate_2 = dialog.findViewById<TextView>(R.id.dial_rb_2)
                    val rate_3 = dialog.findViewById<TextView>(R.id.dial_rb_3)
                    val rate_4 = dialog.findViewById<TextView>(R.id.dial_rb_4)
                    val rate_5 = dialog.findViewById<TextView>(R.id.dial_rb_5)
                    query = dialog.findViewById(R.id.dial_rb_query)
                    queryLayout = dialog.findViewById(R.id.dial_rb_querylayout)
                    rate_1.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            ratingValue = 1
                            queryLayout!!.setVisibility(View.VISIBLE)
                        }
                    })
                    rate_2.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            ratingValue = 2
                            queryLayout!!.setVisibility(View.VISIBLE)
                        }
                    })
                    rate_3.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            ratingValue = 3
                            queryLayout!!.setVisibility(View.VISIBLE)
                        }
                    })
                    rate_4.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            ratingValue = 4
                            queryLayout!!.setVisibility(View.GONE)
                        }
                    })
                    rate_5.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            ratingValue = 5
                            queryLayout!!.setVisibility(View.GONE)
                        }
                    })
                    submit.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            if (ratingValue == 0) Toast.makeText(
                                context,
                                "Please Select Rating",
                                Toast.LENGTH_SHORT
                            ).show() else {
                                val documentSnapshot = snapshots.getSnapshot(adapterPosition)
                                val tiffinId = documentSnapshot["food_ID"] as String?
                                documentSnapshot.reference.update("rating_FOOD", ratingValue)
                                    .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                        override fun onSuccess(unused: Void?) {
                                            val map: MutableMap<String, Any> = HashMap()
                                            map["all_RATING"] = FieldValue.arrayUnion(ratingValue)
                                            if (query!!.getText() != null) {
                                                map["all_REVIEW"] = FieldValue.arrayUnion(
                                                    GetEmoji(ratingValue) + " -> " + query!!.getText()
                                                        .toString()
                                                )
                                            }
                                            FIRE_db.collection(FireTag.FireMarket)
                                                .document((tiffinId)!!)
                                                .update(map)
                                                .addOnSuccessListener { dialog.cancel() }
                                        }
                                    })
                            }
                        }
                    })
                    cancel.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            dialog.cancel()
                        }
                    })
                }
            })
        }
    }
}