package com.boardsportscalifornia.app.data

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardsportscalifornia.app.data.repository.BoardsportsRepository
import com.boardsportscalifornia.app.features.windgraph.WindGraphViewModel

class ViewModelFactory(private val repository: BoardsportsRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(WindGraphViewModel::class.java)) {
            WindGraphViewModel(repository)
        } else {
            throw IllegalArgumentException("Unsupported ViewModel class: $modelClass")
        } as T
}
