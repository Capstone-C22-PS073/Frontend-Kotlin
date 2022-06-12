package com.capstonec22ps073.toursight.view.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstonec22ps073.toursight.adapter.LIstLandmarkAdapter
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivitySearchBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.CustomDialog
import com.capstonec22ps073.toursight.util.Resource
import com.capstonec22ps073.toursight.view.detail.DetailLandmarkActivity
import com.capstonec22ps073.toursight.view.login.LoginActivity
import com.capstonec22ps073.toursight.view.main.MainViewModelFactory
import java.util.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class SearchActivity : AppCompatActivity(), TextView.OnEditorActionListener, View.OnKeyListener {
    lateinit var binding: ActivitySearchBinding
    lateinit var viewModel: SearchViewModel
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application, AuthRepository(pref), CulturalObjectRepository())
        ).get(
            SearchViewModel::class.java
        )

        viewModel.getUserToken().observe(this) { token ->
            if (token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                this.token = token
            }
        }

        viewModel.search.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { culturalObjectsResponse ->
                        if (culturalObjectsResponse.isEmpty()) {
                            showRecycleList(ArrayList())
                            showErrorMessage(true)
                        } else {
                            showErrorMessage(false)
                            showRecycleList(culturalObjectsResponse)
                        }
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
                        } else if (message == "no internet connection") {
                            showDialogNoConnection()
                        } else if (message == "network failure" || message == "conversion error") {
                            showDialogNoConnection()
                        } else {
                            showErrorMessage(true)
                            showRecycleList(ArrayList())

                        }
                    }
                }
            }

        }

        binding.btnBack.setOnClickListener { finish() }

        binding.etSearch.setOnEditorActionListener(this)
    }

    private fun showDialogNoConnection() {
        val dialog = CustomDialog(this, true, R.string.no_internet, R.string.no_internet_message)
        dialog.startDialogError()
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
            if (binding.etSearch.text?.isNotEmpty()!!) {
                viewModel.getCulturalObjectBasedOnSearch(
                    this.token,
                    binding.etSearch.text.toString()
                )
            }
            binding.etSearch.clearFocus()
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            return true
        }
        return false
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        return true
    }

    private fun showErrorMessage(status: Boolean) {
        if (status) {
            binding.lottieEmptyContent.visibility = View.VISIBLE
            binding.tvEmptyContent.visibility = View.VISIBLE
        } else {
            binding.lottieEmptyContent.visibility = View.INVISIBLE
            binding.tvEmptyContent.visibility = View.INVISIBLE
        }
    }

    private fun showRecycleList(data: List<CulturalObject>?) {
        binding.rvSearch.layoutManager = if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }

        val listUserAdapter = data?.let { LIstLandmarkAdapter(it) }
        binding.rvSearch.adapter = listUserAdapter

        listUserAdapter?.setOnItemClickCallback(object : LIstLandmarkAdapter.OnItemClickCallback {
            override fun onItemClicked(culturalObject: CulturalObject, image: ImageView) {
                val intent = Intent(this@SearchActivity, DetailLandmarkActivity::class.java)
                intent.putExtra(DetailLandmarkActivity.DATA, culturalObject)
                intent.putExtra(DetailLandmarkActivity.SOURCE, "recycle view")

                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@SearchActivity,
                        Pair(image, "culturalObject")
                    )

                startActivity(intent, optionCompat.toBundle())
            }
        })
    }

    companion object {
        const val TAG = "SearchActivity"
    }

}