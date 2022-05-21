package com.capstonec22ps073.toursight.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstonec22ps073.toursight.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Toast.makeText(this, "login berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
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
        return true
    }

    private fun checkPasswordValid(): Boolean {
        val passwordText = binding.etPassword.text.toString()
        if (passwordText.isEmpty()) {
            binding.etPasswordLayout.error =  "Field can not be empty"
            return false
        }
        return true
    }
}