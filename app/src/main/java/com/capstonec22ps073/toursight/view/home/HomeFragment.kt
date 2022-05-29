package com.capstonec22ps073.toursight.view.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstonec22ps073.toursight.LIstLandmarkAdapter
import com.capstonec22ps073.toursight.Landmark
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.databinding.FragmentHomeBinding
import com.capstonec22ps073.toursight.view.DetailLandmarkActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var list = ArrayList<Landmark>()


    private val listLandmark: ArrayList<Landmark>
        get() {
            val dataName = resources.getStringArray(R.array.data_landmark_name)
            val dataDescription = resources.getStringArray(R.array.data_landmark_description)
            val dataImage = resources.getStringArray(R.array.data_landmark_image)

            val listData = ArrayList<Landmark>()

            for (i in dataName.indices) {
                val data = Landmark(
                    dataName[i],
                    dataDescription[i],
                    dataImage[i]
                )
                listData.add(data)
            }

            return listData
        }

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

        list.addAll(listLandmark)
        showRecycleList()
    }

    private fun showRecycleList() {
        binding.rvLandmark.layoutManager = LinearLayoutManager(requireContext())

        val listUserAdapter = LIstLandmarkAdapter(list)
        binding.rvLandmark.adapter = listUserAdapter
        binding.rvLandmark.isNestedScrollingEnabled = false

        listUserAdapter.setOnItemClickCallback(object : LIstLandmarkAdapter.OnItemClickCallback {
            override fun onItemClicked() {
                val intent = Intent(requireContext(), DetailLandmarkActivity::class.java)
                startActivity(intent)
            }
        })
    }
}