package com.capstonec22ps073.toursight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.ImageUploadedByUser
import com.capstonec22ps073.toursight.databinding.ItemRowImageUploadedByUserBinding

class ListImageAdapter(
    private val listImageUploadedByUser: List<ImageUploadedByUser>
) : RecyclerView.Adapter<ListImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(var binding: ItemRowImageUploadedByUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            ItemRowImageUploadedByUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = listImageUploadedByUser[position]

        val uploadedAt = holder.itemView.context.getString(R.string.uploaded_at)
        holder.binding.tvItemCreatedAt.text = String.format(uploadedAt, data.createdAt)

        Glide.with(holder.itemView.context)
            .load(data.image)
            .into(holder.binding.imgItemImage)

        holder.binding.imgItemImage.clipToOutline = true
    }

    override fun getItemCount(): Int = listImageUploadedByUser.size
}