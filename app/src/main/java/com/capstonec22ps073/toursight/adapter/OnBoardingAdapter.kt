package com.capstonec22ps073.toursight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstonec22ps073.toursight.model.OnBoardingItem
import com.capstonec22ps073.toursight.databinding.OnboardingItemContainerBinding

class OnBoardingAdapter(private val onBoardingItem: List<OnBoardingItem>) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    class OnBoardingViewHolder(var binding: OnboardingItemContainerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding =OnboardingItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.binding.onboardingImage.setImageResource(onBoardingItem[position].onBoardingImage)
        holder.binding.tvOnBoardingTitle.text = onBoardingItem[position].title
        holder.binding.tvOnBoardingDescription.text = onBoardingItem[position].description
    }

    override fun getItemCount(): Int {
        return onBoardingItem.size
    }
}