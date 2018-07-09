package com.boardsportscalifornia.app.features.windgraph

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.boardsportscalifornia.app.data.repository.BoardsportsRepository
import com.boardsportscalifornia.app.data.repository.models.WindGraph
import com.boardsportscalifornia.app.data.repository.models.WindGraphException
import com.github.kittinunf.result.Result
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class WindGraphViewModel(private val repository: BoardsportsRepository) : ViewModel() {

    private var job: Job? = null

    val latestWindGraph: LiveData<Result<WindGraph, WindGraphException>> by lazy {
        MutableLiveData<Result<WindGraph, WindGraphException>>().apply {
            job = launch {
                postValue(repository.getWindGraph())
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        job = null
    }
}
