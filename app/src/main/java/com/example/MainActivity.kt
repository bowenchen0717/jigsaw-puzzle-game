package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.HomeScreen
import com.example.ui.PlayScreen
import com.example.ui.SettingsScreen
import com.example.ui.AudioPlayer
import com.example.ui.PuzzleGameViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.theme.MyApplicationTheme

sealed class Screen {
    object Home : Screen()
    object Settings : Screen()
    data class Play(val id: String, val title: String, val source: Any, val size: Int) : Screen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: PuzzleGameViewModel by viewModels {
        ViewModelFactory((application as PuzzleApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                val context = LocalContext.current
                val audioPlayer = remember { AudioPlayer(context) }

                // System back press handler
                BackHandler(enabled = currentScreen !is Screen.Home) {
                    currentScreen = Screen.Home
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToPlay = { id, title, source, size ->
                                    viewModel.startMatch(id, title, source, size)
                                    currentScreen = Screen.Play(id, title, source, size)
                                },
                                onNavigateToSettings = {
                                    currentScreen = Screen.Settings
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.Settings -> {
                            SettingsScreen(
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.Play -> {
                            PlayScreen(
                                viewModel = viewModel,
                                audioPlayer = audioPlayer,
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
