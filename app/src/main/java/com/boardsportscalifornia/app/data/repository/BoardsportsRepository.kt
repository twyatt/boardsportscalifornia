package com.boardsportscalifornia.app.data.repository

import com.boardsportscalifornia.app.data.FileStorage
import com.boardsportscalifornia.app.data.api.BoardsportsApi
import com.boardsportscalifornia.app.data.database.BoardsportsDatabase
import com.boardsportscalifornia.app.data.database.WindGraphEntity
import com.boardsportscalifornia.app.data.repository.models.WindGraph
import com.boardsportscalifornia.app.data.repository.models.WindGraphException
import com.github.ajalt.timberkt.Timber
import com.github.kittinunf.result.Result
import okhttp3.Response
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.gildor.coroutines.retrofit.awaitResult
import ru.gildor.coroutines.retrofit.Result as RetrofitResult

class BoardsportsRepository(
    private val api: BoardsportsApi,
    database: BoardsportsDatabase,
    private val storage: FileStorage
) {

    private val windGraphDao = database.windGraphDao()

    suspend fun getWindGraph(): Result<WindGraph, WindGraphException> =
        api.windGraph().awaitResult().let { result ->
            when (result) {
                is RetrofitResult.Ok -> {
                    Timber.d { "Result.Ok" }
                    val lastModified = result.response.lastModified ?: ZonedDateTime.now()
                    val filename = "windreport-${lastModified.toEpochSecond()}.png"
                    val entity = WindGraphEntity(
                        lastModified = lastModified.toOffsetDateTime(),
                        filename = filename
                    )
                    val bytes = result.value.bytes()

                    storage.write(filename, bytes)
                    windGraphDao.put(entity)

                    Result.Success(WindGraph(bytes, lastModified.toOffsetDateTime()))
                }
                is RetrofitResult.Error ->
                    Result.Failure(
                        WindGraphException(windGraphDao.latest()?.loadWindGraph(), result.exception)
                    )
                is RetrofitResult.Exception ->
                    Result.Failure(
                        WindGraphException(windGraphDao.latest()?.loadWindGraph(), result.exception)
                    )
            }
        }

    private fun WindGraphEntity.loadWindGraph() = WindGraph(storage.read(filename), lastModified)
}

private val Response.lastModified: ZonedDateTime? // e.g. "Sun, 08 Jul 2018 04:48:08 GMT"
    get() = header("last-modified")?.let {
        ZonedDateTime.parse(it, DateTimeFormatter.RFC_1123_DATE_TIME)
    }
