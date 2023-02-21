package com.example.mukfinalproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mukfinalproject.R
import kotlinx.android.synthetic.main.muk_text_item.view.*

class MukChooserAdapter(
    private var mukChoices: List<String>,
    private var mukChooserAdapterListener: MukChooserAdapterListener?
): RecyclerView.Adapter<MukChooserAdapter.MukViewHoler>() {

    // Interfaces
    interface MukChooserAdapterListener {
        fun mukOnChooseItem(mukChoice: String)
    }

    // View Holder
    class MukViewHoler(view: View,
        private val mukChooserAdapterListener: MukChooserAdapterListener?): RecyclerView.ViewHolder(view) {

        val mukOptionTextView: TextView = view.findViewById(R.id.mukOptionTextView)

        init {
            view.setOnClickListener {
                val mukChoice = itemView.tag as? String
                mukChoice?.let {
                    mukChooserAdapterListener?.mukOnChooseItem(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mukChoices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MukViewHoler {
        val mukView = LayoutInflater.from(parent.context).inflate(R.layout.muk_text_item, parent, false)

        return MukViewHoler(mukView, mukChooserAdapterListener)
    }

    override fun onBindViewHolder(holder: MukViewHoler, position: Int) {
        holder.itemView.tag = mukChoices[position]
        holder.mukOptionTextView.text = mukChoices[position]
    }
}