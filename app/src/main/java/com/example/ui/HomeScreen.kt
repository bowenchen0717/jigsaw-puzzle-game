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
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PuzzleGameViewModel,
    audioPlayer: AudioPlayer,
    onNavigateToPlay: (id: String, title: String, source: Any, size: Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customPuzzles by viewModel.customPuzzles.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

    var selectedGridSize by remember { mutableStateOf(3) } // Default 3x3
    var selectedCategory by remember { mutableStateOf(PresetPuzzle.Category.ABSTRACT) }
    val filteredPresets = remember(selectedCategory) {
        PuzzlePresets.list.filter { it.category == selectedCategory }
    }
    val calendar = remember { java.util.Calendar.getInstance() }
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val dateString = String.format("%04d-%02d-%02d", year, month, day)
    val dailyChallengeId = "daily_$dateString"

    val dailyPreset = remember(year, month, day) {
        val seed = year * 10000 + month * 100 + day
        val index = (seed % PuzzlePresets.list.size).let { if (it < 0) -it else it }
        PuzzlePresets.list.getOrNull(index) ?: PuzzlePresets.list.first()
    }
    val dailyRecord = remember(dailyChallengeId, selectedGridSize, historyList) {
        historyList.find { it.puzzleId == dailyChallengeId && it.difficulty == selectedGridSize }
    }
    val dailyIsCompleted = dailyRecord?.isCompleted == true
    val dailyProgressFraction = if (dailyIsCompleted) 1.0f else 0.0f
    val dailyProgressPct = if (dailyIsCompleted) "100%" else "0%"
    val dailyButtonText = if (dailyIsCompleted) "再次挑戰" else "開始挑戰"
    var showCustomNameDialog by remember { mutableStateOf(false) }
    var pendingCustomUri by remember { mutableStateOf<Uri?>(null) }
    var customPuzzleName by remember { mutableStateOf("") }
    var selectedPresetForDetail by remember { mutableStateOf<PresetPuzzle?>(null) }
    var isDailyChallengeSelected by remember { mutableStateOf(false) }

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
                        .wrapContentHeight()
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                        text = "DAILY CHALLENGE • $dateString",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = (dailyPreset?.titleCn ?: "") + "\n" + (dailyPreset?.titleEn ?: ""),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 22.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (dailyIsCompleted && dailyRecord != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🏆 今日已通關！最佳: ${dailyRecord.bestTime}秒 | ${dailyRecord.bestMoves}步",
                                        color = Color(0xFF00E676),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                                        audioPlayer.playPageFlip()
                                        selectedPresetForDetail = firstPreset
                                        isDailyChallengeSelected = true
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetPuzzle.Category.values().forEach { category ->
                            val label = when (category) {
                                PresetPuzzle.Category.ABSTRACT -> "抽象風格"
                                PresetPuzzle.Category.ANIMALS -> "可愛動物"
                                PresetPuzzle.Category.BIBLE -> "聖經故事"
                            }
                            val isSelected = selectedCategory == category
                            Button(
                                onClick = { 
                                    audioPlayer.playButtonClick()
                                    selectedCategory = category 
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) NeonPink else Color(0x15FFFFFF)
                                )
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
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
                                    .clickable { 
                                        audioPlayer.playButtonClick()
                                        if (size != -1) selectedGridSize = size else if (selectedGridSize < 3 || selectedGridSize > 5) {} else selectedGridSize = 6 
                                    }
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
                            onValueChange = { 
                                val prev = selectedGridSize
                                selectedGridSize = it.toInt()
                                if (prev != selectedGridSize) {
                                    audioPlayer.playTick()
                                }
                            },
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
                            audioPlayer.playButtonClick()
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
                                        audioPlayer.playPageFlip()
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
                                        audioPlayer.playButtonClick()
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
                                            audioPlayer.playPageFlip()
                                            selectedPresetForDetail = preset
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
            modifier = Modifier.border(BorderStroke(1.5.dp, NeonPink.copy(alpha = 0.8f)), RoundedCornerShape(24.dp)),
            containerColor = Color(0xFF161421),
            shape = RoundedCornerShape(24.dp),
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
                        audioPlayer.playAchievement()
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
                    audioPlayer.playButtonClick()
                    showCustomNameDialog = false
                    pendingCustomUri = null
                }) {
                    Text(text = "取消", color = TextSecondary)
                }
            }
        )
    }

    // Preset Detail Dialog (Popup scripture description & pre-game preview)
    if (selectedPresetForDetail != null) {
        val preset = selectedPresetForDetail!!
        Dialog(
            onDismissRequest = {
                selectedPresetForDetail = null
                isDailyChallengeSelected = false
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 24.dp)
                    .border(BorderStroke(1.5.dp, NeonPink.copy(alpha = 0.8f)), RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161421)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header title & Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDailyChallengeSelected) "📅 今日每日挑戰 • $dateString" else "📖 關卡詳情與經文",
                            color = if (isDailyChallengeSelected) NeonPink else NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                selectedPresetForDetail = null
                                isDailyChallengeSelected = false
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "關閉",
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image preview
                    if (preset.imageRes != null) {
                        Image(
                            painter = painterResource(id = preset.imageRes),
                            contentDescription = preset.titleCn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(BorderStroke(1.5.dp, Color(0x33FFFFFF)), RoundedCornerShape(18.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Titles
                    Text(
                        text = preset.titleCn,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = preset.titleEn,
                        color = NeonPink.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Scrollbox
                    val descText = (if (isDailyChallengeSelected) "【今日特別挑戰項目】\n\n" else "") + (preset.descriptionCn ?: "這是一個精美的預設關卡拼圖。點擊下方按鈕即可開始挑戰！")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0C1B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = descText,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Difficulty selector directly in detail popup
                    Text(
                        text = "⚙️ 選擇拼圖難度",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            3 to "新手 3x3",
                            4 to "極客 4x4",
                            5 to "大師 5x5"
                        ).forEach { (size, label) ->
                            val isSelected = selectedGridSize == size
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonPink.copy(alpha = 0.25f) else Color(0x0AFFFFFF))
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) NeonPink else Color(0x15FFFFFF)
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        audioPlayer.playButtonClick()
                                        selectedGridSize = size
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Play Button
                    Button(
                        onClick = {
                            audioPlayer.playPageFlip()
                            val src = preset.imageRes!!
                            val playId = if (isDailyChallengeSelected) dailyChallengeId else preset.id
                            val playTitle = if (isDailyChallengeSelected) "【每日挑戰】" + preset.titleCn else preset.titleCn
                            onNavigateToPlay(playId, playTitle, src, selectedGridSize)
                            selectedPresetForDetail = null
                            isDailyChallengeSelected = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                    ) {
                        Text(
                            text = "開始挑戰",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
