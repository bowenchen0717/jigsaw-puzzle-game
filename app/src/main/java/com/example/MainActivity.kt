package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
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
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

sealed class Screen {
    object Home : Screen()
    object Settings : Screen()
    data class Play(val id: String, val title: String, val source: Any, val size: Int) : Screen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: PuzzleGameViewModel by viewModels {
        ViewModelFactory((application as PuzzleApplication).repository)
    }

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                val context = LocalContext.current
                val audioPlayer = remember(context) {
                    val audioContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        context.applicationContext.createAttributionContext("audio")
                    } else {
                        context.applicationContext
                    }
                    AudioPlayer(audioContext)
                }

                // System back press handler
                BackHandler(enabled = currentScreen !is Screen.Home) {
                    currentScreen = Screen.Home
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                audioPlayer = audioPlayer,
                                onNavigateToPlay = { id, title, source, size ->
                                    audioPlayer.playPageFlip()
                                    viewModel.startMatch(id, title, source, size)
                                    currentScreen = Screen.Play(id, title, source, size)
                                },
                                onNavigateToSettings = {
                                    audioPlayer.playPageFlip()
                                    currentScreen = Screen.Settings
                                },
                                modifier = Modifier
                            )
                        }
                        is Screen.Settings -> {
                            SettingsScreen(
                                audioPlayer = audioPlayer,
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                },
                                modifier = Modifier
                            )
                        }
                        is Screen.Play -> {
                            PlayScreen(
                                viewModel = viewModel,
                                audioPlayer = audioPlayer,
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::appUpdateManager.isInitialized) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                android.util.Log.e("MainActivity", "In-app update failed or was cancelled by user. Result code: $resultCode")
            }
        }
    }

    companion object {
        private const val UPDATE_REQUEST_CODE = 1234
    }
}
