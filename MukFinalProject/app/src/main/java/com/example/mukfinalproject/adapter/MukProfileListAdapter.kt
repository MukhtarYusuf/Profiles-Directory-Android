package com.example.mukfinalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mukfinalproject.R
import com.example.mukfinalproject.viewmodel.MukSharedProfilesViewModel
import kotlinx.android.synthetic.main.muk_profile_view_item.view.*

class MukProfileListAdapter(
    private var mukProfileViews: List<MukSharedProfilesViewModel.MukProfileView>?,
    private var mukProfileListAdapterListener: MukProfileListAdapterListener,
    private var mukContext: Context?
): RecyclerView.Adapter<MukProfileListAdapter.MukViewHolder>() {

    // Interfaces
    interface MukProfileListAdapterListener {
        fun mukOnProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView)
    }

    // View Holder
    class MukViewHolder(view: View,
                        private val mukProfileListAdapterListener: MukProfileListAdapterListener): RecyclerView.ViewHolder(view) {

        var mukProfileImageView: ImageView = view.findViewById(R.id.mukProfileImageView)
        var mukNameTextView: TextView = view.findViewById(R.id.mukNameTextView)
        var mukBirthdayTextView: TextView = view.findViewById(R.id.mukBirthdayTextView)
        var mukGenderTextView: TextView = view.findViewById(R.id.mukGenderTextView)
        var mukCountryTextView: TextView = view.findViewById(R.id.mukCountryTextView)

        init {
            view.setOnClickListener {
                val mukProfileView = itemView.tag as? MukSharedProfilesViewModel.MukProfileView
                mukProfileView?.let {
                    mukProfileListAdapterListener.mukOnProfileClicked(it)
                }
            }
        }
    }

    // Adapter Methods
    override fun getItemCount(): Int {
        return mukProfileViews?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MukProfileListAdapter.MukViewHolder {
        val mukView = LayoutInflater.from(parent.context)
                .inflate(R.layout.muk_profile_view_item, parent, false)

        return MukProfileListAdapter.MukViewHolder(mukView, mukProfileListAdapterListener)
    }

    override fun onBindViewHolder(holder: MukProfileListAdapter.MukViewHolder, position: Int) {
            val mukProfileViews = mukProfileViews ?: return

            val mukProfileView = mukProfileViews[position]
            holder.itemView.tag = mukProfileView

            mukContext?.let {
                val mukBitmapImage = mukProfileView.mukGetProfileImage(it)
                holder.mukProfileImageView.setImageBitmap(mukProfileView.mukGetProfileImage(it))
            }

            holder.mukNameTextView.text = mukProfileView.mukName
            holder.mukBirthdayTextView.text = mukProfileView.mukBirthday
            holder.mukGenderTextView.text = mukProfileView.mukGender
            holder.mukCountryTextView.text = mukProfileView.mukCountry
    }

    // Methods
    fun mukSetProfileViews(mukProfileViewsData: List<MukSharedProfilesViewModel.MukProfileView>) {
        mukProfileViews = mukProfileViewsData
        notifyDataSetChanged()
    }
}