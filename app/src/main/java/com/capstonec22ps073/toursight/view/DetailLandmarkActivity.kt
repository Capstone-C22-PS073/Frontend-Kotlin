package com.capstonec22ps073.toursight.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.databinding.ActivityDetailLandmarkBinding

class DetailLandmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLandmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.containerContent.clipToOutline = true

        val culturalObject = intent.getParcelableExtra<CulturalObject>(DATA)
        val dataStatus = intent.getStringExtra(STATUS)

        if (dataStatus == "passing data") {
            setData(culturalObject!!)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setData(culturalObject: CulturalObject) {
        Glide.with(this)
            .load(culturalObject.image)
            .transform()
            .into(binding.imgBackground)
        binding.imgBackground.setColorFilter(Color.argb(155, 0, 0, 0))

        binding.imgMain.clipToOutline = true
        Glide.with(this)
            .load(culturalObject.image)
            .into(binding.imgMain)

        binding.apply {
            tvName.text = culturalObject.name
            tvLocation.text = culturalObject.location
            tvDescription.text = culturalObject.deskripsi
            tvCategory.text = culturalObject.category
        }
        setCategoryBackgroundColor(culturalObject.category!!)
    }

    private fun setCategoryBackgroundColor(category: String) {
        when (category) {
            "Landmark" -> {
                binding.tvCategory.backgroundTintList = ContextCompat.getColorStateList(this, R.color.tertiary_color)
            }
            "Food" -> {
                binding.tvCategory.backgroundTintList = ContextCompat.getColorStateList(this, R.color.quaternary_color)
            }
            else -> {
                binding.tvCategory.backgroundTintList = ContextCompat.getColorStateList(this, R.color.secondary_color)
            }
        }
    }

    companion object {
        const val DATA = "data"
        const val STATUS = "status"
    }
}