package com.capstonec22ps073.toursight.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.view.category.CategoryViewModel

class CulturalObjectViewModelProvider(
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(culturalObjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}