package com.boardsportscalifornia.app

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.boardsportscalifornia.app.data.BoardsportsPreferences
import com.boardsportscalifornia.app.data.FileStorage
import com.boardsportscalifornia.app.data.api.BoardsportsApiProvider
import com.boardsportscalifornia.app.data.database.BoardsportsDatabase
import com.boardsportscalifornia.app.data.repository.BoardsportsRepository
import com.boardsportscalifornia.app.work.BoardsportsWorkManager
import com.github.ajalt.timberkt.Timber
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalTime
import timber.log.Timber.DebugTree
import java.util.Random

class App : Application() {

    private val random = Random()

    lateinit var repository: BoardsportsRepository
    lateinit var workManager: BoardsportsWorkManager
    lateinit var preferences: BoardsportsPreferences

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        repository = BoardsportsRepository(
            api = BoardsportsApiProvider().api,
            database = BoardsportsDatabase.openPersistentDatabase(this),
            storage = FileStorage(this)
        )
        workManager = BoardsportsWorkManager(WorkManager.getInstance())
        preferences = BoardsportsPreferences(this)

        checkDownloadWindGraphSchedule()
    }

    private fun checkDownloadWindGraphSchedule() {
        val time = preferences.windGraphDownloadScheduledTime
        if (time == null) {
            // random time between 21:00 and 22:59
            val randomTime = LocalTime.of(21, 0)
                .plusHours((0..1).random().toLong())
                .plusMinutes((0..59).random().toLong())

            workManager.scheduleWindGraphDownload(randomTime)
            preferences.windGraphDownloadScheduledTime = randomTime
        } else {
            Timber.d { "Wind graph download already scheduled at $time" }
        }
    }

    private fun ClosedRange<Int>.random() =
        random.nextInt((endInclusive + 1) - start) + start
}

val Context.app
    get() = applicationContext as App
