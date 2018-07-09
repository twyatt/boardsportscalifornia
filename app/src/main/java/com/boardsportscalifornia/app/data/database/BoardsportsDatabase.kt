package com.boardsportscalifornia.app.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(
    entities = [WindGraphEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OffsetDateTimeTypeConverter::class)
abstract class BoardsportsDatabase : RoomDatabase() {

    abstract fun windGraphDao(): WindGraphDao

    companion object {
        private const val DATABASE_NAME = "boardsports.db"

        fun openPersistentDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BoardsportsDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
