package com.boardsportscalifornia.app

import android.app.Application
import android.content.Context
import com.boardsportscalifornia.app.data.FileStorage
import com.boardsportscalifornia.app.data.api.BoardsportsApiProvider
import com.boardsportscalifornia.app.data.database.BoardsportsDatabase
import com.boardsportscalifornia.app.data.repository.BoardsportsRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber.DebugTree
import timber.log.Timber

class App : Application() {

    lateinit var repository: BoardsportsRepository

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        val api = BoardsportsApiProvider().api
        val database = BoardsportsDatabase.openPersistentDatabase(this)
        val storage = FileStorage(this)
        repository = BoardsportsRepository(api, database, storage)
    }
}

val Context.app
    get() = applicationContext as App
