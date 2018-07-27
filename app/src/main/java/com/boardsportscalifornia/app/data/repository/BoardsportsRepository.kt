package com.boardsportscalifornia.app.data.repository

import com.boardsportscalifornia.app.data.FileStorage
import com.boardsportscalifornia.app.data.api.BoardsportsApi
import com.boardsportscalifornia.app.data.database.BoardsportsDatabase
import com.boardsportscalifornia.app.data.database.WindGraphEntity
import com.boardsportscalifornia.app.data.repository.models.WindGraphData
import okhttp3.Response
import okhttp3.ResponseBody
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException
import retrofit2.Response as RetrofitResponse
import ru.gildor.coroutines.retrofit.Result as RetrofitResult

// e.g. "Sun, 08 Jul 2018 04:48:08 GMT"
private val HTTP_LAST_MODIFIED_HEADER_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME

class BoardsportsRepository(
    private val api: BoardsportsApi,
    database: BoardsportsDatabase,
    private val storage: FileStorage
) {

    private val windGraphDao = database.windGraphDao()

    fun getLatestWindGraphFromDatabase(): WindGraphData? = windGraphDao.latest()?.loadWindGraph()

    suspend fun getWindGraphFromApi(): WindGraphData =
        api.windGraph()
            .awaitResponse()
            .asWindGraphOrThrow()
            .also {
                it.saveToDatabase()
            }

    private fun WindGraphEntity.loadWindGraph() =
        WindGraphData(storage.read(filename), lastModified.toZonedDateTime())

    private fun WindGraphData.saveToDatabase() {
        val filename = "windreport-${lastModified.toEpochSecond()}.png"
        val entity = WindGraphEntity(
            lastModified = lastModified.toOffsetDateTime(),
            filename = filename
        )
        storage.write(filename, bytes)
        windGraphDao.put(entity)
    }
}

class MalformedResponse(message: String) : IOException(message)

private fun RetrofitResponse<ResponseBody>.asWindGraphOrThrow(): WindGraphData {
    if (isSuccessful) {
        val lastModified = raw().lastModified ?: ZonedDateTime.now(ZoneId.of("GMT"))
        val bytes = body()?.bytes() ?: throw MalformedResponse("Response body is null")
        return WindGraphData(bytes, lastModified)
    } else {
        throw HttpException(this)
    }
}

private val Response.lastModified: ZonedDateTime?
    get() = header("last-modified")?.let {
        ZonedDateTime.parse(it, HTTP_LAST_MODIFIED_HEADER_DATE_FORMATTER)
    }
