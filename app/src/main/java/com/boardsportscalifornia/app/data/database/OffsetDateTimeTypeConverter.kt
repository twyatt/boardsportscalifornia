package com.boardsportscalifornia.app.data.database

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

class OffsetDateTimeTypeConverter {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? =
        value?.let {
            formatter.parse(it, OffsetDateTime::from)
        }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?): String? = date?.format(formatter)
}
