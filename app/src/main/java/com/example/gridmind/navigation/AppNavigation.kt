package com.example.gridmind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gridmind.presentation.game.GameScreen
import com.example.gridmind.presentation.game.GameViewModel
import com.example.gridmind.presentation.home.HomeScreen
import com.example.gridmind.presentation.home.HomeViewModel
import com.example.gridmind.presentation.result.ResultScreen
import com.example.gridmind.presentation.settings.SettingsScreen
import com.example.gridmind.presentation.settings.SettingsViewModel
import com.example.gridmind.presentation.statistics.StatisticsScreen
import com.example.gridmind.presentation.statistics.StatisticsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    fun startGame(level: Int, popResult: Boolean) {
        navController.navigate(Screen.Game.createRoute(level)) {
            if (popResult) popUpTo(Screen.Home.route)
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            val progress by vm.progress.collectAsStateWithLifecycle()
            HomeScreen(
                progress = progress,
                onPlay = { startGame(progress.highestUnlockedLevel, popResult = false) },
                onStatistics = { navController.navigate(Screen.Statistics.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            Screen.Game.route,
            arguments = listOf(navArgument(Screen.Game.ARG_LEVEL) { type = NavType.IntType })
        ) {
            val vm: GameViewModel = viewModel(factory = GameViewModel.Factory)
            GameScreen(
                viewModel = vm,
                onRoundFinished = { r ->
                    navController.navigate(
                        Screen.Result.createRoute(
                            r.level, r.score, r.correctCount, r.incorrectCount,
                            r.missedCount, r.accuracy.toFloat(), r.passed
                        )
                    ) { popUpTo(Screen.Home.route) }
                },
                onExit = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }
        composable(
            Screen.Result.route,
            arguments = listOf(
                navArgument(Screen.Result.ARG_LEVEL) { type = NavType.IntType },
                navArgument(Screen.Result.ARG_SCORE) { type = NavType.IntType },
                navArgument(Screen.Result.ARG_CORRECT) { type = NavType.IntType },
                navArgument(Screen.Result.ARG_INCORRECT) { type = NavType.IntType },
                navArgument(Screen.Result.ARG_MISSED) { type = NavType.IntType },
                navArgument(Screen.Result.ARG_ACCURACY) { type = NavType.FloatType },
                navArgument(Screen.Result.ARG_PASSED) { type = NavType.BoolType }
            )
        ) { entry ->
            val a = entry.arguments!!
            val level = a.getInt(Screen.Result.ARG_LEVEL)
            val passed = a.getBoolean(Screen.Result.ARG_PASSED)
            ResultScreen(
                level = level,
                score = a.getInt(Screen.Result.ARG_SCORE),
                correct = a.getInt(Screen.Result.ARG_CORRECT),
                incorrect = a.getInt(Screen.Result.ARG_INCORRECT),
                missed = a.getInt(Screen.Result.ARG_MISSED),
                accuracy = a.getFloat(Screen.Result.ARG_ACCURACY),
                passed = passed,
                onNextLevel = { startGame(level + 1, popResult = true) },
                onRetry = { startGame(level, popResult = true) },
                onHome = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }
        composable(Screen.Statistics.route) {
            val vm: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
            val stats by vm.statistics.collectAsStateWithLifecycle()
            StatisticsScreen(stats, onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
            val settings by vm.settings.collectAsStateWithLifecycle()
            SettingsScreen(
                settings = settings,
                onSound = vm::setSound,
                onVibration = vm::setVibration,
                onTheme = vm::setTheme,
                onReset = vm::resetProgress,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
