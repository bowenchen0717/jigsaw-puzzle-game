package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.PuzzleDatabase
import com.example.data.PuzzleRepository

class PuzzleApplication : Application() {
    lateinit var repository: PuzzleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(
            applicationContext,
            PuzzleDatabase::class.java,
            "puzzle_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        repository = PuzzleRepository(database.puzzleDao())
    }
}
