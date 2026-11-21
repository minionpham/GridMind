package com.example.gridmind

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.gridmind.domain.engine.GameEngine
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.LevelStats
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.presentation.statistics.StatisticsScreen
import com.example.gridmind.presentation.game.GameContent
import com.example.gridmind.presentation.home.HomeScreen
import com.example.gridmind.presentation.result.ResultScreen
import com.example.gridmind.ui.theme.GridMindTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GridMindUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val engine = GameEngine()

    @Test
    fun homeButtonsInvokeNavigationCallbacks() {
        val clicks = mutableListOf<String>()
        rule.setContent {
            GridMindTheme {
                HomeScreen(
                    progress = PlayerProgress(bestScore = 450, highestUnlockedLevel = 3),
                    onPlay = { clicks += "play" },
                    onLevels = { clicks += "levels" },
                    onStatistics = { clicks += "stats" },
                    onSettings = { clicks += "settings" }
                )
            }
        }
        rule.onNodeWithTag("home_level").assertTextEquals("3")
        rule.onNodeWithTag("home_best").assertTextEquals("450")
        rule.onNodeWithTag("play_button").performClick()
        rule.onNodeWithTag("levels_button").performClick()
        rule.onNodeWithTag("statistics_button").performClick()
        rule.onNodeWithTag("settings_button").performClick()
        assertEquals(listOf("play", "levels", "stats", "settings"), clicks)
    }

    @Test
    fun gridSelectionDeselectionAndConfirm() {
        var state by mutableStateOf(engine.finishShowing(engine.startRound(1)))
        rule.setContent {
            GridMindTheme {
                GameContent(
                    game = state,
                    onCellClick = { state = engine.toggleCell(state, it) },
                    onConfirm = { state = engine.submit(state) }
                )
            }
        }
        rule.onNodeWithTag("confirm_button").assertIsNotEnabled()
        rule.onNodeWithTag("cell_0").performClick()
        assertEquals(setOf(0), state.selectedPositions)
        rule.onNodeWithTag("confirm_button").assertIsEnabled()
        rule.onNodeWithTag("cell_0").performClick()
        assertTrue(state.selectedPositions.isEmpty())
        rule.onNodeWithTag("cell_1").performClick()
        rule.onNodeWithTag("confirm_button").performClick()
        rule.waitForIdle()
        assertEquals(GamePhase.CHECKING_RESULT, state.phase)
        rule.onNodeWithTag("confirm_button").assertDoesNotExist()
    }

    @Test
    fun resultScreenShowsDetails() {
        rule.setContent {
            GridMindTheme {
                ResultScreen(
                    level = 2, score = 350, correct = 4, incorrect = 1, missed = 1,
                    accuracy = 80f, passed = true,
                    onNextLevel = {}, onRetry = {}, onHome = {}
                )
            }
        }
        rule.onNodeWithTag("result_status").assertTextEquals("PASS")
        rule.onNodeWithTag("result_score").assertTextEquals("350")
        rule.onNodeWithTag("result_correct").assertTextEquals("4")
        rule.onNodeWithTag("result_incorrect").assertTextEquals("1")
        rule.onNodeWithTag("result_missed").assertTextEquals("1")
        rule.onNodeWithTag("result_accuracy").assertTextEquals("80%")
        rule.onNodeWithTag("next_level_button").assertExists()
    }

    @Test
    fun failedResultHidesNextLevel() {
        rule.setContent {
            GridMindTheme {
                ResultScreen(1, 0, 0, 0, 3, 0f, false, {}, {}, {})
            }
        }
        rule.onNodeWithTag("result_status").assertTextEquals("FAILED")
        rule.onNodeWithTag("next_level_button").assertDoesNotExist()
        rule.onNodeWithTag("retry_button").assertExists()
    }

    @Test
    fun statisticsShowsStreaksChartAndLevels() {
        val games = listOf(
            GameHistory(2, 1, 300, 3, 0, 0, 100.0, true, 2000L),
            GameHistory(1, 1, 100, 1, 0, 2, 33.0, false, 1000L)
        )
        rule.setContent {
            GridMindTheme {
                StatisticsScreen(
                    PlayerStatistics(
                        totalGamesPlayed = 2, currentWinStreak = 1, bestWinStreak = 1,
                        currentDayStreak = 3, bestDayStreak = 5,
                        levelStats = listOf(LevelStats(1, 2, 1, 66.5)),
                        recentGames = games, trend = games.reversed()
                    ),
                    onBack = {}
                )
            }
        }
        rule.onNodeWithTag("win_streak").assertTextEquals("1 (best 1)")
        rule.onNodeWithTag("day_streak").assertTextEquals("3 (best 5)")
        rule.onNodeWithTag("trend_chart").assertExists()
        rule.onNodeWithTag("level_row_1").assertExists()
    }
}

