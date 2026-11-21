package com.example.gridmind.navigation

import com.example.gridmind.domain.model.GameMode

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")

    data object Mode : Screen("mode/{mode}") {
        const val ARG_MODE = "mode"
        fun createRoute(mode: GameMode) = "mode/${mode.name}"
    }

    data object LevelSelect : Screen("levels/{mode}") {
        const val ARG_MODE = "mode"
        fun createRoute(mode: GameMode) = "levels/${mode.name}"
    }

    data object Statistics : Screen("statistics/{mode}") {
        const val ARG_MODE = "mode"
        fun createRoute(mode: GameMode) = "statistics/${mode.name}"
    }

    data object Game : Screen("game/{level}") {
        const val ARG_LEVEL = "level"
        fun createRoute(level: Int) = "game/$level"
    }

    data object CubeGame : Screen("cubes/{level}") {
        const val ARG_LEVEL = "level"
        fun createRoute(level: Int) = "cubes/$level"
    }

    data object Result : Screen("result/{level}/{score}/{correct}/{incorrect}/{missed}/{accuracy}/{passed}") {
        const val ARG_LEVEL = "level"
        const val ARG_SCORE = "score"
        const val ARG_CORRECT = "correct"
        const val ARG_INCORRECT = "incorrect"
        const val ARG_MISSED = "missed"
        const val ARG_ACCURACY = "accuracy"
        const val ARG_PASSED = "passed"

        fun createRoute(
            level: Int, score: Int, correct: Int, incorrect: Int, missed: Int, accuracy: Float, passed: Boolean
        ) = "result/$level/$score/$correct/$incorrect/$missed/$accuracy/$passed"
    }

    data object CubeResult : Screen("cube_result/{level}/{score}/{chosen}/{answer}/{passed}") {
        const val ARG_LEVEL = "level"
        const val ARG_SCORE = "score"
        const val ARG_CHOSEN = "chosen"
        const val ARG_ANSWER = "answer"
        const val ARG_PASSED = "passed"

        /** A missing answer is encoded as -1. */
        fun createRoute(level: Int, score: Int, chosen: Int?, answer: Int, passed: Boolean) =
            "cube_result/$level/$score/${chosen ?: -1}/$answer/$passed"
    }
}
