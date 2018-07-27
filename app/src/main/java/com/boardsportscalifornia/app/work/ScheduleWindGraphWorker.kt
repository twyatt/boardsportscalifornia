package com.boardsportscalifornia.app.work

import androidx.work.Worker
import com.boardsportscalifornia.app.App
import com.github.ajalt.timberkt.Timber

class ScheduleWindGraphWorker : Worker() {

    private val app = applicationContext as App

    override fun doWork(): Result {
        return try {
            Timber.d { "Scheduling wind graph download worker" }
            app.workManager.enqueueDailyWindGraphDownload()
            Result.SUCCESS
        } catch (e: Exception) {
            Timber.e(e) { "Scheduling of wind graph download worker failed" }
            Result.FAILURE
        }
    }
}
