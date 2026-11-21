package com.example.gridmind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.presentation.game.CubeGameScreen
import com.example.gridmind.presentation.game.CubeGameViewModel
import com.example.gridmind.presentation.game.GameScreen
import com.example.gridmind.presentation.game.GameViewModel
import com.example.gridmind.presentation.home.HomeScreen
import com.example.gridmind.presentation.levelselect.LevelSelectScreen
import com.example.gridmind.presentation.levelselect.LevelSelectViewModel
import com.example.gridmind.presentation.levelselect.levelDetail
import com.example.gridmind.presentation.mode.ModeScreen
import com.example.gridmind.presentation.mode.ModeViewModel
import com.example.gridmind.presentation.result.CubeResultScreen
import com.example.gridmind.presentation.result.ResultScreen
import com.example.gridmind.presentation.settings.SettingsScreen
import com.example.gridmind.presentation.settings.SettingsViewModel
import com.example.gridmind.presentation.statistics.StatisticsScreen
import com.example.gridmind.presentation.statistics.StatisticsViewModel
import com.example.gridmind.presentation.title

private fun NavBackStackEntry.mode(): GameMode =
    GameMode.valueOf(arguments?.getString("mode") ?: GameMode.REMEMBER_THE_POSITION.name)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    fun startGame(mode: GameMode, level: Int, popResult: Boolean) {
        val route = when (mode) {
            GameMode.REMEMBER_THE_POSITION -> Screen.Game.createRoute(level)
            GameMode.COUNT_CUBES -> Screen.CubeGame.createRoute(level)
        }
        navController.navigate(route) {
            if (popResult) popUpTo(Screen.Home.route)
            launchSingleTop = true
        }
    }

    val backToHome = { navController.popBackStack(Screen.Home.route, inclusive = false); Unit }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onModeSelected = { navController.navigate(Screen.Mode.createRoute(it)) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            Screen.Mode.route,
            arguments = listOf(navArgument(Screen.Mode.ARG_MODE) { type = NavType.StringType })
        ) { entry ->
            val mode = entry.mode()
            val vm: ModeViewModel = viewModel(factory = ModeViewModel.Factory)
            val state by vm.uiState.collectAsStateWithLifecycle()
            ModeScreen(
                mode = mode,
                state = state,
                onPlay = { startGame(mode, state.highestUnlockedLevel, popResult = false) },
                onLevels = { navController.navigate(Screen.LevelSelect.createRoute(mode)) },
                onStatistics = { navController.navigate(Screen.Statistics.createRoute(mode)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.LevelSelect.route,
            arguments = listOf(navArgument(Screen.LevelSelect.ARG_MODE) { type = NavType.StringType })
        ) { entry ->
            val mode = entry.mode()
            val vm: LevelSelectViewModel = viewModel(factory = LevelSelectViewModel.Factory)
            val state by vm.uiState.collectAsStateWithLifecycle()
            LevelSelectScreen(
                state = state,
                onLevelSelected = { startGame(mode, it, popResult = false) },
                onBack = { navController.popBackStack() },
                detailFor = { levelDetail(mode, it) }
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
                onExit = { backToHome() }
            )
        }
        composable(
            Screen.CubeGame.route,
            arguments = listOf(navArgument(Screen.CubeGame.ARG_LEVEL) { type = NavType.IntType })
        ) {
            val vm: CubeGameViewModel = viewModel(factory = CubeGameViewModel.Factory)
            CubeGameScreen(
                viewModel = vm,
                onRoundFinished = { r ->
                    navController.navigate(
                        Screen.CubeResult.createRoute(r.level, r.score, r.chosen, r.correctAnswer, r.passed)
                    ) { popUpTo(Screen.Home.route) }
                },
                onExit = { backToHome() }
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
            ResultScreen(
                level = level,
                score = a.getInt(Screen.Result.ARG_SCORE),
                correct = a.getInt(Screen.Result.ARG_CORRECT),
                incorrect = a.getInt(Screen.Result.ARG_INCORRECT),
                missed = a.getInt(Screen.Result.ARG_MISSED),
                accuracy = a.getFloat(Screen.Result.ARG_ACCURACY),
                passed = a.getBoolean(Screen.Result.ARG_PASSED),
                onNextLevel = { startGame(GameMode.REMEMBER_THE_POSITION, level + 1, popResult = true) },
                onRetry = { startGame(GameMode.REMEMBER_THE_POSITION, level, popResult = true) },
                onHome = { backToHome() }
            )
        }
        composable(
            Screen.CubeResult.route,
            arguments = listOf(
                navArgument(Screen.CubeResult.ARG_LEVEL) { type = NavType.IntType },
                navArgument(Screen.CubeResult.ARG_SCORE) { type = NavType.IntType },
                navArgument(Screen.CubeResult.ARG_CHOSEN) { type = NavType.IntType },
                navArgument(Screen.CubeResult.ARG_ANSWER) { type = NavType.IntType },
                navArgument(Screen.CubeResult.ARG_PASSED) { type = NavType.BoolType }
            )
        ) { entry ->
            val a = entry.arguments!!
            val level = a.getInt(Screen.CubeResult.ARG_LEVEL)
            CubeResultScreen(
                level = level,
                score = a.getInt(Screen.CubeResult.ARG_SCORE),
                chosen = a.getInt(Screen.CubeResult.ARG_CHOSEN).takeIf { it >= 0 },
                correctAnswer = a.getInt(Screen.CubeResult.ARG_ANSWER),
                passed = a.getBoolean(Screen.CubeResult.ARG_PASSED),
                onNextLevel = { startGame(GameMode.COUNT_CUBES, level + 1, popResult = true) },
                onRetry = { startGame(GameMode.COUNT_CUBES, level, popResult = true) },
                onHome = { backToHome() }
            )
        }
        composable(
            Screen.Statistics.route,
            arguments = listOf(navArgument(Screen.Statistics.ARG_MODE) { type = NavType.StringType })
        ) { entry ->
            val mode = entry.mode()
            val vm: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
            val stats by vm.statistics.collectAsStateWithLifecycle()
            StatisticsScreen(stats, onBack = { navController.popBackStack() }, modeLabel = mode.title)
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
