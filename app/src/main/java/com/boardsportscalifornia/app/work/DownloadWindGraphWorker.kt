package com.boardsportscalifornia.app.work

import androidx.work.Worker
import com.boardsportscalifornia.app.App
import com.boardsportscalifornia.app.data.windGraphWorkerFailure
import com.boardsportscalifornia.app.data.repository.MalformedResponse
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.experimental.runBlocking
import retrofit2.HttpException

class DownloadWindGraphWorker : Worker() {

    private val app = applicationContext as App

    override fun doWork(): Result = runBlocking {
        try {
            Timber.d { "Download wind graph worker started" }
            val windGraph = app.repository.getWindGraphFromApi()

            Timber.d {
                val size = windGraph.bytes.size
                val lastModified = windGraph.lastModified
                "Downloaded wind graph of $size bytes, last modified at $lastModified"
            }

            Result.SUCCESS
        } catch (e: HttpException) {
            Timber.w(e) { "Failed to download wind graph due to HTTP error" }
            Result.RETRY
        } catch (e: MalformedResponse) {
            Timber.w(e) { "Failed to download wind graph due to malformed response" }
            Result.RETRY
        } catch (e: Exception) {
            Timber.e(e) { "Download wind graph worker failed" }
            app.preferences.windGraphWorkerFailure = windGraphWorkerFailure(e)
            Result.FAILURE
        }
    }
}
