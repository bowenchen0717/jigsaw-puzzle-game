package com.example.ui

data class PuzzlePiece(
    val id: Int,
    val row: Int,
    val col: Int,
    val targetLeft: Float, // target X coordinate in DP (relative to the board)
    val targetTop: Float,  // target Y coordinate in DP (relative to the board)
    val currentX: Float,   // current X coordinate in DP
    val currentY: Float,   // current Y coordinate in DP
    val isLocked: Boolean = false,
    val isDragging: Boolean = false,
    val shuffleOrder: Int = 0
)
