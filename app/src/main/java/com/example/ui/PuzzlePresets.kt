package com.example.ui

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.R

data class PresetPuzzle(
    val id: String,
    val titleEn: String,
    val titleCn: String,
    val imageRes: Int? = null,
    val category: Category = Category.ABSTRACT
) {
    enum class Category { ABSTRACT, ANIMALS }
}

object PuzzlePresets {
    val list = listOf(
        PresetPuzzle(
            id = "preset_cyberpunk",
            titleEn = "Cyberpunk City",
            titleCn = "霓虹霓虹都市",
            imageRes = R.drawable.img_cyberpunk_city_1782897470770,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_synthwave",
            titleEn = "Synthwave Sunset",
            titleCn = "復古電子日落",
            imageRes = R.drawable.img_synthwave_sunset_1782897483134,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_skater_cat",
            titleEn = "Graffiti Skater Cat",
            titleCn = "潮流街頭貓",
            imageRes = R.drawable.img_skater_cat_1782897498022,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_lofi_room",
            titleEn = "Cozy Lofi Bedroom",
            titleCn = "溫馨Lofi小屋",
            imageRes = R.drawable.img_lofi_room_1782897508426,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_celestial_moon",
            titleEn = "Celestial Mystic Moon",
            titleCn = "奇幻星塵明月",
            imageRes = R.drawable.img_mystic_moon_1782897519268,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_proc_grid",
            titleEn = "Neon Cyber Grid",
            titleCn = "霓虹網格矩陣",
            imageRes = R.drawable.neon_cyber_grid_1782920497332,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_proc_aurora",
            titleEn = "Fluorescent Aurora",
            titleCn = "流光極光幻境",
            imageRes = R.drawable.fluorescent_aurora_1782920508180,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_proc_pixel",
            titleEn = "8-Bit Retro Invaders",
            titleCn = "八位元復古機",
            imageRes = R.drawable.retro_invaders_1782920518322,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_proc_circles",
            titleEn = "Golden Ratio Zen Rings",
            titleCn = "禪意黃金圓環",
            imageRes = R.drawable.zen_circles_1782920527687,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "preset_proc_matrix",
            titleEn = "Binary Matrix Rain",
            titleCn = "數位矩陣降雨",
            imageRes = R.drawable.matrix_rain_1782920538280,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(
            id = "abstract_1",
            titleEn = "Geometric Dreams",
            titleCn = "幾何夢境",
            imageRes = R.drawable.abstract_art_1_1782924023159,
            category = PresetPuzzle.Category.ABSTRACT
        ),
        PresetPuzzle(id = "dog_golden", titleEn = "Golden Retriever", titleCn = "黃金獵犬", imageRes = R.drawable.dog_golden_retriever_1782922385163, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_frenchie", titleEn = "French Bulldog", titleCn = "法國鬥牛犬", imageRes = R.drawable.dog_french_bulldog_1782922398307, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_corgi", titleEn = "Corgi", titleCn = "柯基犬", imageRes = R.drawable.dog_corgi_1782922413340, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_husky", titleEn = "Husky", titleCn = "哈士奇", imageRes = R.drawable.dog_husky_1782922422641, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_poodle", titleEn = "Poodle", titleCn = "貴賓犬", imageRes = R.drawable.dog_poodle_1782922434464, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_shiba", titleEn = "Shiba Inu", titleCn = "柴犬", imageRes = R.drawable.dog_shiba_inu_1782922447711, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_dachshund", titleEn = "Dachshund", titleCn = "臘腸犬", imageRes = R.drawable.dog_dachshund_1782922460202, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_collie", titleEn = "Border Collie", titleCn = "邊境牧羊犬", imageRes = R.drawable.dog_border_collie_1782922470704, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_beagle", titleEn = "Beagle", titleCn = "米格魯", imageRes = R.drawable.dog_beagle_1782922481154, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "dog_pug", titleEn = "Pug", titleCn = "巴哥犬", imageRes = R.drawable.dog_pug_1782922492788, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_maine_coon", titleEn = "Maine Coon", titleCn = "緬因貓", imageRes = R.drawable.cat_maine_coon_1782924627592, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_siamese", titleEn = "Siamese", titleCn = "暹羅貓", imageRes = R.drawable.cat_siamese_1782924638531, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_british_shorthair", titleEn = "British Shorthair", titleCn = "英國短毛貓", imageRes = R.drawable.cat_british_shorthair_1782924648399, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_ragdoll", titleEn = "Ragdoll", titleCn = "布偶貓", imageRes = R.drawable.cat_ragdoll_1782924657349, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_scottish_fold", titleEn = "Scottish Fold", titleCn = "蘇格蘭摺耳貓", imageRes = R.drawable.cat_scottish_fold_1782924669805, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_black_cat", titleEn = "Black Cat", titleCn = "黑貓", imageRes = R.drawable.cat_black_cat_1782924679882, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_persian", titleEn = "Persian", titleCn = "波斯貓", imageRes = R.drawable.cat_persian_1782924690681, category = PresetPuzzle.Category.ANIMALS),
        PresetPuzzle(id = "cat_bengal", titleEn = "Bengal", titleCn = "孟加拉貓", imageRes = R.drawable.cat_bengal_1782924701269, category = PresetPuzzle.Category.ANIMALS)
    )

    fun loadBitmap(context: Context, source: Any, width: Int = 600, height: Int = 600): ImageBitmap? {
        return try {
            when (source) {
                is Int -> {
                    BitmapFactory.decodeResource(context.resources, source)?.asImageBitmap()
                }
                is String -> {
                    BitmapFactory.decodeFile(source)?.asImageBitmap()
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
