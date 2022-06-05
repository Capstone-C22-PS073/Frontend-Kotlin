package com.capstonec22ps073.toursight.view.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.databinding.ActivityDetailLandmarkBinding
import com.capstonec22ps073.toursight.view.main.MainActivity

class DetailLandmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLandmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.containerContent.clipToOutline = true

        val culturalObject = intent.getParcelableExtra<CulturalObject>(DATA)
        val source = intent.getStringExtra(SOURCE)

        setData(culturalObject!!)

        binding.btnBack.setOnClickListener {
            if (source == "camera") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("HOME", true)
                startActivity(intent)
            } else {
                super.onBackPressed()
            }
        }

        setButtonFindLocation(culturalObject)
    }

    private fun setButtonFindLocation(culturalObject: CulturalObject) {
        if (culturalObject.category == "Landmark") {
            binding.btnMaps.visibility = View.VISIBLE
            binding.btnMaps.setOnClickListener { goToGmaps(culturalObject.location!!) }
        } else {
            binding.btnMaps.visibility = View.GONE
        }
    }

    private fun goToGmaps(mapLink: String) {
        val zoomLevel = 5
        val gmmIntentUri = Uri.parse("${mapLink}&z=$zoomLevel")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        startActivity(mapIntent)
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
            tvLocation.text = culturalObject.city
            tvDescription.text = culturalObject.deskripsi
            tvCategory.text = culturalObject.category
            tvHistory.text = culturalObject.history.toString()
        }
        setCategoryBackgroundColor(culturalObject.category!!)
    }

    private fun setCategoryBackgroundColor(category: String) {
        when (category) {
            "Landmark" -> {
                binding.tvCategory.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.tertiary_color)
            }
            "Food" -> {
                binding.tvCategory.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.quaternary_color)
            }
            else -> {
                binding.tvCategory.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.secondary_color)
            }
        }
    }

    companion object {
        const val DATA = "data"
        const val SOURCE = "source"
    }
}