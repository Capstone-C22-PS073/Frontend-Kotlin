package com.capstonec22ps073.toursight.view.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.data.FirstInstallDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivitySplashBinding
import com.capstonec22ps073.toursight.repository.FirstInstallRepository
import com.capstonec22ps073.toursight.view.MainViewModelFactory
import com.capstonec22ps073.toursight.view.main.MainActivity
import com.capstonec22ps073.toursight.view.onboarding.OnBoardingActivity

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "is_first_install")

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.ivLogo.alpha = 0f
        binding.ivTextLogo.alpha = 0f

        val pref = FirstInstallDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(FirstInstallRepository(pref))).get(
            SplashViewModel::class.java
        )

        binding.ivTextLogo.animate().setDuration(1500).alpha(1f)
        binding.ivLogo.animate().setDuration(1500).alpha(1f).withEndAction {
            viewModel.getUserFirstInstallStatus().observe(this) { status ->
                val intent = if (status) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, OnBoardingActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}