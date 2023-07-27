package com.suviridedriver.ui.rides.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.suviridedriver.R
import com.suviridedriver.model.rides.Data
import com.suviridedriver.utils.Constants

class CancelledAdpater(val context: Context, val list: List<Data>) : RecyclerView.Adapter<CancelledAdpater.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.ongoing_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.tvCustomerName.text = list[position].customerName
        holder.tvPaymentMode.text = "Cash"
        holder.tvFare.text =  "Rs ${list[position].fare}/-"
        holder.tvDistance.text = "${list[position].distance} Km"
        holder.tvPickupAddress.text = list[position].pickupLocation
        holder.tvDropAddress.text = list[position].destinationLocation
        holder.ivCancelled_stamp.visibility = View.VISIBLE

        try {
            val imageUrl = list[position].customerImage
            val url = Constants.BASE_URL + imageUrl

            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.profile_icon)
                .into(holder.ivCustomerProfile)
            Log.d("Customer_Image","Success - Customer Image "+url)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Customer_Image","Error - Customer Image "+e.toString())
        }

       /* holder.cardView.setOnClickListener { view: View ->
            Toast.makeText(
                view.context,
                "click on item: $position",
                Toast.LENGTH_LONG
            ).show()
        }*/
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView
        var tvCustomerName: TextView
        var tvPaymentMode: TextView
        var tvFare: TextView
        var tvDistance: TextView
        var tvPickupAddress: TextView
        var tvDropAddress: TextView
        var ivCancelled_stamp: ImageView
        var ivCustomerProfile: ImageView

        init {
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName)
            ivCustomerProfile = itemView.findViewById(R.id.ivCustomerProfile)
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode)
            tvFare = itemView.findViewById(R.id.tvFare)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            tvPickupAddress = itemView.findViewById(R.id.tvPickupAddress)
            tvDropAddress = itemView.findViewById(R.id.tvDropAddress)
            ivCancelled_stamp = itemView.findViewById(R.id.ivCancelled_stamp)
            cardView = itemView.findViewById(R.id.card)
        }
    }
}