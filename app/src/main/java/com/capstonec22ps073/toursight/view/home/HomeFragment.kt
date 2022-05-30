package com.capstonec22ps073.toursight.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstonec22ps073.toursight.LIstLandmarkAdapter
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.databinding.FragmentHomeBinding
import com.capstonec22ps073.toursight.util.Resource
import com.capstonec22ps073.toursight.view.DetailLandmarkActivity
import com.capstonec22ps073.toursight.view.login.LoginActivity
import com.capstonec22ps073.toursight.view.main.MainActivity
import com.capstonec22ps073.toursight.view.main.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerHome.clipToOutline = true

        viewModel = (activity as MainActivity).viewModel

        viewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token != "") {
                viewModel.getALlToursight(token)
            }
        }

        viewModel.toursights.observe(viewLifecycleOwner, Observer { response ->
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
                        Log.e(TAG, "An error occured: $message")
                        if (message == "Token expired" || message == "Wrong Token or expired Token") {
//                            AlertDialog.Builder(activity as MainActivity)
//                                .setTitle(getString(R.string.error))
//                                .setMessage(getString(R.string.token_exp_message))
//                                .setCancelable(false)
//                                .setPositiveButton("Ok") { _, _ ->
////                                    activity?.let {
//                                        val intent = Intent(requireContext(), LoginActivity::class.java)
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                        startActivity(intent)
////                                        Log.e(TAG, "intent")
////                                    }
                                    Log.e(TAG, "Dialog on click")
//                                }
//                                .show()
                        }
                    }
                }
            }

        })
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun showRecycleList(data: List<CulturalObject>?) {
        binding.rvLandmark.layoutManager = LinearLayoutManager(requireContext())

        val listUserAdapter = data?.let { LIstLandmarkAdapter(it) }
        binding.rvLandmark.adapter = listUserAdapter
        binding.rvLandmark.isNestedScrollingEnabled = false

        listUserAdapter?.setOnItemClickCallback(object : LIstLandmarkAdapter.OnItemClickCallback {
            override fun onItemClicked(culturalObject: CulturalObject) {
                val intent = Intent(requireContext(), DetailLandmarkActivity::class.java)
                intent.putExtra(DetailLandmarkActivity.DATA, culturalObject)
                intent.putExtra(DetailLandmarkActivity.STATUS, "passing data")
                startActivity(intent)
            }
        })
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}