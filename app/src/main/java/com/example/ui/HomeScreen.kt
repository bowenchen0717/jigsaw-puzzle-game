package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PuzzleGameViewModel,
    onNavigateToPlay: (id: String, title: String, source: Any, size: Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customPuzzles by viewModel.customPuzzles.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

    var selectedGridSize by remember { mutableStateOf(3) } // Default 3x3
    var isAnimalCategory by remember { mutableStateOf(false) }
    val filteredPresets = remember(isAnimalCategory) {
        PuzzlePresets.list.filter {
            if (isAnimalCategory) it.category == PresetPuzzle.Category.ANIMALS
            else it.category == PresetPuzzle.Category.ABSTRACT
        }
    }
    val dailyPreset = filteredPresets.firstOrNull()
    val dailyRecord = dailyPreset?.let { preset ->
        historyList.find { it.puzzleId == preset.id && it.difficulty == selectedGridSize }
    }
    val dailyIsCompleted = dailyRecord?.isCompleted == true
    val dailyProgressFraction = if (dailyIsCompleted) 1.0f else 0.0f
    val dailyProgressPct = if (dailyIsCompleted) "100%" else "0%"
    val dailyButtonText = if (dailyIsCompleted) "再次挑戰" else "開始挑戰"
    var showCustomNameDialog by remember { mutableStateOf(false) }
    var pendingCustomUri by remember { mutableStateOf<Uri?>(null) }
    var customPuzzleName by remember { mutableStateOf("") }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingCustomUri = uri
            customPuzzleName = "我的自訂拼圖 #${(System.currentTimeMillis() % 1000).toInt()}"
            showCustomNameDialog = true
        }
    }

    // Helper: copy picked image to local storage
    fun saveUriToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val filename = "custom_puzzle_${System.currentTimeMillis()}.jpg"
            val outFile = File(context.filesDir, filename)
            val outputStream = FileOutputStream(outFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFD0BCFF), Color(0xFF381E72))))
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🧩",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "動感拼圖 PixelPuzzle",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "設定",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .frostedBackground(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 600.dp)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Hero Arcade Banner Card (Frosted Glass style Daily Challenge Highlight)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(162.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF381E72), Color(0xFF4F378B), Color(0xFFD0BCFF))
                             )
                        )
                        .border(
                            BorderStroke(1.dp, Color(0x3DFFFFFF)),
                            RoundedCornerShape(28.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color(0x33FFFFFF))
                                        .border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "DAILY CHALLENGE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "霓虹午夜都市\nMidnight Neon",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 22.sp
                                )
                            }
                            
                            val dailyPainter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(dailyPreset?.imageRes ?: R.drawable.img_app_icon_1782897535121)
                                    .crossfade(true)
                                    .build()
                            )
                            Image(
                                painter = dailyPainter,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .align(Alignment.CenterVertically),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                              ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color(0x33FFFFFF))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(dailyProgressFraction)
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(Color.White)
                                    )
                                }
                                Text(
                                    text = dailyProgressPct,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Button(
                                onClick = {
                                    val firstPreset = dailyPreset
                                    if (firstPreset != null) {
                                        val src = firstPreset.imageRes!!
                                        onNavigateToPlay(firstPreset.id, firstPreset.titleCn, src, selectedGridSize)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF381E72)
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(text = dailyButtonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Grid Size Selector Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CyberSurface)
                        .border(BorderStroke(1.5.dp, BorderColor), RoundedCornerShape(24.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ 選擇拼圖主題",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isAnimalCategory = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (!isAnimalCategory) NeonPink else Color(0x15FFFFFF))
                        ) { Text("抽象風格") }
                        Button(
                            onClick = { isAnimalCategory = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAnimalCategory) NeonPink else Color(0x15FFFFFF))
                        ) { Text("可愛動物") }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "🎮 選擇拼圖難度 (切換尺寸)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            3 to "新手 3x3",
                            4 to "極客 4x4",
                            5 to "大師 5x5",
                            -1 to "自訂"
                        ).forEach { (size, label) ->
                            val isSelected = if (size == -1) selectedGridSize < 3 || selectedGridSize > 5 else selectedGridSize == size
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0x0AFFFFFF))
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) NeonCyan else Color(0x15FFFFFF)
                                        ),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { if (size != -1) selectedGridSize = size else if (selectedGridSize < 3 || selectedGridSize > 5) {} else selectedGridSize = 6 }
                                    .testTag("diff_chip_$size"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    if (selectedGridSize < 3 || selectedGridSize > 5) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "選擇尺寸: ${selectedGridSize}x${selectedGridSize}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        androidx.compose.material3.Slider(
                            value = selectedGridSize.toFloat(),
                            onValueChange = { selectedGridSize = it.toInt() },
                            valueRange = 2f..10f,
                            steps = 8
                        )
                    }
                }
            }

            // Custom Upload Tile Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "✨ 我的相簿上傳",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "自訂圖片變拼圖",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                
                // Add Custom Tile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(BorderStroke(1.dp, NeonPink.copy(alpha = 0.4f)), RoundedCornerShape(24.dp))
                        .clickable {
                            pickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("upload_custom_button"),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(NeonPink.copy(alpha = 0.2f))
                                .border(BorderStroke(1.dp, NeonPink.copy(alpha = 0.4f)), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload",
                                tint = NeonPink,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "點擊上傳自己喜歡的圖片",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "自動裁切成拼圖進行挑戰",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Custom Puzzles Row (if any)
            if (customPuzzles.isNotEmpty()) {
                item {
                    Text(
                        text = "📸 我的專屬拼圖 (${customPuzzles.size})",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        customPuzzles.forEach { puzzle ->
                            val puzzleId = "custom_${puzzle.id}"
                            val record = historyList.find { it.puzzleId == puzzleId && it.difficulty == selectedGridSize }
                            val isCompleted = record?.isCompleted == true

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CyberSurface)
                                    .border(BorderStroke(1.dp, BorderColor), RoundedCornerShape(20.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = puzzle.filePath,
                                    contentDescription = puzzle.title,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = puzzle.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (isCompleted && record != null) {
                                        Text(
                                            text = "最佳: ${record.bestTime}秒 / ${record.bestMoves}步",
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        Text(
                                            text = "未挑戰 / ${selectedGridSize}x${selectedGridSize}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                
                                // Play Button
                                IconButton(
                                    onClick = {
                                        onNavigateToPlay(puzzleId, puzzle.title, puzzle.filePath, selectedGridSize)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NeonPink.copy(alpha = 0.2f))
                                        .border(BorderStroke(1.dp, NeonPink.copy(alpha = 0.4f)), RoundedCornerShape(12.dp))
                                        .testTag("play_custom_${puzzle.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = NeonPink
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(6.dp))
                                
                                // Delete button
                                IconButton(
                                    onClick = {
                                        viewModel.deleteCustomPuzzle(puzzle.id)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Preset Puzzles Section (10 items)
            item {
                Text(
                    text = "🏆 精選潮流預設關卡 (10)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Preset List in Grid-like display (We can render them inside a custom chunk or item lists)
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    val presets = filteredPresets
                    presets.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { preset ->
                                val record = historyList.find { it.puzzleId == preset.id && it.difficulty == selectedGridSize }
                                val isCompleted = record?.isCompleted == true

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(0.82f)
                                        .clip(RoundedCornerShape(22.dp))
                                        .border(
                                            BorderStroke(
                                                if (isCompleted) 2.dp else 1.dp,
                                                if (isCompleted) NeonCyan.copy(alpha = 0.9f) else BorderColor
                                            ),
                                            RoundedCornerShape(22.dp)
                                        )
                                        .clickable {
                                            val src = preset.imageRes!!
                                            onNavigateToPlay(preset.id, preset.titleCn, src, selectedGridSize)
                                        }
                                        .testTag("preset_card_${preset.id}"),
                                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Image or Procedural Placeholder
                                        if (preset.imageRes != null) {
                                            val cardPainter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(preset.imageRes)
                                                    .crossfade(true)
                                                    .build()
                                            )
                                            Image(
                                                painter = cardPainter,
                                                contentDescription = preset.titleCn,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(0.62f)
                                                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            // Fallback image representation
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(0.62f)
                                                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(NeonPurple.copy(alpha = 0.6f), CyberDark)
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "IMAGE ART",
                                                    color = NeonCyan,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp,
                                                    textAlign = TextAlign.Center,
                                                    letterSpacing = 0.5.sp
                                                )
                                            }
                                        }

                                        // Completed Badge
                                        if (isCompleted) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .align(Alignment.TopEnd)
                                                     .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                                                     .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(10.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Text(
                                                        text = "已通關",
                                                        color = NeonCyan,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Bottom details
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomStart)
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = preset.titleCn,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = preset.titleEn,
                                                color = TextSecondary,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (record != null && isCompleted) {
                                                Text(
                                                    text = "⏱️ ${record.bestTime}s | 🧩 ${record.bestMoves}步",
                                                    color = NeonCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            } else {
                                                Text(
                                                    text = "難度: ${selectedGridSize}x${selectedGridSize}",
                                                    color = NeonPink.copy(alpha = 0.8f),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // Custom Named Dialogue on Import
    if (showCustomNameDialog && pendingCustomUri != null) {
        AlertDialog(
            onDismissRequest = {
                showCustomNameDialog = false
                pendingCustomUri = null
            },
            containerColor = CyberSurface,
            title = {
                Text(text = "命名自訂拼圖", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = "為上傳的拼圖設定一個酷炫的名字：", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customPuzzleName,
                        onValueChange = { customPuzzleName = it },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uri = pendingCustomUri
                        if (uri != null) {
                            val path = saveUriToInternalStorage(uri)
                            if (path != null) {
                                viewModel.addCustomPuzzle(
                                    title = customPuzzleName.ifBlank { "我的自訂拼圖" },
                                    filePath = path
                                )
                            }
                        }
                        showCustomNameDialog = false
                        pendingCustomUri = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    modifier = Modifier.testTag("confirm_custom_name")
                ) {
                    Text(text = "確認", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCustomNameDialog = false
                    pendingCustomUri = null
                }) {
                    Text(text = "取消", color = TextSecondary)
                }
            }
        )
    }
}
