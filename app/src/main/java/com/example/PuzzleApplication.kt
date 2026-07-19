package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.PuzzleDatabase
import com.example.data.PuzzleRepository
import com.example.data.RemoteConfigManager
import com.example.data.UserPreferences

class PuzzleApplication : Application() {
    lateinit var repository: PuzzleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Preferences
        UserPreferences.init(this)
        
        // Initialize Firebase Remote Config (with safe fallbacks)
        RemoteConfigManager.init(this)

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
