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
    val category: Category = Category.ABSTRACT,
    val descriptionEn: String? = null,
    val descriptionCn: String? = null
) {
    enum class Category { ABSTRACT, ANIMALS, BIBLE }
}

object PuzzlePresets {
    val list = listOf(
        PresetPuzzle(
            id = "bible_mark_1_1",
            titleEn = "Jesus Christ, the Son of God",
            titleCn = "神的兒子耶穌基督",
            imageRes = R.drawable.img_bible_jesus_gospel_1783568484990,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「神的兒子，耶穌基督福音的起頭。」——馬可福音 1:1\n\n這節經文莊嚴地宣示了福音的開端，揭示了耶穌基督作為上帝之子的救贖與恩典。祂如同晨星，將神聖溫暖的光芒灑向世人，為生命注入永恆的真理、指引與盼望。",
            descriptionEn = "\"The beginning of the gospel of Jesus Christ, the Son of God.\" — Mark 1:1\n\nThis powerful verse proclaims the dawn of the gospel, revealing Jesus Christ as the divine Son of God. His heavenly radiant light breaks through the dark shadows, guiding souls with eternal peace, love, and salvation."
        ),
        PresetPuzzle(
            id = "bible_mark_1_17",
            titleEn = "Fishers of Men",
            titleCn = "得人如得魚",
            imageRes = R.drawable.bible_fishers_men_1783569499701,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶穌對他們說：『來跟從我，我要叫你們得人如得魚一樣。』」——馬可福音 1:17\n\n耶穌在加利利海邊呼召門徒，向他們發出宏大而溫暖的邀請。這是一個神聖的轉折，引導我們放下世俗的網，跟隨祂的腳步，去拯救、溫暖、得著更多迷失的寶貴生命。",
            descriptionEn = "Jesus said to them, \"Follow Me, and I will make you become fishers of men.\" — Mark 1:17\n\nBy the peaceful shores of Galilee, Jesus called His very first disciples, extending a divine invitation. This verse urges us to leave our old nets behind, follow His lead, and gather precious souls into His warm embrace of love."
        ),
        PresetPuzzle(
            id = "bible_mark_4_39",
            titleEn = "Calming the Storm",
            titleCn = "平息風浪",
            imageRes = R.drawable.bible_calm_storm_1783569512025,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶穌醒了，斥責風，向海說：『住了吧！靜了吧！』風就止住，大大地平靜了。」——馬可福音 4:39\n\n在人生狂風暴雨的大海中，恐懼常令我們顫抖。但只要有耶穌在船上，祂只消一聲宣告，便能讓咆哮的風浪瞬間止息。祂是賜予絕對平安的主，將無比的寧靜與安慰注入我們的心靈。",
            descriptionEn = "Then He arose and rebuked the wind, and said to the sea, \"Peace, be still!\" And the wind ceased and there was a great calm. — Mark 4:39\n\nIn the middle of life's raging storms, fear often grips our hearts. Yet, with Jesus on our boat, His single word can quiet the loudest gale and calm the roughest waters, restoring absolute peace, safety, and tranquil rest."
        ),
        PresetPuzzle(
            id = "bible_mark_8_34",
            titleEn = "Take Up the Cross",
            titleCn = "背起十字架",
            imageRes = R.drawable.bible_take_cross_1783569522914,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「若有人要跟從我，就當捨己，背起他的十字架來跟從我。」——馬可福音 8:34\n\n跟隨耶穌是一條充滿恩典與榮耀、卻也需要勇氣的生命之路。十字架並非重擔，而是戰勝自我、走向永生之光的徽章。當我們甘心背起它時，神聖的榮光正照亮前方的道路。",
            descriptionEn = "He said to them, \"Whoever desires to come after Me, let him deny himself, and take up his cross, and follow Me.\" — Mark 8:34\n\nFollowing Jesus is a journey of grace and glory that calls for courage. The cross is not a burden, but a badge of victory over self and a path to eternal light. When we carry it, a heavenly sunrise guides our steps."
        ),
        PresetPuzzle(
            id = "bible_mark_9_23",
            titleEn = "Everything is Possible",
            titleCn = "在信的人凡事都能",
            imageRes = R.drawable.bible_faith_possible_1783569534578,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶穌對他說：『你若能信，在信的人，凡事都能。』」——馬可福音 9:23\n\n當眼前的困境看似無法逾越、深淵重重時，信心是穿透黑暗、開啟奇蹟之門的鑰匙。只要我們全然信靠，神那浩瀚無限的恩典與大能便會為我們開闢道路，化不可能為坦途。",
            descriptionEn = "Jesus said to him, \"If you can believe, all things are possible to him who believes.\" — Mark 9:23\n\nWhen we are surrounded by obstacles and deep canyons of doubt, simple faith is the key that unlocks heavenly miracles. By trusting in Him completely, His limitless grace opens glorious pathways of hope out of the dark."
        ),
        PresetPuzzle(
            id = "bible_mark_10_14",
            titleEn = "Let the Children Come",
            titleCn = "讓小孩子到我這裡來",
            imageRes = R.drawable.bible_jesus_children_1783569544620,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「讓小孩子到我這裡來，不要禁止他們；因為在神國的，正是這樣的人。」——馬可福音 10:14\n\n耶穌張開溫暖的雙臂，微笑迎接最純真、最無防備的孩子們。這段經文提醒我們，神國的大門正向那些保持著純真、善良、謙卑、且毫無保留信靠天父的人們而敞開。",
            descriptionEn = "\"Let the little children come to Me, and do not forbid them; for of such is the kingdom of God.\" — Mark 10:14\n\nWith a warm smile, Jesus welcomes the innocent and pure-hearted children into His loving arms. He teaches us that God's glorious kingdom belongs to those who trust their heavenly Father with humble, pure, and childlike sincerity."
        ),
        PresetPuzzle(
            id = "bible_mark_10_27",
            titleEn = "Possible with God",
            titleCn = "在神凡事都能",
            imageRes = R.drawable.bible_possible_god_1783569553845,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶穌看著他們，說：『在人是不能，在神卻不然，因為在神凡事都能。』」——馬可福音 10:27\n\n人的力量有限，常在峭壁和深谷面前感到無能為力。然而神的智慧與大能無窮無盡，祂在星空中漫步，在曠野中開道路。將重擔交託給神，祂必引領你穿過高山，見證生命的奇蹟。",
            descriptionEn = "Jesus looked at them and said, \"With men it is impossible, but not with God; for with God all things are possible.\" — Mark 10:27\n\nHuman strength is limited, often failing before steep cliffs and deep valleys. But God's wisdom is boundless; He paves highways in the desert and rules the cosmos. Surrender to Him, and He will guide you to see His wonders."
        ),
        PresetPuzzle(
            id = "bible_mark_10_45",
            titleEn = "To Serve and Give Life",
            titleCn = "捨命作多人的贖價",
            imageRes = R.drawable.bible_servant_savior_1783569564243,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「因為人子來，並不是要受人的服事，乃是要服事人，並且要捨命作多人的贖價。」——馬可福音 10:45\n\n萬王之王沒有穿戴世俗的冠冕，而是以僕人的姿態，俯身洗腳、醫治病痛，並最終在十字架上為世人獻出生命。祂用極致的謙卑與愛，重新定義了偉大，點亮了無數在黑暗中摸索的心靈。",
            descriptionEn = "\"For even the Son of Man did not come to be served, but to serve, and to give His life a ransom for many.\" — Mark 10:45\n\nThe King of Kings did not wear a worldly crown; instead, He knelt to wash feet, healed the sick, and gave His life on the cross. In His beautiful humility, He redefined greatness and illuminated our souls with everlasting love."
        ),
        PresetPuzzle(
            id = "bible_mark_12_30",
            titleEn = "Love Your God",
            titleCn = "盡心盡意愛主你的神",
            imageRes = R.drawable.bible_love_god_1783569575145,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「你要盡心、盡性、盡意、盡力愛主你的神。」——馬可福音 12:30\n\n這是最偉大、最核心的誡命：將我們全部的情感、靈魂、思想與力量，都奉獻給這位愛我們的創造主。當我們的生命與祂那源源不絕的宇宙之愛連結時，便能尋得靈魂深處最大的滿足與喜樂。",
            descriptionEn = "\"And you shall love the Lord your God with all your heart, with all your soul, with all your mind, and with all your strength.\" — Mark 12:30\n\nThis is the first and greatest commandment: to direct our entire heart, soul, mind, and energy toward the Creator who first loved us. When our lives align with His infinite celestial love, we find true purpose and overflowing joy."
        ),
        PresetPuzzle(
            id = "bible_mark_16_6",
            titleEn = "He Is Risen",
            titleCn = "祂已經復活了",
            imageRes = R.drawable.bible_resurrection_1783569588711,
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「那少年人對他們說：『不要驚恐！你們尋找那釘十字架的拿撒勒人耶穌，他已經復活了，不在這裡。』」——馬可福音 16:6\n\n清晨的微光灑在空墓穴上，巨石已滾開，死亡的權勢在生命的曙光前崩解。耶穌已經復活了！這是一場最輝煌的勝利，為整個世界帶來了永不褪色的救贖、新生與永恆的盼望。",
            descriptionEn = "But he said to them, \"Do not be alarmed. You seek Jesus of Nazareth, who was crucified. He is risen! He is not here.\" — Mark 16:6\n\nMorning light floods the empty tomb as the massive stone is rolled away. Death's grip is broken forever by the dawn of life. Jesus is risen! This triumphant victory brings the promise of renewal, hope, and eternal life to all."
        ),
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
