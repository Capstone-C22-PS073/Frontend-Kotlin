package com.capstonec22ps073.toursight.view.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstonec22ps073.toursight.adapter.LIstLandmarkAdapter
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.databinding.FragmentHomeBinding
import com.capstonec22ps073.toursight.util.CustomDialog
import com.capstonec22ps073.toursight.util.Resource
import com.capstonec22ps073.toursight.view.detail.DetailLandmarkActivity
import com.capstonec22ps073.toursight.view.category.CategoryActivity
import com.capstonec22ps073.toursight.view.main.MainActivity
import com.capstonec22ps073.toursight.view.main.MainViewModel
import com.capstonec22ps073.toursight.view.search.SearchActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private var token = ""

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
                this.token = token
            }
        }

        viewModel.getUsername().observe(requireActivity()) { username ->
            if (username != "") {
                val greetingUser = String.format(getString(R.string.greeting), username)
                binding.tvGreeting.setText(greetingUser)
            }
        }

        viewModel.location.observe(requireActivity()) { location ->
            binding.tvLocation.setText(location)
        }

        viewModel.culturalObjects.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { culturalObjectresponse ->
                        if (culturalObjectresponse.size > 7) {
                            showRecycleList(getSubList(culturalObjectresponse))
                        } else {
                            showRecycleList(culturalObjectresponse)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                        if (message == "Token expired" || message == "Wrong Token or expired Token") {
                            AlertDialog.Builder(activity as MainActivity)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.token_exp_message))
                                .setCancelable(false)
                                .setPositiveButton("Ok") { _, _ ->
                                    activity?.let {
                                        viewModel.removeUserEmail()
                                        viewModel.removeUsername()
                                        viewModel.removeUserToken()
                                    }
                                }
                                .show()
                        } else if (message == "No Content") {
                            viewModel.getALlCulturalObjects(this.token)
                        } else if (message == "no internet connection") {
                            showDialogNoConnection()
                        } else {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        btnCategoryClickListener()

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
    }

    private fun btnCategoryClickListener() {
        binding.btnLandmark.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(CategoryActivity.TOKEN, token)
            intent.putExtra(CategoryActivity.CATEGORY, "landmark")
            startActivity(intent)
        }
        binding.btnCultural.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(CategoryActivity.TOKEN, token)
            intent.putExtra(CategoryActivity.CATEGORY, "culture")
            startActivity(intent)
        }
        binding.btnFood.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(CategoryActivity.TOKEN, token)
            intent.putExtra(CategoryActivity.CATEGORY, "food")
            startActivity(intent)
        }
    }

    private fun showDialogNoConnection() {
        val dialog = CustomDialog(requireActivity(), true, R.string.no_internet, R.string.no_internet_message)
        dialog.startDialogError()
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    fun <T> getSubList(list: List<T>): List<T>? {
        val subList: MutableList<T> = ArrayList()
        for (i in 0..7) {
            subList.add(list[i])
        }
        return subList
    }

    private fun showRecycleList(data: List<CulturalObject>?) {
        binding.rvLandmark.layoutManager = if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }

        val listUserAdapter = data?.let { LIstLandmarkAdapter(it) }
        binding.rvLandmark.adapter = listUserAdapter
        binding.rvLandmark.isNestedScrollingEnabled = false

        listUserAdapter?.setOnItemClickCallback(object : LIstLandmarkAdapter.OnItemClickCallback {
            override fun onItemClicked(culturalObject: CulturalObject, image: ImageView) {
                val intent = Intent(requireContext(), DetailLandmarkActivity::class.java)
                intent.putExtra(DetailLandmarkActivity.DATA, culturalObject)
                intent.putExtra(DetailLandmarkActivity.SOURCE, "recycle view")

                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        Pair(image, "culturalObject")
                    )

                startActivity(intent, optionCompat.toBundle())
            }
        })
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}