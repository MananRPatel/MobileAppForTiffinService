package com.manan.annadata.foodcycle

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.manan.annadata.R
import com.manan.annadata.activitys.DeliveryConform
import com.manan.annadata.dbhelpers.DELIVERY_BOY
import com.manan.annadata.foodcycle.DeliveryBoyAdapter.DeliveryHolder
import com.manan.annadata.idtag.FireTag
import com.manan.annadata.idtag.Mtag

class DeliveryBoyAdapter(
    options: FirestoreRecyclerOptions<DELIVERY_BOY>,
    var context: Context
) : FirestoreRecyclerAdapter<DELIVERY_BOY, DeliveryHolder>(options) {
    var FIRE_db: FirebaseFirestore
    override fun onBindViewHolder(holder: DeliveryHolder, position: Int, model: DELIVERY_BOY) {
        var s: String?
        s = "Food Name : " + model.FOOD_NAME
        holder.t1.setText(s)
        FIRE_db.collection(FireTag.FireUser)
            .document((model.BUYER_ID)!!)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                public override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                    val s1: String = "Buyer : " + documentSnapshot.get("user_NAME")
                    holder.t2.setText(s1)
                }
            })
        s = "Order Took : " + model.TOTAL_ORDER
        holder.t4.setText(s)
        s = "Order Time : " + model.DELIVERY_TIME
        holder.t5.setText(s)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.getContext())
        val view: View = layoutInflater.inflate(R.layout.card_food_delivery, parent, false)
        return DeliveryHolder(view)
    }

    inner class DeliveryHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var t1: TextView
        var t2: TextView
        var t4: TextView
        var t5: TextView

        init {
            t1 = itemView.findViewById(R.id.card_fd_foodname)
            t2 = itemView.findViewById(R.id.card_fd_buyername)
            t4 = itemView.findViewById(R.id.card_fd_ordertake)
            t5 = itemView.findViewById(R.id.card_fd_ordertime)
            itemView.setOnClickListener(View.OnClickListener { view: View? ->
                Log.d(Mtag.Tag, "DD" + getAdapterPosition())
                val documentSnapshot: DocumentSnapshot =
                    getSnapshots().getSnapshot(getAdapterPosition())
                val str: String = documentSnapshot.getId()
                Log.d(Mtag.Tag, "AA" + str)
                val intent: Intent = Intent(context, DeliveryConform::class.java)
                intent.putExtra("DELIVERY_DOC_ID", str)
                context.startActivity(intent)
            })
        }
    }

    init {
        FIRE_db = FirebaseFirestore.getInstance()
    }
}