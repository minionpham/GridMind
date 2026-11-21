package com.example.gridmind.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [GameHistoryEntity::class], version = 2, exportSchema = true)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        // Existing rows all belong to the original mode.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE game_history ADD COLUMN mode TEXT NOT NULL DEFAULT 'REMEMBER_THE_POSITION'"
                )
            }
        }

        fun create(context: Context): GameDatabase =
            Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "grid_mind.db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
