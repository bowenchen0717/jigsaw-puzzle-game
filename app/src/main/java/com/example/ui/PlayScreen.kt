package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.*
import com.example.ui.AudioPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    viewModel: PuzzleGameViewModel,
    audioPlayer: AudioPlayer,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val matchState by viewModel.matchState.collectAsStateWithLifecycle()
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showStencil by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    val trayScrollState = rememberScrollState()
    val trayScrollX = -trayScrollState.value / density

    // Load bitmap asynchronously when match state details change or match is reset
    LaunchedEffect(matchState.imageSource, matchState.gridSize, matchState.matchToken) {
        val src = matchState.imageSource
        if (src != null) {
            val bitmap = PuzzlePresets.loadBitmap(context, src, 600, 600)
            imageBitmap = bitmap
            if (bitmap != null) {
                viewModel.resizeMatchBoard(bitmap.width, bitmap.height)
            }
        }
    }

    // Trigger victory sound once completed
    LaunchedEffect(matchState.isVictory) {
        if (matchState.isVictory) {
            audioPlayer.playPuzzleComplete()
        }
    }

    // Timer formatting
    val minutes = matchState.timerSeconds / 60
    val seconds = matchState.timerSeconds % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = matchState.title.ifEmpty { "解謎空間" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                audioPlayer.playPageFlip()
                                onNavigateBack()
                            },
                            modifier = Modifier.testTag("play_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // Toggle Fullscreen
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                isFullscreen = true
                            },
                            modifier = Modifier
                                .background(Color.Transparent, CircleShape)
                                .testTag("fullscreen_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White
                            )
                        }

                        // Toggle showing/hiding the background stencil guide on the board
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                showStencil = !showStencil
                            },
                            modifier = Modifier
                                .background(
                                    if (showStencil) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                                .testTag("stencil_toggle_button")
                        ) {
                            Text(
                                text = if (showStencil) "👁️" else "🕶️",
                                fontSize = 20.sp
                            )
                        }

                        // Hold or click eye to preview full image
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                viewModel.togglePreview(!matchState.showPreview)
                            },
                            modifier = Modifier
                                .background(
                                    if (matchState.showPreview) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                                .testTag("preview_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Preview Guide",
                                tint = if (matchState.showPreview) NeonCyan else Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                viewModel.resetMatch()
                            },
                            modifier = Modifier.testTag("reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .frostedBackground()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 600.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isFullscreen) {
                    // Info Dashboard Row (Timer, Grid difficulty, Moves)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(CyberSurface)
                            .border(BorderStroke(1.dp, BorderColor), RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "⏱️ 時間", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = timerString,
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.testTag("game_timer")
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonPurple.copy(alpha = 0.15f))
                                .border(BorderStroke(1.dp, NeonPurple), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${matchState.gridCols} x ${matchState.gridRows} 矩陣",
                                color = NeonPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "🧩 步數", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${matchState.moves} 次",
                                color = NeonPink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.testTag("game_moves")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(72.dp))
                }

                val trayPieces = matchState.pieces.filter { !it.isLocked && !it.isDragging && it.currentY >= 340f }.sortedBy { it.shuffleOrder }
                val pWidth = matchState.boardWidth / matchState.gridCols
                val pHeight = matchState.boardHeight / matchState.gridRows
                val gap = 16f
                val totalSlotWidth = pWidth + gap
                val startPadding = 16f
                val offsetX = (320f - matchState.boardWidth) / 2f
                val offsetY = (320f - matchState.boardHeight) / 2f

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val containerWidth = maxWidth
                    val containerHeight = maxHeight

                    val scaleFactor = if (isFullscreen) {
                        // Calculate scale based on the board itself rather than the 320 container,
                        // allowing the board to fill the full width or height of the screen dynamically.
                        // Since tray ends at y=480, we use 480f as the active content height.
                        val scaleX = containerWidth.value / matchState.boardWidth
                        val scaleY = containerHeight.value / 480f
                        minOf(scaleX, scaleY).coerceIn(0.5f, 3.5f)
                    } else {
                        val scaleX = containerWidth.value / 320f
                        val scaleY = (containerHeight.value - 16f) / 500f
                        if (scaleX < 1.0f || scaleY < 1.0f) {
                            minOf(scaleX, scaleY).coerceIn(0.5f, 1.0f)
                        } else {
                            1.0f
                        }
                    }

                    val scaledWidth = 320.dp * scaleFactor
                    val scaledHeight = 500.dp * scaleFactor

                    Box(
                        modifier = Modifier.requiredSize(scaledWidth, scaledHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredSize(320.dp, 500.dp)
                                .graphicsLayer {
                                    scaleX = scaleFactor
                                    scaleY = scaleFactor
                                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                                }
                                .testTag("puzzle_game_box")
                        ) {
                        // 1. The Background Target Board Box (centered based on aspect ratio)
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX.dp, y = offsetY.dp)
                                .size(matchState.boardWidth.dp, matchState.boardHeight.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(CyberSurface)
                                .border(BorderStroke(1.5.dp, BorderColor), RoundedCornerShape(24.dp))
                        ) {
                            // Dim guide stencil (shown only if showStencil is true)
                            if (showStencil && imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap!!,
                                    contentDescription = "Guide Stencil",
                                    modifier = Modifier.fillMaxSize(),
                                    alpha = 0.15f,
                                    contentScale = ContentScale.FillBounds
                                )
                            } else if (imageBitmap == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = NeonCyan)
                                }
                            }
                        }

                        // Decorative Tray label separating the board and the Tray pool
                        Box(
                            modifier = Modifier
                                .offset(y = 325.dp)
                                .fillMaxWidth()
                                .height(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👇 拖曳下方拼圖到上方對應位置 👇",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Horizontal Scrollable Tray Box background & gesture listener
                        Box(
                            modifier = Modifier
                                .offset(y = 350.dp)
                                .size(320.dp, 130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .border(BorderStroke(1.dp, BorderColor), RoundedCornerShape(16.dp))
                                .horizontalScroll(trayScrollState)
                        ) {
                            val totalScrollableWidthDp = (trayPieces.size * totalSlotWidth + startPadding * 2).coerceAtLeast(320f)
                            Box(
                                modifier = Modifier
                                    .size(totalScrollableWidthDp.dp, 130.dp)
                            )
                        }

                        // Arrow buttons inside the tray to scroll it left/right easily
                        if (trayScrollState.value > 0) {
                            IconButton(
                                onClick = {
                                    audioPlayer.playButtonClick()
                                    scope.launch {
                                        val target = (trayScrollState.value - 120 * density).roundToInt().coerceAtLeast(0)
                                        trayScrollState.animateScrollTo(target)
                                    }
                                },
                                modifier = Modifier
                                    .offset(y = 350.dp)
                                    .align(Alignment.TopStart)
                                    .padding(start = 4.dp, top = 53.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Scroll Left",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (trayScrollState.value < trayScrollState.maxValue) {
                            IconButton(
                                onClick = {
                                    audioPlayer.playButtonClick()
                                    scope.launch {
                                        val target = (trayScrollState.value + 120 * density).roundToInt().coerceAtMost(trayScrollState.maxValue)
                                        trayScrollState.animateScrollTo(target)
                                    }
                                },
                                modifier = Modifier
                                    .offset(y = 350.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(end = 4.dp, top = 53.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Scroll Right",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // 2. Render all Puzzle Pieces (locked on board, unlocked in scrollable tray)
                        matchState.pieces.forEach { piece ->
                            val defaultX: Float
                            val defaultY: Float
                            val isInTray = !piece.isLocked && !piece.isDragging && piece.currentY >= 340f

                            if (piece.isLocked) {
                                defaultX = piece.targetLeft
                                defaultY = piece.targetTop
                            } else if (isInTray) {
                                val idx = trayPieces.indexOfFirst { it.id == piece.id }
                                if (idx != -1) {
                                    defaultX = startPadding + idx * totalSlotWidth + trayScrollX
                                    defaultY = 350f + (130f - pHeight) / 2f
                                } else {
                                    defaultX = piece.currentX
                                    defaultY = piece.currentY
                                }
                            } else {
                                defaultX = piece.currentX
                                defaultY = piece.currentY
                            }

                            key(piece.id) {
                                PuzzlePieceItem(
                                    piece = piece,
                                    audioPlayer = audioPlayer,
                                    pieceWidth = pWidth,
                                    pieceHeight = pHeight,
                                    gridRows = matchState.gridRows,
                                    gridCols = matchState.gridCols,
                                    imageBitmap = imageBitmap,
                                    defaultX = defaultX,
                                    defaultY = defaultY,
                                    density = density,
                                    onDragStart = { startX, startY ->
                                        audioPlayer.playPickup()
                                        viewModel.startDragging(piece.id, startX, startY)
                                    },
                                    onDragEnd = { finalX, finalY ->
                                        val finalYAdjusted = if (finalY >= 340f) 350f else finalY
                                        
                                        val pWidth = matchState.boardWidth / matchState.gridCols
                                        val pHeight = matchState.boardHeight / matchState.gridRows
                                        val thresholdX = (pWidth * 0.45f).coerceIn(32f, 48f)
                                        val thresholdY = (pHeight * 0.45f).coerceIn(32f, 48f)

                                        val distanceX = Math.abs(finalX - piece.targetLeft)
                                        val distanceY = Math.abs(finalYAdjusted - piece.targetTop)
                                        val isCloseEnough = distanceX < thresholdX && distanceY < thresholdY

                                        if (isCloseEnough) {
                                            val hasAdjacentLocked = matchState.pieces.any { other ->
                                                other.isLocked && other.id != piece.id && (
                                                    (other.row == piece.row && Math.abs(other.col - piece.col) == 1) ||
                                                    (other.col == piece.col && Math.abs(other.row - piece.row) == 1)
                                                )
                                            }
                                            if (hasAdjacentLocked) {
                                                audioPlayer.playConnect()
                                            } else {
                                                audioPlayer.playSnap()
                                            }
                                        } else {
                                            val isInsideBoard = finalX >= 0 && finalX <= matchState.boardWidth && finalYAdjusted >= 0 && finalYAdjusted <= matchState.boardHeight
                                            if (isInsideBoard) {
                                                audioPlayer.playWrongPlace()
                                            } else {
                                                audioPlayer.playDrop()
                                            }
                                        }

                                        viewModel.stopDragging(piece.id, finalX, finalYAdjusted)
                                    },
                                    onDrag = { deltaX, deltaY ->
                                        viewModel.dragPiece(piece.id, deltaX, deltaY)
                                        val p = matchState.pieces.find { it.id == piece.id }
                                        if (p != null) {
                                            val pWidth = matchState.boardWidth / matchState.gridCols
                                            val pHeight = matchState.boardHeight / matchState.gridRows
                                            val tX = (pWidth * 0.45f).coerceIn(32f, 48f)
                                            val tY = (pHeight * 0.45f).coerceIn(32f, 48f)
                                            
                                            val nextX = p.currentX + deltaX
                                            val nextY = p.currentY + deltaY
                                            val dX = Math.abs(nextX - p.targetLeft)
                                            val dY = Math.abs(nextY - p.targetTop)
                                            
                                            val nowClose = dX < tX && dY < tY
                                            val wasClose = Math.abs(p.currentX - p.targetLeft) < tX && Math.abs(p.currentY - p.targetTop) < tY
                                            
                                            if (nowClose && !wasClose) {
                                                audioPlayer.playTick()
                                            }
                                        }
                                    },
                                    isInTray = isInTray,
                                    onTrayScroll = { deltaX ->
                                        trayScrollState.dispatchRawDelta(-deltaX)
                                    }
                                )
                            }
                        }
                    }
                }
                }
            }

            // Floating controls overlay for Fullscreen mode
            if (isFullscreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            audioPlayer.playButtonClick()
                            isFullscreen = false
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = NeonCyan
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(BorderStroke(1.dp, BorderColor), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ $timerString",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🧩 ${matchState.moves} 步",
                            color = NeonPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                showStencil = !showStencil
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .border(BorderStroke(1.dp, if (showStencil) NeonCyan else BorderColor.copy(alpha = 0.3f)), CircleShape)
                                .size(40.dp)
                        ) {
                            Text(
                                text = if (showStencil) "👁️" else "🕶️",
                                fontSize = 16.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                viewModel.togglePreview(!matchState.showPreview)
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .border(BorderStroke(1.dp, if (matchState.showPreview) NeonCyan else BorderColor.copy(alpha = 0.3f)), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Preview Guide",
                                tint = if (matchState.showPreview) NeonCyan else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                audioPlayer.playButtonClick()
                                viewModel.resetMatch()
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .border(BorderStroke(1.dp, BorderColor.copy(alpha = 0.3f)), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Floating Preview Overlay Guide (Toggled by the top bar eye icon)
            AnimatedVisibility(
                visible = matchState.showPreview,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable { viewModel.togglePreview(false) }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(matchState.boardWidth / matchState.boardHeight)
                            .clip(RoundedCornerShape(20.dp))
                            .border(BorderStroke(2.dp, NeonCyan), RoundedCornerShape(20.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CyberSurface)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            imageBitmap?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Full Preview Guide",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } ?: Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "圖片加載中...", color = Color.White)
                            }
                            
                            // Floating Dismiss button info
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "點擊任何地方關閉預覽",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // 3. Gorgeous Neon-Arcade Victory Pop-up Dialog
            AnimatedVisibility(
                visible = matchState.isVictory,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF161421))
                            .border(
                                BorderStroke(2.dp, Brush.linearGradient(listOf(NeonPink, NeonCyan))),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Shiny Trophy Emblem
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CyberYellow.copy(alpha = 0.2f))
                                .border(BorderStroke(2.dp, CyberYellow), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 36.sp)
                        }

                        Text(
                            text = "通關成功！",
                            color = NeonCyan,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "完美！你展現了極高的智慧與耐力，成功解開了此張拼圖！",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Score details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "⏱️ 總時間", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    text = timerString,
                                    color = NeonCyan,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(36.dp)
                                    .background(BorderColor)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🧩 總步數", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    text = "${matchState.moves} 次",
                                    color = NeonPink,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Controls
                        Button(
                            onClick = {
                                audioPlayer.playButtonClick()
                                viewModel.resetMatch()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("victory_play_again_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "再玩一次", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Button(
                            onClick = {
                                audioPlayer.playPageFlip()
                                onNavigateBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("victory_home_button")
                        ) {
                            Text(text = "返回大廳", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzlePieceItem(
    piece: PuzzlePiece,
    audioPlayer: AudioPlayer,
    pieceWidth: Float,
    pieceHeight: Float,
    gridRows: Int,
    gridCols: Int,
    imageBitmap: ImageBitmap?,
    defaultX: Float,
    defaultY: Float,
    density: Float,
    onDragStart: (Float, Float) -> Unit,
    onDragEnd: (Float, Float) -> Unit,
    onDrag: (Float, Float) -> Unit,
    isInTray: Boolean,
    onTrayScroll: (Float) -> Unit
) {
    val currentIsLocked by rememberUpdatedState(piece.isLocked)
    val currentIsInTray by rememberUpdatedState(isInTray)
    val currentDefaultX by rememberUpdatedState(defaultX)
    val currentDefaultY by rememberUpdatedState(defaultY)
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnTrayScroll by rememberUpdatedState(onTrayScroll)

    var localOffsetX by remember { mutableStateOf(0f) }
    var localOffsetY by remember { mutableStateOf(0f) }
    var isDraggingLocal by remember { mutableStateOf(false) }
    var hasPickedUp by remember { mutableStateOf(false) }

    LaunchedEffect(piece.isLocked, defaultX, defaultY) {
        if (!isDraggingLocal) {
            localOffsetX = 0f
            localOffsetY = 0f
            hasPickedUp = false
        }
    }

    val currentX = if (isDraggingLocal && !hasPickedUp) {
        defaultX + localOffsetX
    } else {
        defaultX
    }
    val currentY = if (isDraggingLocal && !hasPickedUp) {
        defaultY + localOffsetY
    } else {
        defaultY
    }

    val touchWidth = if (piece.isLocked) pieceWidth else maxOf(pieceWidth, 56f)
    val touchHeight = if (piece.isLocked) pieceHeight else maxOf(pieceHeight, 56f)
    val extraWidth = touchWidth - pieceWidth
    val extraHeight = touchHeight - pieceHeight

    val touchX = currentX - extraWidth / 2f
    val touchY = currentY - extraHeight / 2f

    val baseModifier = Modifier
        .offset { IntOffset((touchX * density).roundToInt(), (touchY * density).roundToInt()) }
        .size(touchWidth.dp, touchHeight.dp)

    val pointerModifier = if (piece.isLocked) {
        baseModifier
    } else {
        baseModifier.pointerInput(piece.id) {
            detectDragGestures(
                onDragStart = {
                    if (currentIsLocked) return@detectDragGestures
                    isDraggingLocal = true
                    localOffsetX = 0f
                    localOffsetY = 0f
                    hasPickedUp = !currentIsInTray
                    if (hasPickedUp) {
                        currentOnDragStart(currentDefaultX, currentDefaultY)
                    }
                },
                onDragEnd = {
                    if (currentIsLocked) return@detectDragGestures
                    val finalX = if (hasPickedUp) currentDefaultX else currentDefaultX + localOffsetX
                    val finalY = if (hasPickedUp) currentDefaultY else currentDefaultY + localOffsetY
                    isDraggingLocal = false
                    hasPickedUp = false
                    currentOnDragEnd(finalX, finalY)
                },
                onDragCancel = {
                    if (currentIsLocked) return@detectDragGestures
                    val finalX = if (hasPickedUp) currentDefaultX else currentDefaultX + localOffsetX
                    val finalY = if (hasPickedUp) currentDefaultY else currentDefaultY + localOffsetY
                    isDraggingLocal = false
                    hasPickedUp = false
                    currentOnDragEnd(finalX, finalY)
                },
                onDrag = { change, dragAmount ->
                    if (currentIsLocked) return@detectDragGestures
                    change.consume()
                    val deltaX = dragAmount.x / density
                    val deltaY = dragAmount.y / density

                    localOffsetX += deltaX
                    localOffsetY += deltaY

                    if (currentIsInTray && !hasPickedUp) {
                        if (Math.abs(localOffsetX) > Math.abs(localOffsetY) && Math.abs(localOffsetX) > 8f) {
                            currentOnTrayScroll(dragAmount.x)
                            localOffsetX = 0f
                            localOffsetY = 0f
                        } else if (localOffsetY < -12f || Math.abs(localOffsetY) > 15f) {
                            hasPickedUp = true
                            currentOnDragStart(currentDefaultX + localOffsetX, currentDefaultY + localOffsetY)
                            localOffsetX = 0f
                            localOffsetY = 0f
                        }
                    } else {
                        currentOnDrag(deltaX, deltaY)
                    }
                }
            )
        }
    }

    Box(
        modifier = pointerModifier.testTag("piece_${piece.id}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(pieceWidth.dp, pieceHeight.dp)) {
            val pWidth = size.width
            val pHeight = size.height

            val row = piece.row
            val col = piece.col

            val topEdge = if (row == 0) 0 else -getHorizontalEdge(row - 1, col)
            val bottomEdge = if (row == gridRows - 1) 0 else getHorizontalEdge(row, col)
            val leftEdge = if (col == 0) 0 else -getVerticalEdge(row, col - 1)
            val rightEdge = if (col == gridCols - 1) 0 else getVerticalEdge(row, col)

            val boardLeft = -col * pWidth
            val boardTop = -row * pHeight
            val boardRight = boardLeft + gridCols * pWidth
            val boardBottom = boardTop + gridRows * pHeight

            val boardPath = Path().apply {
                val r = 0.075f * minOf(gridCols * pWidth, gridRows * pHeight)
                addRoundRect(
                    RoundRect(
                        rect = Rect(boardLeft, boardTop, boardRight, boardBottom),
                        topLeft = CornerRadius(r, r),
                        topRight = CornerRadius(r, r),
                        bottomLeft = CornerRadius(r, r),
                        bottomRight = CornerRadius(r, r)
                    )
                )
            }

            val rawPiecePath = Path().apply {
                moveTo(0f, 0f)
                addJigsawEdge(0f, 0f, pWidth, 0f, topEdge)
                addJigsawEdge(pWidth, 0f, pWidth, pHeight, rightEdge)
                addJigsawEdge(pWidth, pHeight, 0f, pHeight, bottomEdge)
                addJigsawEdge(0f, pHeight, 0f, 0f, leftEdge)
                close()
            }

            val piecePath = Path.combine(
                operation = PathOperation.Intersect,
                path1 = rawPiecePath,
                path2 = boardPath
            )

            clipPath(piecePath) {
                val bitmap = imageBitmap
                if (bitmap != null) {
                    val boardWidthPx = pWidth * gridCols
                    val boardHeightPx = pHeight * gridRows
                    translate(left = -col * pWidth, top = -row * pHeight) {
                        drawImage(
                            image = bitmap,
                            dstSize = IntSize(boardWidthPx.toInt(), boardHeightPx.toInt())
                        )
                    }
                } else {
                    drawRect(color = NeonPurple.copy(alpha = 0.4f))
                }
            }

            // Neon Border strokes
            val borderBrush = if (piece.isLocked) {
                Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.4f), NeonCyan.copy(alpha = 0.15f)))
            } else if (isDraggingLocal || piece.isDragging) {
                Brush.linearGradient(listOf(NeonPink, NeonPurple))
            } else {
                Brush.linearGradient(listOf(BorderColor, BorderColor))
            }

            drawPath(
                path = piecePath,
                brush = borderBrush,
                style = Stroke(width = if (isDraggingLocal || piece.isDragging) 4f else 2f)
            )
        }
    }
}

private fun getHorizontalEdge(r: Int, c: Int): Int {
    // Deterministic pseudo-random formula to generate a natural variety of edge shapes
    val hash = (r * 13 + c * 37 + 7) % 4
    return if (hash < 2) 1 else -1
}

private fun getVerticalEdge(r: Int, c: Int): Int {
    // Independent deterministic pseudo-random formula for vertical edges
    val hash = (r * 29 + c * 17 + 11) % 4
    return if (hash < 2) 1 else -1
}

private fun Path.addJigsawEdge(
    startX: Float, startY: Float,
    endX: Float, endY: Float,
    direction: Int
) {
    if (direction == 0) {
        lineTo(endX, endY)
        return
    }

    val dx = endX - startX
    val dy = endY - startY
    val len = kotlin.math.hypot(dx, dy)
    val tx = dx / len
    val ty = dy / len
    val nx = ty
    val ny = -tx

    val getPoint = { u: Float, v: Float ->
        Offset(
            startX + u * len * tx + v * len * nx,
            startY + u * len * ty + v * len * ny
        )
    }

    val d = direction.toFloat()

    // Segment 1: from (0,0) to left neck (0.44, 0.06 * d)
    val cp1a = getPoint(0.22f, -0.02f * d)
    val cp1b = getPoint(0.42f, 0.00f * d)
    val p1 = getPoint(0.44f, 0.06f * d)
    cubicTo(cp1a.x, cp1a.y, cp1b.x, cp1b.y, p1.x, p1.y)

    // Segment 2: from left neck to peak (0.50, 0.32 * d)
    val cp2a = getPoint(0.44f, 0.16f * d)
    val cp2b = getPoint(0.30f, 0.32f * d)
    val p2 = getPoint(0.50f, 0.32f * d)
    cubicTo(cp2a.x, cp2a.y, cp2b.x, cp2b.y, p2.x, p2.y)

    // Segment 3: from peak to right neck (0.56, 0.06 * d)
    val cp3a = getPoint(0.70f, 0.32f * d)
    val cp3b = getPoint(0.56f, 0.16f * d)
    val p3 = getPoint(0.56f, 0.06f * d)
    cubicTo(cp3a.x, cp3a.y, cp3b.x, cp3b.y, p3.x, p3.y)

    // Segment 4: from right neck to end (1.0, 0)
    val cp4a = getPoint(0.58f, 0.00f * d)
    val cp4b = getPoint(0.78f, -0.02f * d)
    val p4 = getPoint(1.0f, 0f)
    cubicTo(cp4a.x, cp4a.y, cp4b.x, cp4b.y, p4.x, p4.y)
}
