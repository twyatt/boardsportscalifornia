package com.boardsportscalifornia.app.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface WindGraphDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun put(entity: WindGraphEntity): Long

    @Query("SELECT * FROM windgraph ORDER BY datetime(last_modified) DESC LIMIT 1")
    fun latest(): WindGraphEntity?
}
