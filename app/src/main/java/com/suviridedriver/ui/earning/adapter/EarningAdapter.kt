package com.suviridedriver.ui.earning.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.suviridedriver.R
import com.suviridedriver.model.earnings.TotalRide
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.Constants

class EarningAdapter(val context: Context, val list: List<TotalRide>) :
    RecyclerView.Adapter<EarningAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ongoing_item, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.tvCustomerName.text = list[position].customerId.fullName
        holder.tvPaymentMode.text = list[position].paymentMethod
        holder.tvFare.text = list[position].fare
        holder.tvDistance.text = list[position].distance
        holder.tvPickupAddress.text = list[position].pickupLocation
        holder.tvDropAddress.text = list[position].destinationLocation
        try {
            val imageUrl = list[position].customerId.profileImage
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

        /*holder.cardView.setOnClickListener { view: View ->
            Toast.makeText(view.context,"click on item: $position",  Toast.LENGTH_LONG).show()
        }*/
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode)
            ivCustomerProfile = itemView.findViewById(R.id.ivCustomerProfile)
            tvFare = itemView.findViewById(R.id.tvFare)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            tvPickupAddress = itemView.findViewById(R.id.tvPickupAddress)
            tvDropAddress = itemView.findViewById(R.id.tvDropAddress)
            ivCancelled_stamp = itemView.findViewById(R.id.ivCancelled_stamp)
            cardView = itemView.findViewById(R.id.card)
        }
    }
}