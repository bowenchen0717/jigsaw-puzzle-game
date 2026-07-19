package com.example.data

import android.content.Context
import android.util.Log
import com.example.ui.PresetPuzzle
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    var isInitialized = false
        private set

    private val _latestVersionCode = MutableStateFlow(12) // match current version in build.gradle
    val latestVersionCode: StateFlow<Int> = _latestVersionCode

    private val _updateMessage = MutableStateFlow("一個全新的版本已推出，請點選更新以體驗最新的特色！")
    val updateMessage: StateFlow<String> = _updateMessage

    private val _announcementText = MutableStateFlow("")
    val announcementText: StateFlow<String> = _announcementText

    private val _isAnnouncementVisible = MutableStateFlow(false)
    val isAnnouncementVisible: StateFlow<Boolean> = _isAnnouncementVisible

    private val _featuredPuzzleId = MutableStateFlow("")
    val featuredPuzzleId: StateFlow<String> = _featuredPuzzleId

    private val _remoteCategories = MutableStateFlow<List<PresetPuzzle.Category>>(
        listOf(
            PresetPuzzle.Category.ABSTRACT,
            PresetPuzzle.Category.ANIMALS,
            PresetPuzzle.Category.BIBLE,
            PresetPuzzle.Category.SCENERY
        )
    )
    val remoteCategories: StateFlow<List<PresetPuzzle.Category>> = _remoteCategories

    private val _remotePuzzles = MutableStateFlow<List<PresetPuzzle>>(emptyList())
    val remotePuzzles: StateFlow<List<PresetPuzzle>> = _remotePuzzles

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(if (com.example.BuildConfig.DEBUG) 0 else 3600) // 0 in debug for instant updates
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            val defaultCategoryListJson = """
                [
                  {"id": "ABSTRACT", "nameEn": "Abstract", "nameCn": "抽象風格"},
                  {"id": "ANIMALS", "nameEn": "Animals", "nameCn": "可愛動物"},
                  {"id": "BIBLE", "nameEn": "Bible", "nameCn": "聖經故事"},
                  {"id": "SCENERY", "nameEn": "Scenery", "nameCn": "風景名勝"}
                ]
            """.trimIndent()

            val defaultPuzzleListJson = """
                [
                  {
                    "id": "remote_heavenly_light",
                    "titleEn": "Heavenly Light",
                    "titleCn": "聖光照耀",
                    "imageUrl": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&q=80&w=600",
                    "category": "BIBLE",
                    "descriptionCn": "來自雲端的推薦拼圖！「在祂裡面有生命，這生命就是人的光。」——約翰福音 1:4",
                    "descriptionEn": "In Him was life, and the life was the light of men. — John 1:4"
                  },
                  {
                    "id": "remote_golden_retriever",
                    "titleEn": "Cloud Golden Retriever",
                    "titleCn": "雲端小黃金",
                    "imageUrl": "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=600",
                    "category": "ANIMALS",
                    "descriptionCn": "動態更新的可愛動物拼圖，完全不需要重新下載 App 即可暢玩！",
                    "descriptionEn": "A dynamic cute animal puzzle loaded instantly from the cloud without updating the app!"
                  }
                ]
            """.trimIndent()

            val defaults = mapOf(
                "latest_version_code" to 12L,
                "update_message" to "一個全新的版本已推出，請點選更新以體驗最新的特色！",
                "announcement_text" to "歡迎來到我行我速拼圖快手！全新耶穌基督與福音系列、可愛動物拼圖與科幻霓虹系列均已上線！快來挑戰吧！",
                "is_announcement_visible" to true,
                "featured_puzzle_id" to "",
                "category_list" to defaultCategoryListJson,
                "puzzle_list" to defaultPuzzleListJson
            )
            remoteConfig.setDefaultsAsync(defaults)
            
            isInitialized = true
            Log.d(TAG, "Firebase Remote Config initialized successfully.")
            
            // Set initial default values
            _remoteCategories.value = parseCategoryList(defaultCategoryListJson)
            _remotePuzzles.value = parsePuzzleList(defaultPuzzleListJson)

            // Apply currently active (cached) values or defaults immediately
            applyConfigValues()
            
            fetchAndActivate()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Remote Config. Using offline default values.", e)
            isInitialized = false
        }
    }

    fun fetchAndActivate() {
        if (!isInitialized) return
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Remote Config fetched and activated successfully.")
                        applyConfigValues()
                    } else {
                        Log.w(TAG, "Remote Config fetch failed.")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Remote Config", e)
        }
    }

    private fun applyConfigValues() {
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            _latestVersionCode.value = remoteConfig.getLong("latest_version_code").toInt()
            _updateMessage.value = remoteConfig.getString("update_message")
            _announcementText.value = remoteConfig.getString("announcement_text")
            _isAnnouncementVisible.value = remoteConfig.getBoolean("is_announcement_visible")
            _featuredPuzzleId.value = remoteConfig.getString("featured_puzzle_id")
            
            val categoryListJson = remoteConfig.getString("category_list")
            Log.d(TAG, "Remote Config loaded category_list: ${categoryListJson.take(100)}...")
            _remoteCategories.value = parseCategoryList(categoryListJson)

            val puzzleListJson = remoteConfig.getString("puzzle_list")
            Log.d(TAG, "Remote Config loaded puzzle_list: ${puzzleListJson.take(100)}...")
            _remotePuzzles.value = parsePuzzleList(puzzleListJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error applying Remote Config values", e)
        }
    }

    private fun parseCategoryList(jsonString: String?): List<PresetPuzzle.Category> {
        val defaultList = listOf(
            PresetPuzzle.Category.ABSTRACT,
            PresetPuzzle.Category.ANIMALS,
            PresetPuzzle.Category.BIBLE,
            PresetPuzzle.Category.SCENERY
        )
        if (jsonString.isNullOrEmpty()) return defaultList
        val list = mutableListOf<PresetPuzzle.Category>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val nameEn = obj.optString("nameEn", id)
                val nameCn = obj.optString("nameCn", id)
                list.add(PresetPuzzle.Category(id, nameEn, nameCn))
            }
            // Ensure all hardcoded default categories are present in the list
            for (defaultCat in defaultList) {
                if (list.none { it.id.equals(defaultCat.id, ignoreCase = true) }) {
                    list.add(defaultCat)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse category_list JSON: $jsonString", e)
            return defaultList
        }
        return list
    }

    private fun parsePuzzleList(jsonString: String?): List<PresetPuzzle> {
        if (jsonString.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<PresetPuzzle>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val titleEn = obj.optString("titleEn", "")
                val titleCn = obj.optString("titleCn", "")
                val imageUrl = obj.optString("imageUrl", "")
                val categoryStr = obj.optString("category", "ABSTRACT")
                val category = _remoteCategories.value.find { it.id.equals(categoryStr, ignoreCase = true) }
                    ?: PresetPuzzle.Category.valueOf(categoryStr)
                val descriptionEn: String? = if (obj.has("descriptionEn")) obj.getString("descriptionEn") else null
                val descriptionCn: String? = if (obj.has("descriptionCn")) obj.getString("descriptionCn") else null
                
                list.add(
                    PresetPuzzle(
                        id = id,
                        titleEn = titleEn,
                        titleCn = titleCn,
                        imageRes = null,
                        category = category,
                        descriptionEn = descriptionEn,
                        descriptionCn = descriptionCn,
                        imageUrl = imageUrl
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse puzzle_list JSON: $jsonString", e)
        }
        return list
    }
}
