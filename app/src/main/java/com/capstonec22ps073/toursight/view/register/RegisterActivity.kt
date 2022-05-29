package com.capstonec22ps073.toursight.view.register

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstonec22ps073.toursight.api.ApiConfig
import com.capstonec22ps073.toursight.api.ResponseLogin
import com.capstonec22ps073.toursight.api.ResponseRegister
import com.capstonec22ps073.toursight.databinding.ActivityRegristerBinding
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
            finish()
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
            binding.etNameLayout.error = "Field can not be empty"
            return false
        }
        binding.etNameLayout.error = null
        return true
    }

    private fun checkEmailValid(): Boolean {
        val emailText = binding.etEmail.text.toString()
        if (emailText.isEmpty()) {
            binding.etEmailLayout.error = "Field can not be empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            binding.etEmailLayout.error = "Please enter a valid email address"
            return false
        }
        binding.etEmailLayout.error = null
        return true
    }

    private fun checkPasswordValid(): Boolean {
        val passwordText = binding.etPassword.text.toString()
        if (passwordText.isEmpty()) {
            binding.etPasswordLayout.error =  "Field can not be empty"
            return false
        }
        if (passwordText.length < 8) {
            binding.etPasswordLayout.error = "Password must have at least 6 characters"
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
                        Toast.makeText(
                            this@RegisterActivity,
                            responseBody.msg,
                            Toast.LENGTH_LONG
                        ).show()
                        showAlert("Sukses", "Registrasi telah berhasil, silahkan kembali ke halaman Login untuk masuk ke dalam aplikasi")
                        Log.e(TAG, "status: ${responseBody.msg}")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.body()?.message}")
                    val gson = Gson()
                    val type = object : TypeToken<ResponseRegister>() {}.type
                    val errorResponse: ResponseRegister? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(this@RegisterActivity, "${errorResponse?.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message} fail")
                showAlert("Gagal", "Terjadi kesalahan harap coba lagi nanti")
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

    companion object {
        private const val TAG = "RegisterActivity"
    }
}