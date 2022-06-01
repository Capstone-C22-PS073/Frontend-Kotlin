package com.capstonec22ps073.toursight.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.view.category.CategoryActivity
import com.capstonec22ps073.toursight.view.category.CategoryViewModel
import com.capstonec22ps073.toursight.view.search.SearchViewModel

class MainViewModelFactory(
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(culturalObjectRepository, authRepository) as T
        } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(authRepository, culturalObjectRepository) as T
        } else if (modelClass.isAssignableFrom(CategoryActivity::class.java)) {
            return CategoryViewModel(authRepository, culturalObjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}