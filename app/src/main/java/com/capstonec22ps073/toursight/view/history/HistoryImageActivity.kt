package com.capstonec22ps073.toursight.view.history

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.adapter.ListImageAdapter
import com.capstonec22ps073.toursight.api.ImageUploadedByUser
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityHistoryImageHistoryBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.capstonec22ps073.toursight.view.main.MainViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class HistoryImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryImageHistoryBinding
    private lateinit var viewModel: HistoryImageViewModel

    private var token = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryImageHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = "Image Uploaded"
        actionbar.setDisplayHomeAsUpEnabled(true)

        token = intent.getStringExtra(TOKEN) as String
        username = intent.getStringExtra(USERNAME) as String

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(AuthRepository(pref), CulturalObjectRepository())).get(
            HistoryImageViewModel::class.java
        )

        if (token.isNotEmpty() && username.isNotEmpty()) {
            viewModel.getImageUploadedByUser(token, username)
        }

        viewModel.imagesHistory.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { listResponse ->
                        showEmptyContentLottie(false)
                        showRecycleList(listResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                        if (message == "Token expired" || message == "Wrong Token or expired Token") {
                            AlertDialog.Builder(this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.token_exp_message))
                                .setCancelable(false)
                                .setPositiveButton("Ok") { _, _ ->
                                    viewModel.removeUserDataFromDataStore()
                                }
                                .show()
                        } else if (message == "No Content") {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            showEmptyContentLottie(true)
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun showEmptyContentLottie(status: Boolean) {
        if (status) {
            binding.lottieEmptyContent.visibility = View.VISIBLE
        } else {
            binding.lottieEmptyContent.visibility = View.GONE
        }
    }

    private fun showRecycleList(data: List<ImageUploadedByUser>?) {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        val listUserAdapter = data?.let { ListImageAdapter(it) }
        binding.rvHistory.adapter = listUserAdapter
        binding.rvHistory.isNestedScrollingEnabled = false
    }

    companion object {
        const val TOKEN = "token"
        const val USERNAME = "username"
        private const val TAG = "HistoryImageActivity"
    }
}