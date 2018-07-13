package com.boardsportscalifornia.app.features.windgraph

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import com.boardsportscalifornia.app.App
import com.boardsportscalifornia.app.R
import com.boardsportscalifornia.app.data.repository.BoardsportsRepository
import com.boardsportscalifornia.app.data.repository.models.WindGraphData
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

// e.g. "Sun, 08 Jul 2018 04:48:08 GMT"
private val LAST_MODIFIED_DISPLAY_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME

sealed class WindGraphState {
    data class Loading(val windGraph: WindGraphData?) : WindGraphState()
    data class Success(val windGraph: WindGraphData) : WindGraphState()
    data class Failure(val windGraph: WindGraphData?, val throwable: Throwable) : WindGraphState()
}

data class WindGraphDisplay(
    val drawable: Drawable?,
    val alpha: Float,
    val text: CharSequence,
    @ColorInt val color: Int
)

class WindGraphViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoardsportsRepository = getApplication<App>().repository

    private val resources
        get() = getApplication<App>().resources

    private var job: Job? = null

    /**
     * Provides an internal [LiveData] to send [WindGraphState] to, which will ultimately be mapped
     * to view models emitted by [windGraph].
     */
    private val windGraphStateLiveData = MutableLiveData<WindGraphState>()

    /**
     * Provides a [LiveData] that emits [WindGraphDisplay] objects to be used for UI updates.
     *
     * Lazily instantiated and will fetch wind graph on creation. Subsequent updates can be
     * triggered via the [refreshWindGraph] method.
     */
    val windGraph: LiveData<WindGraphDisplay> by lazy(LazyThreadSafetyMode.NONE) {
        createWindGraphDisplayLiveData().also {
            refreshWindGraph()
        }
    }

    /** Performs refresh of wind graph and emits state to [windGraphStateLiveData]. */
    fun refreshWindGraph() {
        job = launch {
            val windGraphFromDatabase: WindGraphData? =
                repository.getLatestWindGraphFromDatabase()
            withContext(UI) {
                windGraphStateLiveData.value = WindGraphState.Loading(windGraphFromDatabase)
            }

            val state = try {
                WindGraphState.Success(repository.getWindGraphFromApi())
            } catch (e: Exception) {
                Timber.e(e) { "Failed to retrieve wind graph from API" }
                WindGraphState.Failure(windGraphFromDatabase, e)
            }

            withContext(UI) {
                windGraphStateLiveData.value = state
            }
        }
    }

    /**
     * Creates a [LiveData] that maps [WindGraphState] information (loading, success or failure) to
     * view models used for UI updates.
     */
    private fun createWindGraphDisplayLiveData(): LiveData<WindGraphDisplay> =
        Transformations.map(windGraphStateLiveData) { state ->
            when (state) {
                is WindGraphState.Loading -> WindGraphDisplay(
                    drawable = state.windGraph?.bytes?.asBitmapDrawable(),
                    alpha = 0.25f,
                    text = resources.getString(R.string.loading),
                    color = Color.BLACK
                )

                is WindGraphState.Success -> WindGraphDisplay(
                    drawable = state.windGraph.bytes.asBitmapDrawable(),
                    alpha = 1f,
                    text = state.windGraph.lastModified
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .format(LAST_MODIFIED_DISPLAY_DATE_FORMATTER),
                    color = Color.BLACK
                )

                is WindGraphState.Failure -> WindGraphDisplay(
                    drawable = state.windGraph?.bytes?.asBitmapDrawable(),
                    alpha = 0.25f,
                    text = state.throwable.toString(),
                    color = Color.RED
                )
            }
        }

    override fun onCleared() {
        job?.cancel()
        job = null
    }

    private fun ByteArray.asBitmapDrawable(): BitmapDrawable =
        BitmapDrawable(resources, BitmapFactory.decodeByteArray(this, 0, size))
}

