package com.suviridedriver.ui.language

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.common.collect.ImmutableList
import com.suviridedriver.R
import com.suviridedriver.model.languages.LanguagesDetails
import com.suviridedriver.ui.number_verification.MobileNumberActivity
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.TAG


class
LanguageAdapter(val context: Context, val arrayList: List<LanguagesDetails>, val button: TextView) :
    RecyclerView.Adapter<LanguageAdapter.RecyclerViewHolder>() {
    private var lastSelectedPosition: Int? = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_list, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.rbLanguage.text = arrayList[position].name
        // holder.rbLanguage.isChecked = lastSelectedPosition == position;
        holder.rbLanguage.setChecked(lastSelectedPosition == position);
        button.setOnClickListener {
            // Toast.makeText(context,"$lastSelectedPosition",Toast.LENGTH_SHORT).show()
            if(lastSelectedPosition != -1){
               AppSession.save(context, Constants.LANGUAGE_ID,arrayList[lastSelectedPosition!!]._id)
                Log.d(TAG,arrayList[lastSelectedPosition!!]._id)
              // context.startActivity(Intent(context, WelcomeActivity::class.java))
               context.startActivity(Intent(context, MobileNumberActivity::class.java))
            }
            else{
                Toast.makeText(context,"Please select Language first.", Toast.LENGTH_SHORT).show()
            }
        }
        if(position == arrayList.size-1){
            holder.view.visibility = View.GONE
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rbLanguage: RadioButton
        var view:View
        init {
            rbLanguage = itemView.findViewById(R.id.rbLanguage)
            view = itemView.findViewById(R.id.view)
            rbLanguage.setOnClickListener(View.OnClickListener {
                lastSelectedPosition = adapterPosition
                if(adapterPosition != -1)
                    notifyDataSetChanged()
            })
        }
    }
}