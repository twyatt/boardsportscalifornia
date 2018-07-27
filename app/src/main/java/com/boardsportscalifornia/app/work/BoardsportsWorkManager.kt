package com.boardsportscalifornia.app.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.ajalt.timberkt.Timber
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

private val BOARDSPORTS_CALIFORNIA_ZONE_ID = ZoneId.of("US/Pacific")

private const val SCHEDULE_WIND_GRAPH_DOWNLOAD_NAME =
    "com.boardsportscalifornia.work.ScheduleDownloadingOfWindGraph"
private const val DOWNLOAD_WIND_GRAPH_NAME = "com.boardsportscalifornia.work.DownloadWindGraph"

class BoardsportsWorkManager(private val workManager: WorkManager) {

    /**
     * @param time (US/Pacific) to schedule wind graph download
     */
    fun scheduleWindGraphDownload(time: LocalTime) {
        val now = ZonedDateTime.now()
        val schedule = ZonedDateTime.of(now.toLocalDate(), time, BOARDSPORTS_CALIFORNIA_ZONE_ID)
        val delay = now.until(schedule, ChronoUnit.SECONDS)
            .let {
                if (it >= 0) {
                    it
                } else {
                    TimeUnit.HOURS.toSeconds(24) - it.absoluteValue
                }
            }

        Timber.d { "Scheduling wind graph download at $time ($schedule)" }

        val request = OneTimeWorkRequestBuilder<ScheduleWindGraphWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .build()

        Timber.d { "Scheduling download wind graph worker to start in $delay seconds" }
        workManager.beginUniqueWork(SCHEDULE_WIND_GRAPH_DOWNLOAD_NAME, REPLACE, request)
    }

    fun enqueueDailyWindGraphDownload() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(CONNECTED)
            .build()

        val interval = 24L
        val request = PeriodicWorkRequestBuilder<DownloadWindGraphWorker>(interval, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        Timber.d { "Enqueueing download wind graph worker to run every $interval hours" }
        workManager.enqueueUniquePeriodicWork(DOWNLOAD_WIND_GRAPH_NAME, KEEP, request)

    }
}
