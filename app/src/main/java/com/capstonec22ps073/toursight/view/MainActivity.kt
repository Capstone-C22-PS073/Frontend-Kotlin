package com.capstonec22ps073.toursight.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null
    }
}