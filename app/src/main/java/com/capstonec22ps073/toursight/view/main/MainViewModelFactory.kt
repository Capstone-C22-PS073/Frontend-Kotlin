package com.capstonec22ps073.toursight.view.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.view.category.CategoryViewModel
import com.capstonec22ps073.toursight.view.history.HistoryImageViewModel
import com.capstonec22ps073.toursight.view.preview.PreviewViewModel
import com.capstonec22ps073.toursight.view.search.SearchViewModel

class MainViewModelFactory(
    private val app: Application,
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(app, culturalObjectRepository, authRepository) as T
        } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(app, authRepository, culturalObjectRepository) as T
        } else if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(app, authRepository, culturalObjectRepository) as T
        } else if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
            return PreviewViewModel(app, authRepository, culturalObjectRepository) as T
        } else if (modelClass.isAssignableFrom(HistoryImageViewModel::class.java)) {
            return HistoryImageViewModel(app, authRepository, culturalObjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}