package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "custom_puzzles")
data class CustomPuzzle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val filePath: String,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "puzzle_history")
data class PuzzleHistory(
    @PrimaryKey val puzzleId: String, // e.g. "preset_0", "custom_1"
    val bestTime: Long, // in seconds
    val bestMoves: Int,
    val difficulty: Int, // e.g. 3 (3x3), 4 (4x4), 5 (5x5)
    val isCompleted: Boolean = false,
    val lastPlayed: Long = System.currentTimeMillis()
)

@Dao
interface PuzzleDao {
    @Query("SELECT * FROM custom_puzzles ORDER BY dateAdded DESC")
    fun getAllCustomPuzzles(): Flow<List<CustomPuzzle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomPuzzle(puzzle: CustomPuzzle): Long

    @Query("DELETE FROM custom_puzzles WHERE id = :id")
    suspend fun deleteCustomPuzzle(id: Int)

    @Query("SELECT * FROM puzzle_history")
    fun getAllHistory(): Flow<List<PuzzleHistory>>

    @Query("SELECT * FROM puzzle_history WHERE puzzleId = :puzzleId LIMIT 1")
    suspend fun getHistoryForPuzzle(puzzleId: String): PuzzleHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(history: PuzzleHistory)
}

@Database(entities = [CustomPuzzle::class, PuzzleHistory::class], version = 1, exportSchema = false)
abstract class PuzzleDatabase : RoomDatabase() {
    abstract fun puzzleDao(): PuzzleDao
}
