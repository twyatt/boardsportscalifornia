package com.boardsportscalifornia.app.data.repository.models

import org.threeten.bp.ZonedDateTime

data class WindGraphData(
    val bytes: ByteArray,
    val lastModified: ZonedDateTime
)
