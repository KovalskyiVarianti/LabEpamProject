package com.example.labepamproject.presentation

import android.media.Image
import com.example.labepamproject.presentation.adapter.Item

sealed class MainViewState {
    data class LoadingState(val loadingImageId: Int) : MainViewState()
    data class ErrorState(val errorImageId: Int) : MainViewState()
    data class ResultState(val items: List<Item>) : MainViewState()
}