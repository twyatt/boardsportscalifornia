package com.boardsportscalifornia.app.data.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "windgraph")
data class WindGraphEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,

    @ColumnInfo(name = "last_modified") val lastModified: OffsetDateTime,

    @ColumnInfo(name = "filename") val filename: String
)
