package com.capstonec22ps073.toursight.view.category

import android.content.Context
import android.content.Intent
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
import com.capstonec22ps073.toursight.LIstLandmarkAdapter
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityCategoryBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.capstonec22ps073.toursight.view.detail.DetailLandmarkActivity
import com.capstonec22ps073.toursight.view.home.HomeFragment
import com.capstonec22ps073.toursight.view.main.MainViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: CategoryViewModel

    private var token = ""
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(TOKEN) as String
        category = intent.getStringExtra(CATEGORY) as String

        setSupportActionBar(binding.toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = getToolbarTitle(category)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(AuthRepository(pref), CulturalObjectRepository())
        ).get(
            CategoryViewModel::class.java
        )

        if (token.isNotEmpty() && category.isNotEmpty()) {
            viewModel.getCulturalObjectsByCategory(token, category)
        }

        viewModel.culturalObjects.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        showRecycleList(newsResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(HomeFragment.TAG, "An error occured: $message")
                        if (message == "Token expired" || message == "Wrong Token or expired Token") {
                            AlertDialog.Builder(this)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.token_exp_message))
                                .setCancelable(false)
                                .setPositiveButton("Ok") { _, _ ->
                                        viewModel.removeUserDataFromDataStore()
                                }
                                .show()
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

    private fun getToolbarTitle(category: String): String {
        return when (category) {
            "landmark" -> {
                getString(R.string.landmark)
            }
            "food" -> {
                getString(R.string.food)
            }
            else -> {
                getString(R.string.culture)
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun showRecycleList(data: List<CulturalObject>?) {
        binding.rvCategory.layoutManager = LinearLayoutManager(this)

        val listUserAdapter = data?.let { LIstLandmarkAdapter(it) }
        binding.rvCategory.adapter = listUserAdapter
        binding.rvCategory.isNestedScrollingEnabled = false

        listUserAdapter?.setOnItemClickCallback(object : LIstLandmarkAdapter.OnItemClickCallback {
            override fun onItemClicked(culturalObject: CulturalObject) {
                val intent = Intent(this@CategoryActivity, DetailLandmarkActivity::class.java)
                intent.putExtra(DetailLandmarkActivity.DATA, culturalObject)
                intent.putExtra(DetailLandmarkActivity.SOURCE, "recycle view")
                startActivity(intent)
            }
        })
    }

    companion object {
        const val CATEGORY = "category"
        const val TOKEN = "token"
    }
}