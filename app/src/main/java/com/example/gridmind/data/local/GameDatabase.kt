package com.example.gridmind.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameHistoryEntity::class], version = 1, exportSchema = true)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        fun create(context: Context): GameDatabase =
            Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "grid_mind.db")
                .build()
    }
}
