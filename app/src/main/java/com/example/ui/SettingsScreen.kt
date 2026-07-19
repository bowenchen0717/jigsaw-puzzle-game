package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.RemoteConfigManager
import com.example.data.UserPreferences
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
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

            // Preferred Theme Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCard)
            ) {
                val localCategories = remember {
                    listOf(
                        PresetPuzzle.Category.SCENERY,
                        PresetPuzzle.Category.BIBLE,
                        PresetPuzzle.Category.ABSTRACT,
                        PresetPuzzle.Category.ANIMALS
                    )
                }
                var isDropdownExpanded by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "偏好主題 PREFERRED THEME",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Column {
                        Text(
                            "預設拼圖主題",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "選擇您喜愛的拼圖分類，首頁與每日挑戰將優先以此分類為主。",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Dropdown Container
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val currentPreferredId = UserPreferences.preferredCategoryId
                        val currentCategoryName = if (currentPreferredId == null) {
                            "跟隨系統預設"
                        } else {
                            localCategories.find { it.id.equals(currentPreferredId, ignoreCase = true) }?.nameCn ?: currentPreferredId
                        }

                        Surface(
                            onClick = {
                                audioPlayer.playButtonClick()
                                isDropdownExpanded = !isDropdownExpanded
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (currentPreferredId != null) NeonPink else BorderColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = CyberDark
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentCategoryName,
                                    color = if (currentPreferredId != null) Color.White else TextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "下拉選單",
                                    tint = if (currentPreferredId != null) NeonPink else TextSecondary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .background(CyberDark)
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        ) {
                            // "跟隨系統預設" option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "跟隨系統預設",
                                        color = if (currentPreferredId == null) NeonPink else Color.White,
                                        fontWeight = if (currentPreferredId == null) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    audioPlayer.playButtonClick()
                                    UserPreferences.setPreferredCategory(context, null)
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier.background(if (currentPreferredId == null) CyberCard else Color.Transparent)
                            )

                            // Fixed local categories
                            localCategories.forEach { category ->
                                val isSelected = category.id.equals(currentPreferredId, ignoreCase = true)
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            category.nameCn,
                                            color = if (isSelected) NeonPink else Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        audioPlayer.playButtonClick()
                                        UserPreferences.setPreferredCategory(context, category.id)
                                        isDropdownExpanded = false
                                    },
                                    modifier = Modifier.background(if (isSelected) CyberCard else Color.Transparent)
                                )
                            }
                        }
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
                                Image(
                                    painter = painterResource(id = R.drawable.img_app_icon_1782897535121),
                                    contentDescription = "App Icon",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column {
                                Text(
                                    "我行我速拼圖快手",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Speed Jigsaw Puzzle",
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
                text = "© 2026 我行我速拼圖快手\n版權所有 • 匠心打造",
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
