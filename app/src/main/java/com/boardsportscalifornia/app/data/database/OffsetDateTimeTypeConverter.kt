package com.boardsportscalifornia.app.data.database

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Provides JSR-310 (Android backport) type converters for Room.
 *
 * @see [Room + Time](https://medium.com/@chrisbanes/room-time-2b4cf9672b98)
 */
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
