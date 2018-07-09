package com.boardsportscalifornia.app.data.repository.models

import org.threeten.bp.OffsetDateTime

data class WindGraph(
    val bytes: ByteArray,
    val lastModified: OffsetDateTime
)

class WindGraphException(
    val cachedWindGraph: WindGraph?,
    cause: Throwable
) : IllegalStateException(cause)
