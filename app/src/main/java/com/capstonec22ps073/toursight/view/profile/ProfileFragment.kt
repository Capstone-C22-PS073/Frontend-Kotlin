package com.capstonec22ps073.toursight.view.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstonec22ps073.toursight.databinding.FragmentProfileBinding
import com.capstonec22ps073.toursight.view.main.MainActivity
import com.capstonec22ps073.toursight.view.main.MainViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.btnLogout.setOnClickListener {
            viewModel.removeUserToken()
            viewModel.removeUsername()
            viewModel.removeUserEmail()
        }

        setUserData()
    }

    private fun setUserData() {
        viewModel.getUserEmail().observe(requireActivity()) { email ->
            binding.tvEmail.text = email
        }

        viewModel.getUsername().observe(requireActivity()) { username ->
            binding.tvUsername.text = username
        }
    }
}