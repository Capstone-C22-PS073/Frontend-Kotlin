package com.capstonec22ps073.toursight.view.register

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.ApiConfig
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.ResponseRegister
import com.capstonec22ps073.toursight.databinding.ActivityRegristerBinding
import com.capstonec22ps073.toursight.util.CustomDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

class RegisterActivity: AppCompatActivity() {
    lateinit var binding: ActivityRegristerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegristerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            register()
        }

        binding.tvBtnLogin.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun register() {
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()
        binding.etName.clearFocus()

        val isEmailValid = checkEmailValid()
        val isPasswordValid = checkPasswordValid()
        val isNameValid = checkNameValid()

        if (isEmailValid && isPasswordValid && isNameValid) {
            registerUser()
        }
    }

    private fun checkNameValid(): Boolean{
        val nameText = binding.etName.text.toString()
        if (nameText.isEmpty()) {
            binding.etNameLayout.error = getString(R.string.error_et_empty)
            return false
        }
        binding.etNameLayout.error = null
        return true
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
        binding.etEmailLayout.error = null
        return true
    }

    private fun checkPasswordValid(): Boolean {
        val passwordText = binding.etPassword.text.toString()
        if (passwordText.isEmpty()) {
            binding.etPasswordLayout.error =  getString(R.string.error_et_empty)
            return false
        }
        if (passwordText.length < 8) {
            binding.etPasswordLayout.error = getString(R.string.error_password_6char)
            return false
        }
        binding.etPasswordLayout.error = null
        return true
    }

    private fun registerUser() {
        val nameText = binding.etName.text.toString()
        val passwordText = binding.etPassword.text.toString()
        val emailText = binding.etEmail.text.toString()
        val client = ApiConfig.getApiService().register(nameText, emailText, passwordText)
        showLoading(true)
        client.enqueue(object : retrofit2.Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.msg != null) {
                        showDialogSuccess()
                        Log.e(TAG, "status: ${responseBody.msg}")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.errorBody()}")
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(this@RegisterActivity, "${errorResponse?.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message} fail")
                showAlert(getString(R.string.error), getString(R.string.error_something_went_wrong))
            }
        })
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            binding.progressCircular.visibility = View.VISIBLE
            binding.apply {
                etEmail.isEnabled = false
                etPassword.isEnabled = false
                etName.isEnabled = false
                btnSignup.isEnabled = false
                tvBtnLogin.isEnabled = false
            }
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
            binding.apply {
                etEmail.isEnabled = true
                etPassword.isEnabled = true
                etName.isEnabled = true
                btnSignup.isEnabled = true
                tvBtnLogin.isEnabled = true
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

    private fun showDialogSuccess() {
        val successDialog = CustomDialog(this, false, R.string.success, R.string.success_signup_message)
        successDialog.startDialogError()
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}