package com.example.gridmind.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object LevelSelect : Screen("levels")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")

    data object Game : Screen("game/{level}") {
        const val ARG_LEVEL = "level"
        fun createRoute(level: Int) = "game/$level"
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
}

