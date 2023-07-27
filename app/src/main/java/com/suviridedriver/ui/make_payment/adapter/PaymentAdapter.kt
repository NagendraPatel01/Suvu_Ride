package com.suviridedriver.ui.make_payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.suviridedriver.R
import com.suviridedriver.model.ride_packages.Package

class PaymentAdapter(val packages: List<Package>, val context: Context,
    private var onSelectRides: ((Package) -> Unit)
) :
    RecyclerView.Adapter<PaymentAdapter.RecyclerViewHolder>() {
    var clList = ArrayList<ConstraintLayout>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_list_item, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.tvDescription.text = packages[position].description
        holder.tvAmount.text = packages[position].price.toString()
        holder.tvRides.text = packages[position].totalRides.toString()
        holder.tvValidity.text = packages[position].validity
        holder.tvPack.text = packages[position].name


        holder.clMain.setOnClickListener {
            for (l in clList) {
                l.setBackgroundResource(0)
            }
            holder.clMain.setBackgroundResource(R.drawable.payment_select_bg)
            onSelectRides(packages[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDescription: TextView
        var tvAmount: TextView
        var tvRides: TextView
        var tvValidity: TextView
        var tvPack: TextView
        var clMain: ConstraintLayout

        init {
            tvDescription = itemView.findViewById(R.id.tvDescription)
            tvAmount = itemView.findViewById(R.id.tvAmount)
            tvRides = itemView.findViewById(R.id.tvRides)
            tvValidity = itemView.findViewById(R.id.tvValidity)
            tvPack = itemView.findViewById(R.id.packText)
            clMain = itemView.findViewById(R.id.clMain)
            clList.add(clMain)
        }
    }
}
