package com.capstonec22ps073.toursight

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.databinding.ItemRowLandmarkBinding

class LIstLandmarkAdapter(private val listLandmark: ArrayList<Landmark>): RecyclerView.Adapter<LIstLandmarkAdapter.LandmarkViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class LandmarkViewHolder(var binding: ItemRowLandmarkBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandmarkViewHolder {
        val binding = ItemRowLandmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LandmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LandmarkViewHolder, position: Int) {
        val (name, description, photo) = listLandmark[position]

        holder.binding.tvItemName.text = name
        holder.binding.tvItemDescription.text = description

        Glide.with(holder.itemView.context)
            .load(photo)
            .into(holder.binding.imgItemImage)

        holder.binding.imgItemImage.clipToOutline = true

        holder.binding.imgItemImage.setOnClickListener { onItemClickCallback.onItemClicked() }
    }

    override fun getItemCount(): Int = listLandmark.size

    interface OnItemClickCallback {
        fun onItemClicked()
    }
}