package com.example.gridmind

import android.content.Context
import com.example.gridmind.data.local.GameDatabase
import com.example.gridmind.data.local.PreferencesDataSource
import com.example.gridmind.data.repository.GameRepositoryImpl
import com.example.gridmind.domain.engine.GameEngine
import com.example.gridmind.domain.repository.GameRepository

/** Manual dependency container; sufficient for the MVP. */
class AppContainer(context: Context) {
    private val database = GameDatabase.create(context)
    val repository: GameRepository =
        GameRepositoryImpl(database.gameHistoryDao(), PreferencesDataSource(context))

    fun createGameEngine() = GameEngine()
}
