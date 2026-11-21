package com.example.gridmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.navigation.AppNavigation
import com.example.gridmind.ui.theme.GridMindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as GridMindApp).container.repository
        setContent {
            val settings by repository.settings.collectAsStateWithLifecycle(GameSettings())
            GridMindTheme(themeMode = settings.themeMode) {
                AppNavigation()
            }
        }
    }
}
