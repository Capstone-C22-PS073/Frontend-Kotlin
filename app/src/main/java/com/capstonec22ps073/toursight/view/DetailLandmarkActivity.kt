package com.capstonec22ps073.toursight.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.databinding.ActivityDetailLandmarkBinding

class DetailLandmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLandmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.containerContent.clipToOutline = true
        setImage()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }


    private fun setImage() {
        val imageUrl = resources.getStringArray(R.array.data_landmark_image)

        Glide.with(this)
            .load(imageUrl[0])
            .transform()
            .into(binding.imgBackground)

        binding.imgBackground.setColorFilter(Color.argb(155, 0, 0, 0))

        binding.imgMain.clipToOutline = true

        Glide.with(this)
            .load(imageUrl[0])
            .into(binding.imgMain)

    }
}