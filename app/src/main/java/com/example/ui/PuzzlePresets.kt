package com.example.ui

import android.content.Context
import android.graphics.*
import coil.imageLoader
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class PresetPuzzle(
    val id: String,
    val titleEn: String,
    val titleCn: String,
    val imageRes: Int? = null,
    val category: Category = Category.ABSTRACT,
    val descriptionEn: String? = null,
    val descriptionCn: String? = null,
    val imageUrl: String? = null
) {
    class Category(val id: String, val nameEn: String, val nameCn: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Category) return false
            return id == other.id
        }
        override fun hashCode(): Int = id.hashCode()

        companion object {
            val ABSTRACT = Category("ABSTRACT", "Abstract", "抽象風格")
            val ANIMALS = Category("ANIMALS", "Animals", "可愛動物")
            val BIBLE = Category("BIBLE", "Bible", "聖經故事")
            val SCENERY = Category("SCENERY", "Scenery", "風景名勝")

            fun values(): Array<Category> = arrayOf(ABSTRACT, ANIMALS, BIBLE, SCENERY)

            fun valueOf(value: String): Category {
                return when (value.uppercase()) {
                    "ABSTRACT" -> ABSTRACT
                    "ANIMALS" -> ANIMALS
                    "BIBLE" -> BIBLE
                    "SCENERY" -> SCENERY
                    else -> Category(value.uppercase(), value, value)
                }
            }
        }
    }
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
            id = "bible_genesis_1_3",
            titleEn = "Let There Be Light",
            titleCn = "要有光",
            imageUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「神說：『要有光』，就有了光。」——創世記 1:3\n\n這是在宇宙太初、混沌深淵中發出的第一道神聖宣言。神的話語一出，溫暖而輝煌的奇異光芒瞬間撕裂黑暗，照亮了浩瀚太虛，成為萬物生命的根基，也象徵著希望的誕生。",
            descriptionEn = "And God said, \"Let there be light,\" and there was light. — Genesis 1:3\n\nThis is the very first divine proclamation spoken into the chaotic depths of the early universe. With His word, radiant celestial light burst forth, scattering the darkness and establishing the foundation for all life, hope, and beauty."
        ),
        PresetPuzzle(
            id = "bible_psalm_23_1",
            titleEn = "The Lord is My Shepherd",
            titleCn = "耶和華是我的牧者",
            imageUrl = "https://images.unsplash.com/photo-1484557985045-edf25e08da73?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶和華是我的牧者，我必不致缺乏。」——詩篇 23:1\n\n在大自然靜謐的綠色牧場旁，慈愛的牧人悉心看顧著羊群。這節無比溫馨的經文提醒我們，不論路途多麼崎嶇，神永遠是我們最忠實、體貼的引路人，供應我們身心靈的一切所需，賜下無限的平安與飽足。",
            descriptionEn = "The Lord is my shepherd; I shall not want. — Psalm 23:1\n\nSet in a beautiful peaceful pasture, this beloved verse reminds us that we are tenderly cared for by a loving Guide. Under His watchful eye and gentle staff, we lack nothing, led beside quiet waters into perfect comfort, safety, and spiritual rest."
        ),
        PresetPuzzle(
            id = "bible_proverbs_3_5",
            titleEn = "Trust in the Lord",
            titleCn = "專心仰賴耶和華",
            imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「你要專心仰賴耶和華，不可倚靠自己的聰明。」——箴言 3:5\n\n當我們置身於崇山峻嶺之上，望著前方未知的重重雲海時，這節經文教導我們卸下自我的籌算。全心將步伐交託給神，仰望祂在晨光中為我們撥雲見日，引領我們走上平坦的福樂大道。",
            descriptionEn = "Trust in the Lord with all your heart and lean not on your own understanding. — Proverbs 3:5\n\nStanding atop a magnificent mountain peak, surrounded by a sea of clouds, this verse invites us to surrender our worries and limited sight. Placing our full weight on His wisdom, He clears the mist to reveal beautiful, secure paths."
        ),
        PresetPuzzle(
            id = "bible_isaiah_40_31",
            titleEn = "Soar on Wings Like Eagles",
            titleCn = "如鷹展翅上騰",
            imageUrl = "https://images.unsplash.com/photo-1611689342806-0863700ce1e4?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「但那等候耶和華的必重新得力。他們必如鷹展翅上騰...」——以賽亞書 40:31\n\n當靈魂疲憊、雙翼沉重之際，這句話為我們注入天上的新力量。如同雄鷹乘著上升的熱氣流，在金色的曙光中衝破風雨、翱翔於群山萬壑之上，享受超越一切阻礙的自由與高天之上的遼闊。",
            descriptionEn = "But those who hope in the Lord will renew their strength. They will soar on wings like eagles... — Isaiah 40:31\n\nWhen our spirits are weary, this powerful promise lifts us up. Like a magnificent eagle catching thermal currents, we are enabled to rise above storms, soaring gracefully into a golden sunrise with renewed grace, courage, and divine energy."
        ),
        PresetPuzzle(
            id = "bible_matthew_5_14",
            titleEn = "The Light of the World",
            titleCn = "世上的光",
            imageUrl = "https://images.unsplash.com/photo-1518005020951-eccb494ad742?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「你們是世上的光。城造在山上是不能隱藏的。」——馬太福音 5:14\n\n一座建在巍峨山頂的溫馨古城，在深沉的夜色中熠熠生輝，將溫柔的光芒灑向八方。這段話激勵我們每個人都成為照亮黑暗、傳遞溫暖、彰顯真善美的火種，讓這世界因我們的善良而多一份光明。",
            descriptionEn = "You are the light of the world. A town built on a hill cannot be hidden. — Matthew 5:14\n\nA beautiful stone city built on a prominent hill shines brightly, acting as a beacon of warmth and guidance in the dark of night. This verse calls us to let our lives reflect divine grace, spreading love and goodness to brighten the world."
        ),
        PresetPuzzle(
            id = "bible_john_3_16",
            titleEn = "God So Loved the World",
            titleCn = "神愛世人",
            imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「神愛世人，甚至將他的獨生子賜給他們，叫一切信他的，不致滅亡，反得永生。」——約翰福音 3:16\n\n這是整部聖經中最核心、最令人動容的愛的宣言。在清晨雲霧繚繞的寧靜山頂上，十字架在晨光中默然佇立。它見證了天父為了拯救世人所付出的無比代價，宣告著永不枯竭、跨越時空的救贖大愛。",
            descriptionEn = "For God so loved the world that He gave His only begotten Son, that whoever believes in Him should not perish but have everlasting life. — John 3:16\n\nStanding in quiet majesty amidst morning mist, this image speaks of the ultimate act of grace. It represents the focal point of divine love, offering a path of redemption, eternal warmth, and infinite hope to all humanity."
        ),
        PresetPuzzle(
            id = "bible_john_14_6",
            titleEn = "The Way, Truth, and Life",
            titleCn = "道路、真理、生命",
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「耶穌說：『我就是道路、真理、生命；若不藉著我，沒有人能到父那裡去。』」——約翰福音 14:6\n\n一條灑滿金色陽光的幽靜森林小徑，穿過高聳入雲的古老森林，直通向遠方充滿希望的燦爛光芒。耶穌的話語是我們在迷茫人生旅途中的絕對指南，祂就是那條引導我們尋得生命終極真諦與永恆歸宿的道路。",
            descriptionEn = "Jesus said to him, \"I am the way, the truth, and the life. No one comes to the Father except through Me.\" — John 14:6\n\nA beautiful, sun-drenched forest trail winds through ancient trees towards a radiant light ahead. It symbolizes the divine journey where Jesus acts as our perfect compass, directing our footsteps in truth, clarity, and everlasting life."
        ),
        PresetPuzzle(
            id = "bible_romans_8_38_39",
            titleEn = "Inseparable Love",
            titleCn = "永不隔絕的愛",
            imageUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「因為我深信...都不能叫我們與神的愛隔絕；這愛是在我們的主基督耶穌裡的。」——羅馬書 8:38-39\n\n不論是浩瀚的宇宙、未知的未來、或是深重的苦難，沒有任何力量能讓我們與天父的愛分開。在璀璨斑斕的星空中，那股溫暖而強大的吸引力擁抱著我們，給予我們心靈最堅實、永不動搖的安全感。",
            descriptionEn = "For I am persuaded... that nothing shall be able to separate us from the love of God which is in Christ Jesus our Lord. — Romans 8:38-39\n\nAcross the vast stellar expanses of the cosmos, this triumphant declaration echoes. No height, no depth, and no hardship can tear us away from the Creator's embrace. We are permanently held by an unbreakable celestial bond of divine affection."
        ),
        PresetPuzzle(
            id = "bible_philippians_4_13",
            titleEn = "Strength Through Christ",
            titleCn = "靠主凡事都能",
            imageUrl = "https://images.unsplash.com/photo-1501555088652-021faa106b9b?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「我靠著那加給我力量的，凡事都能做。」——腓立比書 4:13\n\n當我們攀登人生的險峻峭壁時，狂風常令我們心生畏懼。然而當我們將信心與基督的力量相連，神聖的勇氣便會注滿全身，使我們能以無畏的心克服重重阻礙，昂首挺立於勝利的巔峰，看見最壯麗的風景。",
            descriptionEn = "I can do all things through Christ who strengthens me. — Philippians 4:13\n\nClimbing the rugged and daunting cliffs of life, we are never left to struggle alone. This inspiring verse infuses our hearts with super-natural endurance, empowering us to conquer steep obstacles and stand victoriously atop peaks of glory."
        ),
        PresetPuzzle(
            id = "bible_revelation_21_4",
            titleEn = "No More Pain",
            titleCn = "擦去一切眼淚",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.BIBLE,
            descriptionCn = "「神要擦去他們一切的眼淚；不再有死亡，也不再有悲哀、哭號、疼痛，因為以前的事都過去了。」——啟示錄 21:4\n\n這是一個關於永恆新天新地的輝煌應許。當一切悲傷、淚水與痛苦隨著舊世界的陰霾散去，展現在眼前的是充滿純淨晨光、寧靜海浪與無限溫柔的清晨。所有的創傷都將被神輕輕撫平，迎來永恆的安息與喜樂。",
            descriptionEn = "And God will wipe away every tear from their eyes; there shall be no more death, nor sorrow, nor crying. There shall be no more pain... — Revelation 21:4\n\nThis glorious promise of the New Heaven and New Earth comforts every broken heart. As the shadows of trials and tears fade into a serene, soft sunset of absolute peace, we are ushered into an eternal day of perfect rest, health, and joy."
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
        PresetPuzzle(id = "cat_bengal", titleEn = "Bengal", titleCn = "孟加拉貓", imageRes = R.drawable.cat_bengal_1782924701269, category = PresetPuzzle.Category.ANIMALS),

        // Scenery Category - 9 Famous landmarks from different countries
        PresetPuzzle(
            id = "scenery_japan_fuji",
            titleEn = "Mount Fuji, Japan",
            titleCn = "富士山與忠靈塔 - 日本",
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "日本象徵性景點，白雪皚皚的富士山與朱紅色的五重塔「忠靈塔」在櫻花掩映下交織出極致的和風美景。",
            descriptionEn = "Japan's iconic view featuring the snow-capped Mount Fuji paired with the crimson five-story Chureito Pagoda and delicate cherry blossoms."
        ),
        PresetPuzzle(
            id = "scenery_france_eiffel",
            titleEn = "Eiffel Tower, France",
            titleCn = "艾菲爾鐵塔 - 法國",
            imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "矗立於塞納河畔、戰神廣場上的艾菲爾鐵塔，是巴黎最具代表性的標誌，散發著無與倫比的浪漫與現代設計之美。",
            descriptionEn = "The majestic Eiffel Tower standing proud on the Champ de Mars by the Seine River, a timeless symbol of romance and architectural mastery in Paris."
        ),
        PresetPuzzle(
            id = "scenery_italy_colosseum",
            titleEn = "Colosseum, Italy",
            titleCn = "羅馬競技場 - 義大利",
            imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "這座巨大的古羅馬圓形競技場，是帝國歷史與輝煌的見證。古老斑駁的石牆在夕陽照耀下散發出史詩般的力量。",
            descriptionEn = "The magnificent Roman Colosseum, an ancient amphitheater standing as a testament to the grand history and eternal strength of the Roman Empire."
        ),
        PresetPuzzle(
            id = "scenery_usa_canyon",
            titleEn = "Grand Canyon, USA",
            titleCn = "大峽谷 - 美國",
            imageUrl = "https://images.unsplash.com/photo-1615551043360-33de8b5f410c?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "大自然億萬年鬼斧神工的傑作。科羅拉多河切削出的深邃峽谷，展現出無邊無際的宏偉與令人屏息的壯麗色彩。",
            descriptionEn = "A breathtaking natural wonder carved by the Colorado River over millions of years, showcasing majestic layers of colorful ancient rock."
        ),
        PresetPuzzle(
            id = "scenery_india_taj",
            titleEn = "Taj Mahal, India",
            titleCn = "泰姬瑪哈陵 - 印度",
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "莫臥兒帝國皇帝沙賈漢為紀念摯愛妻子而建的白色大理石陵墓，對稱美學的巔峰，被譽為「永恆面頰上的一滴眼淚」。",
            descriptionEn = "The sublime white marble mausoleum built by Mughal Emperor Shah Jahan in memory of his favorite wife, representing the peak of symmetrical architectural beauty."
        ),
        PresetPuzzle(
            id = "scenery_australia_opera",
            titleEn = "Sydney Opera House, Australia",
            titleCn = "雪梨歌劇院 - 澳洲",
            imageUrl = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "坐落於雪梨港灣，宛如風帆或白色貝殼群的獨特外觀，是20世紀最具震撼力與創意的現代建築傑作之一。",
            descriptionEn = "Situated on Bennelong Point in Sydney Harbour, its distinctive sail-like shells make it one of the 20th century's most iconic and creative landmarks."
        ),
        PresetPuzzle(
            id = "scenery_uk_bridge",
            titleEn = "Tower Bridge, UK",
            titleCn = "倫敦塔橋 - 英國",
            imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "橫跨泰晤士河的維多利亞風格開合橋，莊嚴雄偉的雙塔結構，是倫敦古典與工業魅力交融的永恆地標。",
            descriptionEn = "The magnificent Victorian-era suspension bridge crossing the River Thames, featuring grand gothic towers that represent classical British heritage."
        ),
        PresetPuzzle(
            id = "scenery_canada_moraine",
            titleEn = "Moraine Lake, Canada",
            titleCn = "夢蓮湖 - 加拿大",
            imageUrl = "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "班夫國家公園內的耀眼明珠。冰川融水折射出令人驚嘆的翡翠藍色，背靠十峰山，勾勒出如天堂般寧靜的明信片絕景。",
            descriptionEn = "Banff National Park's stunning glacier-fed lake, renowned for its brilliant turquoise hue reflecting the grand Valley of the Ten Peaks."
        ),
        PresetPuzzle(
            id = "scenery_egypt_pyramids",
            titleEn = "Pyramids of Giza, Egypt",
            titleCn = "吉薩金字塔 - 埃及",
            imageUrl = "https://images.unsplash.com/photo-1539650116574-8efeb43e2750?auto=format&fit=crop&w=800&q=80",
            category = PresetPuzzle.Category.SCENERY,
            descriptionCn = "古代世界七大奇蹟中唯一存世的古老遺蹟。在廣袤的金色沙漠中拔地而起，承載著數千年古埃及神祕而偉大的智慧。",
            descriptionEn = "The oldest and only surviving wonder of the ancient world, standing majestically in the golden desert sands, steeped in timeless mystery and historic awe."
        )
    )

    suspend fun loadBitmap(context: Context, source: Any, width: Int = 600, height: Int = 600): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            when (source) {
                is Int -> {
                    BitmapFactory.decodeResource(context.resources, source)?.asImageBitmap()
                }
                is String -> {
                    if (source.startsWith("http://") || source.startsWith("https://")) {
                        // Implement permanent local caching for online/Firebase images
                        val hashName = "cached_puzzle_${source.hashCode()}.jpg"
                        val cacheFile = File(context.filesDir, hashName)
                        if (cacheFile.exists()) {
                            val cachedBmp = BitmapFactory.decodeFile(cacheFile.absolutePath)
                            if (cachedBmp != null) {
                                return@withContext cachedBmp.asImageBitmap()
                            }
                        }

                        val loader = context.imageLoader
                        val request = coil.request.ImageRequest.Builder(context)
                            .data(source)
                            .allowHardware(false)
                            .build()
                        val result = loader.execute(request)
                        if (result is coil.request.SuccessResult) {
                            val drawable = result.drawable
                            val bmp = if (drawable is android.graphics.drawable.BitmapDrawable) {
                                drawable.bitmap
                            } else {
                                val b = Bitmap.createBitmap(
                                    drawable.intrinsicWidth.coerceAtLeast(1),
                                    drawable.intrinsicHeight.coerceAtLeast(1),
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = android.graphics.Canvas(b)
                                drawable.setBounds(0, 0, canvas.width, canvas.height)
                                drawable.draw(canvas)
                                b
                            }
                            // Save to local storage for instant offline loading
                            try {
                                FileOutputStream(cacheFile).use { out ->
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            bmp.asImageBitmap()
                        } else {
                            null
                        }
                    } else {
                        BitmapFactory.decodeFile(source)?.asImageBitmap()
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
