package com.boardsportscalifornia.app.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime

private const val SHARED_PREFERENCES_NAME = "com.boardsportscalifornia.app.preferences"

private const val WIND_GRAPH_WORKER_FAILURE_TYPE = "wind_graph_worker_failure_type"
private const val WIND_GRAPH_WORKER_FAILURE_MESSAGE = "wind_graph_worker_failure_message"
private const val WIND_GRAPH_WORKER_FAILURE_STACKTRACE = "wind_graph_worker_failure_stacktrace"
private const val WIND_GRAPH_WORKER_FAILURE_TIME = "wind_graph_worker_failure_time"

private const val WIND_GRAPH_DOWNLOAD_SCHEDULED_TIME = "wind_graph_download_scheduled_time"

class BoardsportsPreferences(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    var windGraphWorkerFailure: WindGraphWorkerFailure?
        set(value) {
            sharedPreferences.edit {
                if (value != null) {
                    with(value) {
                        putString(WIND_GRAPH_WORKER_FAILURE_TYPE, exceptionType)
                        putString(WIND_GRAPH_WORKER_FAILURE_MESSAGE, exceptionMessage)
                        putString(WIND_GRAPH_WORKER_FAILURE_STACKTRACE, exceptionStacktrace)
                        putInstant(WIND_GRAPH_WORKER_FAILURE_TIME, time)
                    }
                } else {
                    remove(WIND_GRAPH_WORKER_FAILURE_TYPE)
                    remove(WIND_GRAPH_WORKER_FAILURE_MESSAGE)
                    remove(WIND_GRAPH_WORKER_FAILURE_STACKTRACE)
                    remove(WIND_GRAPH_WORKER_FAILURE_TIME)
                }
            }
        }
        get() {
            val type = sharedPreferences.getString(WIND_GRAPH_WORKER_FAILURE_TYPE, null)
            val message = sharedPreferences.getString(WIND_GRAPH_WORKER_FAILURE_MESSAGE, null)
            val stacktrace = sharedPreferences.getString(WIND_GRAPH_WORKER_FAILURE_STACKTRACE, null)
            val time = sharedPreferences.getInstant(WIND_GRAPH_WORKER_FAILURE_TIME, null)
            return if (type != null && message != null && stacktrace != null && time != null) {
                WindGraphWorkerFailure(type, message, stacktrace, time)
            } else {
                null
            }
        }

    var windGraphDownloadScheduledTime: LocalTime?
        set(value) {
            sharedPreferences.edit {
                putLocalTime(WIND_GRAPH_DOWNLOAD_SCHEDULED_TIME, value)
            }
        }
        get() = sharedPreferences.getLocalTime(WIND_GRAPH_DOWNLOAD_SCHEDULED_TIME, null)
}

fun windGraphWorkerFailure(throwable: Throwable, time: Instant = Instant.now()) =
    WindGraphWorkerFailure(
        throwable.javaClass.simpleName,
        throwable.localizedMessage,
        Log.getStackTraceString(throwable),
        time
    )

data class WindGraphWorkerFailure(
    val exceptionType: String,
    val exceptionMessage: String,
    val exceptionStacktrace: String,
    val time: Instant
)

private fun SharedPreferences.Editor.putInstant(key: String, value: Instant?) =
    if (value != null) {
        putLong(key, value.toEpochMilli())
    } else {
        remove(key)
    }

private fun SharedPreferences.getInstant(key: String, defValue: Instant?): Instant? =
    getLong(key, Long.MIN_VALUE).let {
        if (it != Long.MIN_VALUE) {
            Instant.ofEpochMilli(it)
        } else {
            defValue
        }
    }

private fun SharedPreferences.Editor.putLocalTime(key: String, value: LocalTime?) =
    if (value != null) {
        putInt(key, value.toSecondOfDay())
    } else {
        remove(key)
    }

private fun SharedPreferences.getLocalTime(key: String, defValue: LocalTime?): LocalTime? =
    getInt(key, Integer.MIN_VALUE).let {
        if (it != Integer.MIN_VALUE) {
            LocalTime.ofSecondOfDay(it.toLong())
        } else {
            defValue
        }
    }
