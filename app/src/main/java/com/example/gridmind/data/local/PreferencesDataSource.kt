package com.example.gridmind.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "grid_mind_prefs")

class PreferencesDataSource(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.applicationContext.dataStore)

    fun progress(mode: GameMode): Flow<PlayerProgress> {
        val keys = keysFor(mode)
        return dataStore.data.map {
            PlayerProgress(
                bestScore = it[keys.bestScore] ?: 0,
                highestUnlockedLevel = (it[keys.highestLevel] ?: 1).coerceAtLeast(1)
            )
        }
    }

    val settings: Flow<GameSettings> = dataStore.data.map {
        GameSettings(
            soundEnabled = it[SOUND] ?: true,
            vibrationEnabled = it[VIBRATION] ?: true,
            themeMode = it[THEME]?.let { name -> ThemeMode.entries.firstOrNull { m -> m.name == name } }
                ?: ThemeMode.DARK
        )
    }

    /** Atomically raises the stored values; never lowers them. */
    suspend fun updateProgress(mode: GameMode, score: Int, unlockedLevel: Int) {
        val keys = keysFor(mode)
        dataStore.edit {
            it[keys.bestScore] = maxOf(it[keys.bestScore] ?: 0, score)
            it[keys.highestLevel] = maxOf(it[keys.highestLevel] ?: 1, unlockedLevel)
        }
    }

    suspend fun setSound(enabled: Boolean) = dataStore.edit { it[SOUND] = enabled }.let { }
    suspend fun setVibration(enabled: Boolean) = dataStore.edit { it[VIBRATION] = enabled }.let { }
    suspend fun setTheme(mode: ThemeMode) = dataStore.edit { it[THEME] = mode.name }.let { }

    suspend fun resetProgress() {
        dataStore.edit { prefs ->
            GameMode.entries.map(::keysFor).forEach {
                prefs.remove(it.bestScore)
                prefs.remove(it.highestLevel)
            }
        }
    }

    private class ProgressKeys(val bestScore: Preferences.Key<Int>, val highestLevel: Preferences.Key<Int>)

    private fun keysFor(mode: GameMode) = when (mode) {
        GameMode.REMEMBER_THE_POSITION -> ProgressKeys(BEST_SCORE, HIGHEST_LEVEL)
        GameMode.COUNT_CUBES -> ProgressKeys(CUBES_BEST_SCORE, CUBES_HIGHEST_LEVEL)
    }

    private companion object {
        val BEST_SCORE = intPreferencesKey("best_score")
        val HIGHEST_LEVEL = intPreferencesKey("highest_unlocked_level")
        val CUBES_BEST_SCORE = intPreferencesKey("cubes_best_score")
        val CUBES_HIGHEST_LEVEL = intPreferencesKey("cubes_highest_unlocked_level")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val THEME = stringPreferencesKey("theme_mode")
    }
}

