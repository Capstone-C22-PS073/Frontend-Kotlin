package com.capstonec22ps073.toursight.view.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.capstonec22ps073.toursight.OnBoardingAdapter
import com.capstonec22ps073.toursight.OnBoardingItem
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.data.FirstInstallDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityOnBoardingBinding
import com.capstonec22ps073.toursight.repository.FirstInstallRepository
import com.capstonec22ps073.toursight.view.login.LoginActivity
import com.capstonec22ps073.toursight.view.splash.MainViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "is_first_install")

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private lateinit var viewModel: OnBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnBoardingItems()
        setIndicators()
        setCurrentIndicator(0)

        val pref = FirstInstallDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(FirstInstallRepository(pref))).get(
            OnBoardingViewModel::class.java
        )
        
        binding.btnStarted.setOnClickListener {
            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
            viewModel.saveUserStatusAsTrue()
        }
    }

    private fun setOnBoardingItems() {
        onBoardingAdapter = OnBoardingAdapter(
            listOf(
                OnBoardingItem(
                    R.drawable.ic_banner_onboarding1,
                    "Capture All Landmark",
                    "quickly recognise a landmark by using your camera phone."
                ),
                OnBoardingItem(
                    R.drawable.ic_banner_onboarding2,
                    "Gain Information",
                    "gain information about the history and the data about it's landmark."
                ),
            )
        )

        binding.onBoardingViewPager.adapter = onBoardingAdapter
        binding.onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setIndicators() {
        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        layoutParams.setMargins(0, 0, 20, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.apply {
                this.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

}