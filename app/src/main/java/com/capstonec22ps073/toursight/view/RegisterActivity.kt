package com.capstonec22ps073.toursight.view

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstonec22ps073.toursight.databinding.ActivityRegristerBinding

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
            Toast.makeText(this, "register berhasil", Toast.LENGTH_SHORT).show()
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
}