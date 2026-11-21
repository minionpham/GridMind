package com.example.gridmind.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gridmind.AppContainer
import com.example.gridmind.GridMindApp

fun CreationExtras.appContainer(): AppContainer =
    (this[APPLICATION_KEY] as GridMindApp).container
