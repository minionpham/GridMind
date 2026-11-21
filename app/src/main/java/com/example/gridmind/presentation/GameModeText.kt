package com.example.gridmind.presentation

import com.example.gridmind.domain.model.GameMode

val GameMode.title: String
    get() = when (this) {
        GameMode.REMEMBER_THE_POSITION -> "Remember the Position"
        GameMode.COUNT_CUBES -> "Count the Cubes"
    }

val GameMode.description: String
    get() = when (this) {
        GameMode.REMEMBER_THE_POSITION -> "Memorize the lit cells"
        GameMode.COUNT_CUBES -> "How many cubes are stacked?"
    }
