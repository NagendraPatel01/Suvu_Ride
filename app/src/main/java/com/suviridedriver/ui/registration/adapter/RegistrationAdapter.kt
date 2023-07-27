package com.suviridedriver.ui.registration.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suviridedriver.R
import com.suviridedriver.ui.driving_licence.DrivingLicenceActivity
import com.suviridedriver.ui.make_payment.MakePaymentActivity
import com.suviridedriver.ui.persnal_details.PersonalDetailsActivity
import com.suviridedriver.ui.registration.RegistrationModel
import com.suviridedriver.ui.take_selfie.TakeSelfieActivity
import com.suviridedriver.ui.vehicle_detail.VehicleDetailActivity

class RegistrationAdapter(val ct:Context,val array: ArrayList<RegistrationModel>) : RecyclerView.Adapter<RegistrationAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title:TextView
        var description : TextView
        var img :ImageView
        var status:ImageView
        var ll : LinearLayout
        var top:ImageView
        var bottom : ImageView
        init {
            title = itemView.findViewById(R.id.title)
            description = itemView.findViewById(R.id.description)
            img = itemView.findViewById(R.id.img)
            status = itemView.findViewById(R.id.status)
            ll = itemView.findViewById(R.id.rv_dl)
            top = itemView.findViewById(R.id.aboveImg)
            bottom = itemView.findViewById(R.id.bottomImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ct).inflate(R.layout.registration_item,parent,false))
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = array[position].title
        holder.description.text = array[position].body
        holder.img.setImageResource(array[position].image)
        if (position == 0) {
            holder.top.visibility = View.INVISIBLE
        }
        if (position == array.size - 1) {
            holder.bottom.visibility = View.INVISIBLE
        }
        holder.ll.setOnClickListener {
            when(holder.title.text.toString()){
                array[0].title -> if(!array[0].check){
                    ct.startActivity(Intent(ct, DrivingLicenceActivity::class.java))
                }
                array[1].title -> if(array[0].check && !array[1].check){
                    ct.startActivity(Intent(ct, VehicleDetailActivity::class.java))
                }
                array[2].title -> if(array[1].check && !array[2].check) {
                    ct.startActivity(Intent(ct, PersonalDetailsActivity::class.java))
                }
                array[3].title -> if(array[2].check && !array[3].check){
                    ct.startActivity(Intent(ct, TakeSelfieActivity::class.java))
                }
                array[4].title -> if(array[3].check && !array[4].check){
                    ct.startActivity(Intent(ct, MakePaymentActivity::class.java))
                }
            }
        }
        if(array[position].check){
            holder.status.setImageResource(R.drawable.registration_check)
            holder.ll.setBackgroundResource(R.drawable.square_bg_yrllo)
        }else{
            holder.status.setImageResource(R.drawable.registration_uncheck)
            holder.ll.setBackgroundResource(R.drawable.filed_bg)
        }

        if (position==0&&!array[position].check)
        {
            holder.status.setImageResource(R.drawable.registration_eligible)
            holder.ll.setBackgroundResource(R.drawable.filed_bg)
        }
        else if (position!=0&&array[position-1].check&&!array[position].check)
        {
            holder.status.setImageResource(R.drawable.registration_eligible)
            holder.ll.setBackgroundResource(R.drawable.filed_bg)
        }
    }
}