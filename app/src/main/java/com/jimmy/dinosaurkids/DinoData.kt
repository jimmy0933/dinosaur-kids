package com.jimmy.dinosaurkids

import androidx.annotation.DrawableRes

/** Rounded estimates for a preschool learning game, not measurements of individuals. */
enum class Diet { PLANTS, MEAT, BOTH }

data class Dino(
    val id: String,
    val zh: String,
    val en: String,
    @DrawableRes val art: Int,
    val diet: Diet,
    val height: String,
    val mass: String,
    val heightEn: String,
    val massEn: String,
    val factZh: String,
    val factEn: String,
    val actionZh: String,
    val actionEn: String,
)

val dinosaurs = listOf(
    Dino("trex", "暴龍", "T. rex", R.drawable.dino_trex, Diet.MEAT,
        "約 4 公尺", "約 8 噸", "About 4 m", "About 8 t", "暴龍有強大的牙齒和大大的腳，走路咚咚咚！", "T. rex has huge teeth and strong feet. Stomp, stomp!", "大吼一聲", "Big roar"),
    Dino("triceratops", "三角龍", "Triceratops", R.drawable.dino_triceratops, Diet.PLANTS,
        "約 3 公尺", "約 7 噸", "About 3 m", "About 7 t", "三角龍有三根角，還有像盾牌一樣的大頭飾。", "Triceratops has three horns and a big frill.", "亮出角角", "Show your horns"),
    Dino("stegosaurus", "劍龍", "Stegosaurus", R.drawable.dino_stegosaurus, Diet.PLANTS,
        "約 4 公尺", "約 4 噸", "About 4 m", "About 4 t", "劍龍的背上排著大大的骨板，尾巴還有尖刺。", "Stegosaurus has big plates and spikes on its tail.", "搖尾巴", "Wiggle tail"),
    Dino("brachiosaurus", "腕龍", "Brachiosaurus", R.drawable.dino_brachiosaurus, Diet.PLANTS,
        "約 12 公尺", "約 35 噸", "About 12 m", "About 35 t", "腕龍脖子長長，可以吃到高高樹上的葉子。", "Brachiosaurus has a long neck to eat tall leaves.", "伸長脖子", "Stretch your neck"),
    Dino("ankylosaurus", "甲龍", "Ankylosaurus", R.drawable.dino_ankylosaurus, Diet.PLANTS,
        "約 2 公尺", "約 6 噸", "About 2 m", "About 6 t", "甲龍的背上有護甲，尾巴像大棒槌。", "Ankylosaurus wears armor and has a club tail.", "甩尾巴", "Swing your tail"),
    Dino("velociraptor", "伶盜龍", "Velociraptor", R.drawable.dino_velociraptor, Diet.MEAT,
        "約 0.5 公尺", "約 15 公斤", "About 0.5 m", "About 15 kg", "伶盜龍其實不大，身上還可能有羽毛。", "Velociraptor was small and probably had feathers.", "輕快跑跑", "Quick steps"),
    Dino("spinosaurus", "棘龍", "Spinosaurus", R.drawable.dino_spinosaurus, Diet.MEAT,
        "約 5 公尺", "約 7 噸", "About 5 m", "About 7 t", "棘龍背上有大大的帆，喜歡在水邊找魚。", "Spinosaurus has a tall sail and hunted fish.", "找魚吃", "Find a fish"),
    Dino("parasaurolophus", "副櫛龍", "Parasaurolophus", R.drawable.dino_parasaurolophus, Diet.PLANTS,
        "約 3 公尺", "約 3 噸", "About 3 m", "About 3 t", "副櫛龍頭上有長長的頭冠，可能可以發出聲音。", "Parasaurolophus had a long crest that may have made sounds.", "吹號角", "Toot toot"),
    Dino("allosaurus", "異特龍", "Allosaurus", R.drawable.dino_allosaurus, Diet.MEAT,
        "約 3 公尺", "約 2 噸", "About 3 m", "About 2 t", "異特龍是侏羅紀的大型肉食恐龍。", "Allosaurus was a big meat-eating Jurassic dinosaur.", "張開嘴巴", "Open wide"),
    Dino("diplodocus", "梁龍", "Diplodocus", R.drawable.dino_diplodocus, Diet.PLANTS,
        "約 5 公尺", "約 12 噸", "About 5 m", "About 12 t", "梁龍脖子長，尾巴更長，走起路來慢慢的。", "Diplodocus had a long neck and an even longer tail.", "慢慢走", "Walk slowly"),
    Dino("pachycephalosaurus", "厚頭龍", "Pachycephalosaurus", R.drawable.dino_pachycephalosaurus, Diet.PLANTS,
        "約 2 公尺", "約 450 公斤", "About 2 m", "About 450 kg", "厚頭龍頭頂圓圓、厚厚，就像戴了安全帽。", "Pachycephalosaurus has a thick, rounded skull.", "摸摸頭頂", "Pat your head"),
    Dino("therizinosaurus", "鐮刀龍", "Therizinosaurus", R.drawable.dino_therizinosaurus, Diet.PLANTS,
        "約 5 公尺", "約 4 噸", "About 5 m", "About 4 t", "鐮刀龍有很長的爪子，可能用來勾植物。", "Therizinosaurus had long claws, perhaps to pull plants closer.", "伸出長爪", "Show your claws"),
    Dino("carnotaurus", "食肉牛龍", "Carnotaurus", R.drawable.dino_carnotaurus, Diet.MEAT,
        "約 3 公尺", "約 2 噸", "About 3 m", "About 2 t", "食肉牛龍眼睛上方有兩個短短的角。", "Carnotaurus had two short horns above its eyes.", "小牛角", "Little horns"),
    Dino("iguanodon", "禽龍", "Iguanodon", R.drawable.dino_iguanodon, Diet.PLANTS,
        "約 3 公尺", "約 3 噸", "About 3 m", "About 3 t", "禽龍前腳的大拇指上，長著特別的尖刺。", "Iguanodon had a pointed thumb spike.", "比大拇指", "Thumbs up"),
    Dino("maiasaura", "慈母龍", "Maiasaura", R.drawable.dino_maiasaura, Diet.PLANTS,
        "約 3 公尺", "約 2 噸", "About 3 m", "About 2 t", "慈母龍會在巢裡照顧小寶寶，名字有好媽媽的意思。", "Maiasaura cared for its babies in nests.", "照顧寶寶", "Care for babies"),
    Dino("oviraptor", "竊蛋龍", "Oviraptor", R.drawable.dino_oviraptor, Diet.BOTH,
        "約 1 公尺", "約 35 公斤", "About 1 m", "About 35 kg", "竊蛋龍有像鳥的嘴巴，牠可能還會保護自己的蛋。", "Oviraptor had a birdlike beak and may have guarded its eggs.", "護住蛋寶寶", "Protect eggs"),
    Dino("dilophosaurus", "雙冠龍", "Dilophosaurus", R.drawable.dino_dilophosaurus, Diet.MEAT,
        "約 2 公尺", "約 350 公斤", "About 2 m", "About 350 kg", "雙冠龍頭頂有兩片漂亮的骨冠。", "Dilophosaurus has two thin crests on its head.", "頂起頭冠", "Show your crest"),
    Dino("apatosaurus", "迷惑龍", "Apatosaurus", R.drawable.dino_apatosaurus, Diet.PLANTS,
        "約 5 公尺", "約 20 噸", "About 5 m", "About 20 t", "迷惑龍身體巨大，四隻腳像粗壯的柱子。", "Apatosaurus was enormous, with four pillar-like legs.", "大腳走路", "Big footsteps")
)

fun Diet.emoji() = when (this) { Diet.PLANTS -> "🌿"; Diet.MEAT -> "🍖"; Diet.BOTH -> "🥚" }
fun Diet.label(en: Boolean) = when (this) {
    Diet.PLANTS -> if (en) "Plant eater" else "吃植物"
    Diet.MEAT -> if (en) "Meat eater" else "吃肉肉"
    Diet.BOTH -> if (en) "Eats many foods" else "什麼都吃"
}
fun Dino.name(en: Boolean) = if (en) this.en else zh
fun Dino.fact(en: Boolean) = if (en) factEn else factZh
fun Dino.action(en: Boolean) = if (en) actionEn else actionZh
