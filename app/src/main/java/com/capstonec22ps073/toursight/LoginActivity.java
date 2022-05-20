package com.capstonec22ps073.toursight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.capstonec22ps073.toursight.databinding.ActivityLoginBinding;
import com.capstonec22ps073.toursight.databinding.OnboardingItemContainerBinding;


public class LoginActivity extends Activity {
    Button btnRegister;
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    btnRegister = findViewById(R.id.btn_register);
    btnRegister.setOnClickListener(view -> {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    });
}

    }
