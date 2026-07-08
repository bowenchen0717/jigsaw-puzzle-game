package com.example.data

import kotlinx.coroutines.flow.Flow

class PuzzleRepository(private val puzzleDao: PuzzleDao) {
    val allCustomPuzzles: Flow<List<CustomPuzzle>> = puzzleDao.getAllCustomPuzzles()
    val allHistory: Flow<List<PuzzleHistory>> = puzzleDao.getAllHistory()

    suspend fun insertCustomPuzzle(title: String, filePath: String): Long {
        val puzzle = CustomPuzzle(title = title, filePath = filePath)
        return puzzleDao.insertCustomPuzzle(puzzle)
    }

    suspend fun deleteCustomPuzzle(id: Int) {
        puzzleDao.deleteCustomPuzzle(id)
    }

    suspend fun getHistoryForPuzzle(puzzleId: String): PuzzleHistory? {
        return puzzleDao.getHistoryForPuzzle(puzzleId)
    }

    suspend fun saveHistory(puzzleId: String, time: Long, moves: Int, difficulty: Int, completed: Boolean) {
        val existing = puzzleDao.getHistoryForPuzzle(puzzleId)
        val bestTime = if (existing != null && existing.isCompleted && existing.bestTime > 0) {
            if (completed) minOf(existing.bestTime, time) else existing.bestTime
        } else {
            if (completed) time else 0L
        }
        val bestMoves = if (existing != null && existing.isCompleted && existing.bestMoves > 0) {
            if (completed) minOf(existing.bestMoves, moves) else existing.bestMoves
        } else {
            if (completed) moves else 0
        }

        val history = PuzzleHistory(
            puzzleId = puzzleId,
            bestTime = bestTime,
            bestMoves = bestMoves,
            difficulty = difficulty,
            isCompleted = existing?.isCompleted == true || completed,
            lastPlayed = System.currentTimeMillis()
        )
        puzzleDao.insertOrUpdateHistory(history)
    }
}
