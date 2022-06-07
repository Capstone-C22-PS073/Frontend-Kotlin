package com.capstonec22ps073.toursight.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.ApiConfig
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.ResponseLogin
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityLoginBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.view.AuthViewModelFactory
import com.capstonec22ps073.toursight.view.main.MainActivity
import com.capstonec22ps073.toursight.view.register.RegisterActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, AuthViewModelFactory(AuthRepository(pref))).get(
            LoginViewModel::class.java
        )

        viewModel.getUserToken().observe(this) { token ->
            if (token != "") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener{
            login()
        }

        binding.tvBtnSignup.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()

        val isEmailValid = checkEmailValid()
        val isPasswordValid = checkPasswordValid()

        if (isEmailValid && isPasswordValid) {
            loginUser()
        }
    }

    private fun loginUser() {
        val passwordText = binding.etPassword.text.toString()
        val emailText = binding.etEmail.text.toString()
        val client = ApiConfig.getApiService().login(emailText, passwordText)
        showLoading(true)
        client.enqueue(object : retrofit2.Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.accessToken != null) {
                            saveTokenUser(responseBody.accessToken)
                            saveUsername(responseBody.username!!)
                            saveUserEmail(responseBody.email!!)
                        }
                        Log.e(TAG, "status: ${responseBody.accessToken}")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(this@LoginActivity, errorResponse?.msg, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message} fail")
                showAlert(getString(R.string.error), getString(R.string.error_something_went_wrong))
            }
        })
    }

    private fun checkEmailValid(): Boolean {
        val emailText = binding.etEmail.text.toString()
        if (emailText.isEmpty()) {
            binding.etEmailLayout.error = getString(R.string.error_et_empty)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            binding.etEmailLayout.error = getString(R.string.error_email_format)
            return false
        }
        return true
    }

    private fun checkPasswordValid(): Boolean {
        val passwordText = binding.etPassword.text.toString()
        if (passwordText.isEmpty()) {
            binding.etPasswordLayout.error =  getString(R.string.error_et_empty)
            return false
        }
        return true
    }

    private fun saveTokenUser(token: String) {
        viewModel.saveUserToken(token)
    }

    private fun saveUsername(username: String) {
        viewModel.saveUsername((username))
    }

    private fun saveUserEmail(email: String) {
        viewModel.saveUserEmail((email))
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            binding.progressCircular.visibility = View.VISIBLE
            binding.apply {
                etEmail.isEnabled = false
                etPassword.isEnabled = false
                btnLogin.isEnabled = false
                tvBtnSignup.isEnabled = false
            }
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
            binding.apply {
                etEmail.isEnabled = true
                etPassword.isEnabled = true
                btnLogin.isEnabled = true
                tvBtnSignup.isEnabled = true
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                // do nothing
            }.show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}