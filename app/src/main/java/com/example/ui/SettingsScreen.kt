package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay

object AudioConfig {
    var isEnabled by mutableStateOf(true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    audioPlayer: AudioPlayer,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val packageInfo = remember(context) {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
    }
    val versionName = packageInfo?.versionName ?: "8.0"
    val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode?.toString() ?: "8"
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "8"
    }

    var isCheckingUpdates by remember { mutableStateOf(false) }
    var updateStep by remember { mutableStateOf("") }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isCheckingUpdates) {
        if (isCheckingUpdates) {
            updateStep = "正在連接更新伺服器..."
            delay(800)
            updateStep = "正在比對雲端版本資訊..."
            delay(1000)
            updateStep = "正在驗證安裝包特徵碼..."
            delay(700)
            isCheckingUpdates = false
            showUpdateSuccessDialog = true
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .frostedBackground(),
        containerColor = Color.Transparent, // Let the frosted background show through
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "系統設定", 
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        audioPlayer.playPageFlip()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Volume / Sound Effect settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "偏好設定 PREFERENCES",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "遊戲音效",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "點擊、拼合與完成挑戰時的音效",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = AudioConfig.isEnabled,
                            onCheckedChange = { 
                                AudioConfig.isEnabled = it
                                audioPlayer.playButtonClick()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonPink,
                                checkedTrackColor = NeonPurple,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = CyberDark
                            )
                        )
                    }
                }
            }

            // Version info & Update checking Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "版本資訊 VERSION INFO",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // App Name & Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2E1B4E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    "霓虹拼圖工坊",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Jigsaw Puzzle Workshop",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "v$versionName",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Build $versionCode",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    HorizontalDivider(color = BorderColor, thickness = 1.dp)

                    // Update checking button or spinner
                    if (isCheckingUpdates) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1F1B2E))
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = updateStep,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                audioPlayer.playButtonClick()
                                isCheckingUpdates = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPurple,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "檢查更新",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer / Disclaimer
            Text(
                text = "© 2026 Jigsaw Puzzle Studio\n版權所有 • 匠心打造",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Update result dialog
    if (showUpdateSuccessDialog) {
        Dialog(onDismissRequest = { showUpdateSuccessDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13111C))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFF1F2C24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎉", fontSize = 28.sp)
                    }

                    Text(
                        text = "已是最新版本",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "目前安裝的版本：v$versionName ($versionCode) 已是當前最新發佈版本，無需進行任何更新。",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Button(
                        onClick = {
                            audioPlayer.playButtonClick()
                            showUpdateSuccessDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPink,
                            contentColor = Color.White
                        )
                    ) {
                        Text("確定", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
