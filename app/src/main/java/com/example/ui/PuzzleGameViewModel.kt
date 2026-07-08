package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CustomPuzzle
import com.example.data.PuzzleHistory
import com.example.data.PuzzleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MatchState(
    val puzzleId: String = "",
    val title: String = "",
    val imageSource: Any? = null, // Int, String (file path), or String ("procedural:XYZ")
    val gridSize: Int = 3,
    val gridRows: Int = 3,
    val gridCols: Int = 3,
    val pieces: List<PuzzlePiece> = emptyList(),
    val isVictory: Boolean = false,
    val moves: Int = 0,
    val timerSeconds: Long = 0L,
    val isTimerRunning: Boolean = false,
    val showPreview: Boolean = false,
    val boardWidth: Float = 320f,
    val boardHeight: Float = 320f,
    val matchToken: Long = 0L
)

class PuzzleGameViewModel(private val repository: PuzzleRepository) : ViewModel() {

    private val _matchState = MutableStateFlow(MatchState())
    val matchState: StateFlow<MatchState> = _matchState.asStateFlow()

    val customPuzzles: StateFlow<List<CustomPuzzle>> = repository.allCustomPuzzles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyList: StateFlow<List<PuzzleHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    init {
        startTimerCoroutine()
    }

    private fun startTimerCoroutine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _matchState.value
                if (state.isTimerRunning && !state.isVictory) {
                    _matchState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                }
            }
        }
    }

    fun startMatch(puzzleId: String, title: String, source: Any, gridSize: Int) {
        val boardWidth = 320f
        val boardHeight = 320f
        val gridRows = gridSize
        val gridCols = gridSize
        val pieceWidth = boardWidth / gridCols
        val pieceHeight = boardHeight / gridRows
        val list = mutableListOf<PuzzlePiece>()
        
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val id = row * gridCols + col
                val targetLeft = col * pieceWidth
                val targetTop = row * pieceHeight
                
                // Scramble pieces into the tray area:
                // Width: 320dp, height: 140dp (between Y = 340dp and 480dp)
                val randomX = Random.nextFloat() * (boardWidth - pieceWidth)
                val randomY = 340f + Random.nextFloat() * (140f - pieceHeight)
                
                list.add(
                    PuzzlePiece(
                        id = id,
                        row = row,
                        col = col,
                        targetLeft = targetLeft,
                        targetTop = targetTop,
                        currentX = randomX,
                        currentY = randomY,
                        isLocked = false,
                        isDragging = false
                    )
                )
            }
        }

        // Shuffle the list to randomize compile drawing z-order, except keep them unlocked
        list.shuffle()

        _matchState.value = MatchState(
            puzzleId = puzzleId,
            title = title,
            imageSource = source,
            gridSize = gridSize,
            gridRows = gridRows,
            gridCols = gridCols,
            pieces = list,
            isVictory = false,
            moves = 0,
            timerSeconds = 0L,
            isTimerRunning = true,
            showPreview = false,
            boardWidth = boardWidth,
            boardHeight = boardHeight,
            matchToken = System.currentTimeMillis()
        )
    }

    fun resizeMatchBoard(imageWidth: Int, imageHeight: Int) {
        if (imageWidth <= 0 || imageHeight <= 0) return
        val aspectRatio = imageWidth.toFloat() / imageHeight.toFloat()

        val maxBoardWidth = 320f
        val maxBoardHeight = 320f

        val boardWidth: Float
        val boardHeight: Float
        if (aspectRatio >= 1f) {
            boardWidth = maxBoardWidth
            boardHeight = maxBoardWidth / aspectRatio
        } else {
            boardHeight = maxBoardHeight
            boardWidth = maxBoardHeight * aspectRatio
        }

        val offsetX = (maxBoardWidth - boardWidth) / 2f
        val offsetY = (maxBoardHeight - boardHeight) / 2f

        _matchState.update { currentState ->
            val gridSize = currentState.gridSize
            val gridRows: Int
            val gridCols: Int

            if (aspectRatio >= 1.15f) {
                // Wide image (landscape)
                gridRows = gridSize
                gridCols = Math.round(gridSize * aspectRatio).toInt().coerceIn(2, 8)
            } else if (aspectRatio <= 0.85f) {
                // Tall image (portrait)
                gridCols = gridSize
                gridRows = Math.round(gridSize / aspectRatio).toInt().coerceIn(2, 8)
            } else {
                // Roughly square
                gridRows = gridSize
                gridCols = gridSize
            }

            val pieceWidth = boardWidth / gridCols
            val pieceHeight = boardHeight / gridRows
            val list = mutableListOf<PuzzlePiece>()

            for (row in 0 until gridRows) {
                for (col in 0 until gridCols) {
                    val id = row * gridCols + col
                    val targetLeft = offsetX + col * pieceWidth
                    val targetTop = offsetY + row * pieceHeight

                    // Scramble pieces into the tray area:
                    val randomX = Random.nextFloat() * (320f - pieceWidth)
                    val randomY = 340f + Random.nextFloat() * (130f - pieceHeight)

                    list.add(
                        PuzzlePiece(
                            id = id,
                            row = row,
                            col = col,
                            targetLeft = targetLeft,
                            targetTop = targetTop,
                            currentX = randomX,
                            currentY = randomY,
                            isLocked = false,
                            isDragging = false
                        )
                    )
                }
            }
            list.shuffle()

            currentState.copy(
                boardWidth = boardWidth,
                boardHeight = boardHeight,
                gridRows = gridRows,
                gridCols = gridCols,
                pieces = list
            )
        }
    }

    fun dragPiece(pieceId: Int, deltaXDp: Float, deltaYDp: Float) {
        val state = _matchState.value
        if (state.isVictory) return
        
        _matchState.update { currentState ->
            val updatedPieces = currentState.pieces.map { piece ->
                if (piece.id == pieceId && !piece.isLocked) {
                    piece.copy(
                        currentX = (piece.currentX + deltaXDp).coerceIn(-50f, 370f),
                        currentY = (piece.currentY + deltaYDp).coerceIn(0f, 550f)
                    )
                } else {
                    piece
                }
            }
            currentState.copy(pieces = updatedPieces)
        }
    }

    fun startDragging(pieceId: Int, startX: Float, startY: Float) {
        _matchState.update { currentState ->
            val clickedPiece = currentState.pieces.find { it.id == pieceId }
            if (clickedPiece == null || clickedPiece.isLocked) return@update currentState
            
            // Bring the dragging piece to the END of the list so it renders on top
            val filtered = currentState.pieces.filter { it.id != pieceId }
            val updated = filtered + clickedPiece.copy(
                isDragging = true,
                currentX = startX,
                currentY = startY
            )
            currentState.copy(pieces = updated)
        }
    }

    fun stopDragging(pieceId: Int, finalX: Float, finalY: Float) {
        val state = _matchState.value
        val piece = state.pieces.find { it.id == pieceId } ?: return
        if (piece.isLocked) return

        val pieceWidth = state.boardWidth / state.gridCols
        val pieceHeight = state.boardHeight / state.gridRows
        val thresholdX = (pieceWidth * 0.45f).coerceIn(32f, 48f)
        val thresholdY = (pieceHeight * 0.45f).coerceIn(32f, 48f)

        val distanceX = Math.abs(finalX - piece.targetLeft)
        val distanceY = Math.abs(finalY - piece.targetTop)
        val isCloseEnough = distanceX < thresholdX && distanceY < thresholdY

        _matchState.update { currentState ->
            val updatedPieces = currentState.pieces.map { p ->
                if (p.id == pieceId) {
                    if (isCloseEnough) {
                        p.copy(
                            currentX = p.targetLeft,
                            currentY = p.targetTop,
                            isLocked = true,
                            isDragging = false
                        )
                    } else {
                        p.copy(
                            currentX = finalX,
                            currentY = finalY,
                            isDragging = false
                        )
                    }
                } else {
                    p
                }
            }

            val checkVictory = updatedPieces.all { it.isLocked }
            val nextMoves = currentState.moves + 1
            
            if (checkVictory) {
                // Save record to DB
                viewModelScope.launch {
                    repository.saveHistory(
                        puzzleId = currentState.puzzleId,
                        time = currentState.timerSeconds,
                        moves = nextMoves,
                        difficulty = currentState.gridSize,
                        completed = true
                    )
                }
                currentState.copy(
                    pieces = updatedPieces,
                    isVictory = true,
                    isTimerRunning = false,
                    moves = nextMoves
                )
            } else {
                currentState.copy(pieces = updatedPieces, moves = nextMoves)
            }
        }
    }

    fun resetMatch() {
        val state = _matchState.value
        if (state.puzzleId.isEmpty()) return
        startMatch(state.puzzleId, state.title, state.imageSource ?: "", state.gridSize)
    }

    fun togglePreview(show: Boolean) {
        _matchState.update { it.copy(showPreview = show) }
    }

    fun addCustomPuzzle(title: String, filePath: String) {
        viewModelScope.launch {
            repository.insertCustomPuzzle(title, filePath)
        }
    }

    fun deleteCustomPuzzle(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomPuzzle(id)
        }
    }
}

class ViewModelFactory(private val repository: PuzzleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuzzleGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PuzzleGameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
