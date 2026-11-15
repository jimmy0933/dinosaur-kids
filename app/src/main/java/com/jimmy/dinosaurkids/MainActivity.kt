package com.jimmy.dinosaurkids

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.random.Random

private val Pine = Color(0xFF184F4C)
private val Sea = Color(0xFF2C7771)
private val Mint = Color(0xFFB7E0C8)
private val Cream = Color(0xFFFFF8E9)
private val Gold = Color(0xFFFFCD72)
private val Tangerine = Color(0xFFF39370)
private val Plum = Color(0xFF835F8F)
private val Ink = Color(0xFF214E4B)

private enum class Page { HOME, DEX, DETAIL, EXPLORE, FEED, DIG, QUIZ, ALBUM, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContent { MaterialTheme { DinoIslandApp() } }
    }
}

private class VoiceGuide(context: android.content.Context) {
    var ready by mutableStateOf(false)
        private set
    private var tts: TextToSpeech? = null
    init {
        tts = TextToSpeech(context.applicationContext) { ready = it == TextToSpeech.SUCCESS }
    }
    fun speak(message: String, english: Boolean, rate: Float) {
        if (!ready) return
        val engine = tts ?: return
        engine.language = if (english) Locale.US else Locale.TAIWAN
        engine.setSpeechRate(rate)
        engine.speak(message, TextToSpeech.QUEUE_FLUSH, null, "guide-${System.nanoTime()}")
    }
    fun stop() { tts?.stop() }
    fun shutdown() { tts?.shutdown(); tts = null }
}

@Composable
private fun DinoIslandApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("island_progress", Activity.MODE_PRIVATE) }
    val voiceGuide = remember { VoiceGuide(context) }
    DisposableEffect(voiceGuide) { onDispose { voiceGuide.shutdown() } }

    var page by remember { mutableStateOf(Page.HOME) }
    var chosen by remember { mutableStateOf(dinosaurs.first()) }
    var discovered by remember { mutableStateOf(prefs.getStringSet("discovered", emptySet())!!.toSet()) }
    var english by remember { mutableStateOf(prefs.getBoolean("english", false)) }
    var voiceOn by remember { mutableStateOf(prefs.getBoolean("voice", true)) }
    var voiceRate by remember { mutableFloatStateOf(prefs.getFloat("rate", 0.87f)) }

    fun collect(dino: Dino) {
        if (dino.id !in discovered) {
            discovered = discovered + dino.id
            prefs.edit().putStringSet("discovered", discovered.toSet()).apply()
        }
    }
    fun say(zh: String, en: String) {
        if (voiceOn) voiceGuide.speak(if (english) en else zh, english, voiceRate)
    }
    fun go(newPage: Page) {
        voiceGuide.stop()
        page = newPage
    }

    BackHandler(page != Page.HOME) {
        go(if (page == Page.DETAIL) Page.DEX else Page.HOME)
    }

    LaunchedEffect(page, chosen.id, english, voiceOn, voiceGuide.ready) {
        if (voiceOn && voiceGuide.ready && page == Page.DETAIL) {
            voiceGuide.speak(
                if (english) "${chosen.en}. ${chosen.factEn}" else "${chosen.zh}。${chosen.factZh}",
                english, voiceRate
            )
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFE4EEDB))) {
        Image(
            painter = androidx.compose.ui.res.painterResource(R.drawable.jungle_landscape),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.fillMaxSize().background(Color(0x27134C47)))
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 9.dp)) {
            IslandTopBar(
                title = when(page) {
                    Page.HOME -> if (english) "Dinosaur Adventure Island" else "恐龍探險島"
                    Page.DEX -> if (english) "Dinosaur Encyclopedia" else "恐龍圖鑑"
                    Page.DETAIL -> chosen.name(english)
                    Page.EXPLORE -> if (english) "Jungle Expedition" else "森林探險"
                    Page.FEED -> if (english) "Feeding Time" else "恐龍餵食"
                    Page.DIG -> if (english) "Fossil Discovery" else "挖挖化石"
                    Page.QUIZ -> if (english) "Who's That Dino?" else "猜猜恐龍"
                    Page.ALBUM -> if (english) "My Sticker Album" else "恐龍收藏冊"
                    Page.SETTINGS -> if (english) "Settings" else "遊戲設定"
                },
                page = page,
                stars = discovered.size,
                onHome = { go(Page.HOME) },
                onSettings = { go(Page.SETTINGS) },
                onHear = {
                    if (page == Page.DETAIL) say("${chosen.zh}。${chosen.factZh}", "${chosen.en}. ${chosen.factEn}")
                    else say("歡迎來到恐龍探險島！一起探索恐龍吧！", "Welcome to Dinosaur Adventure Island!")
                }
            )
            Spacer(Modifier.height(9.dp))
            Box(Modifier.weight(1f)) {
                when (page) {
                    Page.HOME -> HomeScreen(english, discovered.size, onOpen = ::go, onFeature = {
                        chosen = it
                        go(Page.DETAIL)
                    })
                    Page.DEX -> DexScreen(english, discovered, onChoose = {
                        chosen = it
                        go(Page.DETAIL)
                    })
                    Page.DETAIL -> DetailScreen(chosen, english, chosen.id in discovered,
                        onSay = { say("${chosen.zh}。${chosen.factZh}", "${chosen.en}. ${chosen.factEn}") },
                        onAction = { say(chosen.actionZh, chosen.actionEn) },
                        onExplore = { go(Page.EXPLORE) })
                    Page.EXPLORE -> ExploreScreen(english, onFind = {
                        collect(it)
                        say("哇！找到${it.zh}了！得到一張恐龍貼紙！", "Wow! You found ${it.en}! You earned a sticker!")
                    }, onHint = { _ -> say("看看樹叢裡，誰躲起來了？", "Who's hiding in the jungle?") })
                    Page.FEED -> FeedScreen(english, onPrompt = { say("這隻恐龍喜歡吃什麼呢？選一個食物吧！", "What does this dinosaur like to eat? Pick one!") }, onFeed = { d, correct ->
                        if (correct) { collect(d); say("答對了！${d.zh}吃得好開心！", "Great job! ${d.en} loves the food!") }
                        else say("再試試看，看看恐龍喜歡吃什麼？", "Try again. What does this dinosaur eat?")
                    })
                    Page.DIG -> DigScreen(english, onPrompt = { say("挖挖看，沙子下面藏了什麼？", "Dig away! What is hidden under the sand?") }, onFinish = {
                        collect(it)
                        say("挖到寶藏了！是${it.zh}！", "You found a fossil! It's ${it.en}!")
                    })
                    Page.QUIZ -> QuizScreen(english, voiceGuide.ready, onSay = { d ->
                        say("請找出${d.zh}！", "Can you find ${d.en}?")
                    }, onGuess = { d, correct ->
                        if (correct) { collect(d); say("好棒！你找到${d.zh}了！", "Wonderful! You found ${d.en}!") }
                        else say("再試一次！你一定找得到。", "Try again! You can find it.")
                    })
                    Page.ALBUM -> AlbumScreen(english, discovered, onChoose = {
                        chosen = it
                        go(Page.DETAIL)
                    })
                    Page.SETTINGS -> SettingsScreen(english, voiceOn, voiceRate,
                        onLanguage = { english = it; prefs.edit().putBoolean("english", it).apply() },
                        onVoice = { voiceOn = it; prefs.edit().putBoolean("voice", it).apply(); if (!it) voiceGuide.stop() },
                        onRate = { voiceRate = it; prefs.edit().putFloat("rate", it).apply() },
                        onTry = { say("恐龍探險島，歡迎你！", "Welcome to Dinosaur Adventure Island!") })
                }
            }
        }
    }
}

@Composable
private fun IslandTopBar(title: String, page: Page, stars: Int, onHome: () -> Unit, onSettings: () -> Unit, onHear: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(55.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CircleAction(if (page == Page.HOME) "🦕" else "🏠", if (page == Page.HOME) "Island" else "Home", onHome)
        Surface(shape = RoundedCornerShape(24.dp), color = Cream.copy(alpha = .96f), shadowElevation = 3.dp,
            modifier = Modifier.weight(1f).height(50.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 17.dp)) {
                Text(title, color = Pine, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text("⭐ $stars/${dinosaurs.size}", color = Sea, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
        CircleAction("🔊", "Read aloud", onHear)
        CircleAction("⚙️", "Settings", onSettings)
    }
}

@Composable
private fun CircleAction(emoji: String, description: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.size(51.dp).clickable(onClick = onClick), shape = CircleShape, color = Cream,
        shadowElevation = 3.dp, border = BorderStroke(1.dp, Color.White.copy(alpha = .9f))) {
        Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 26.sp, textAlign = TextAlign.Center) }
    }
}

@Composable
private fun Panel(modifier: Modifier = Modifier, color: Color = Cream.copy(alpha = .96f), content: @Composable BoxScope.() -> Unit) {
    Box(modifier.shadow(6.dp, RoundedCornerShape(27.dp)).clip(RoundedCornerShape(27.dp))
        .background(color).border(1.5.dp, Color.White.copy(alpha = .75f), RoundedCornerShape(27.dp)), content = content)
}

@Composable
private fun DinoStage(dino: Dino, modifier: Modifier = Modifier, background: Color = Color(0xFFDFEFCB), big: Boolean = false) {
    Box(modifier.clip(RoundedCornerShape(if (big) 26.dp else 18.dp))
        .background(Brush.verticalGradient(listOf(background, Color(0xFFE7F3DC), Color(0xFFC8DEC6))))) {
        Text("✦", modifier = Modifier.align(Alignment.TopStart).padding(12.dp), color = Color.White.copy(alpha = .85f), fontSize = 25.sp)
        Text("✧", modifier = Modifier.align(Alignment.TopEnd).padding(12.dp), color = Color.White.copy(alpha = .85f), fontSize = 25.sp)
        Image(
            painter = androidx.compose.ui.res.painterResource(dino.art),
            contentDescription = dino.zh,
            modifier = Modifier.fillMaxSize().padding(if (big) 0.dp else 4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun HomeScreen(english: Boolean, stars: Int, onOpen: (Page) -> Unit, onFeature: (Dino) -> Unit) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        Panel(Modifier.weight(.43f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (english) "LET'S EXPLORE!" else "今天要找哪隻恐龍？", fontWeight = FontWeight.ExtraBold, color = Pine, fontSize = 23.sp)
                Spacer(Modifier.height(5.dp))
                val dino = dinosaurs[0]
                Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(19.dp)).clickable { onFeature(dino) }) {
                    DinoStage(dino, Modifier.fillMaxSize(), background = Color(0xFFFFE6BB), big = true)
                    Text("👋", fontSize = 28.sp, modifier = Modifier.align(Alignment.TopEnd).padding(9.dp))
                }
                Spacer(Modifier.height(6.dp))
                Text(if (english) "Meet ${dino.en}!" else "嗨！我是暴龍！", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(.57f).fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { FeatureTile("📖", if (english) "Dino Book" else "恐龍圖鑑", Color(0xFFE9F3D5), onClick = { onOpen(Page.DEX) }) }
            item { FeatureTile("🌴", if (english) "Explore" else "森林探險", Color(0xFFFFE8BB), onClick = { onOpen(Page.EXPLORE) }) }
            item { FeatureTile("🥬", if (english) "Feed Dino" else "餵食恐龍", Color(0xFFFFD9CB), onClick = { onOpen(Page.FEED) }) }
            item { FeatureTile("🦴", if (english) "Dig Fossils" else "挖挖化石", Color(0xFFE3D5F0), onClick = { onOpen(Page.DIG) }) }
            item { FeatureTile("❓", if (english) "Dino Quiz" else "猜猜恐龍", Color(0xFFCFEAE7), onClick = { onOpen(Page.QUIZ) }) }
            item { FeatureTile("⭐", if (english) "My Album" else "收藏貼紙", Color(0xFFFBE7B9), onClick = { onOpen(Page.ALBUM) }) }
        }
    }
}

@Composable
private fun FeatureTile(emoji: String, label: String, color: Color, onClick: () -> Unit) {
    Panel(Modifier.fillMaxWidth().height(129.dp).clickable(onClick = onClick), color) {
        Column(Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 46.sp, lineHeight = 51.sp)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Ink, textAlign = TextAlign.Center,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.height(42.dp).clickable(onClick = onClick), shape = RoundedCornerShape(18.dp),
        color = if (selected) Pine else Cream, shadowElevation = 2.dp) {
        Box(Modifier.padding(horizontal = 18.dp), contentAlignment = Alignment.Center) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Ink)
        }
    }
}

@Composable
private fun DexScreen(english: Boolean, discovered: Set<String>, onChoose: (Dino) -> Unit) {
    var filter by remember { mutableStateOf<Diet?>(null) }
    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(bottom = 9.dp)) {
            FilterChip(if (english) "🦖 All ${dinosaurs.size}" else "🦖 全部 ${dinosaurs.size}", filter == null) { filter = null }
            FilterChip(if (english) "🌿 Plants" else "🌿 草食", filter == Diet.PLANTS) { filter = Diet.PLANTS }
            FilterChip(if (english) "🍖 Meat" else "🍖 肉食", filter == Diet.MEAT) { filter = Diet.MEAT }
            FilterChip(if (english) "🥚 Both" else "🥚 雜食", filter == Diet.BOTH) { filter = Diet.BOTH }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dinosaurs.filter { filter == null || it.diet == filter }, key = { it.id }) { dino ->
                DinoTile(dino, english, dino.id in discovered, onClick = { onChoose(dino) })
            }
        }
    }
}

@Composable
private fun DinoTile(dino: Dino, english: Boolean, collected: Boolean, onClick: () -> Unit) {
    Panel(Modifier.fillMaxWidth().height(157.dp).clickable(onClick = onClick)) {
        Column(Modifier.fillMaxSize().padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                DinoStage(dino, Modifier.fillMaxSize(), Color(0xFFE4EFDE))
                Text(dino.diet.emoji(), fontSize = 17.sp, modifier = Modifier.align(Alignment.TopStart).padding(5.dp))
                if (collected) Text("⭐", fontSize = 16.sp, modifier = Modifier.align(Alignment.TopEnd).padding(5.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(dino.name(english), color = Pine, fontWeight = FontWeight.ExtraBold,
                fontSize = if (english) 15.sp else 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DetailScreen(dino: Dino, english: Boolean, collected: Boolean, onSay: () -> Unit,
    onAction: () -> Unit, onExplore: () -> Unit) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Panel(Modifier.weight(.54f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(dino.name(english), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Pine)
                    Text(if (collected) "⭐" else "🦕", fontSize = 26.sp)
                }
                DinoStage(dino, Modifier.fillMaxWidth().weight(1f), Color(0xFFF9E3BA), big = true)
            }
        }
        Column(Modifier.weight(.46f).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                FactPill(dino.diet.emoji(), dino.diet.label(english), Modifier.weight(1f))
                FactPill("📏", if (english) dino.heightEn else dino.height, Modifier.weight(1f))
                FactPill("⚖️", if (english) dino.massEn else dino.mass, Modifier.weight(1f))
            }
            Panel(Modifier.fillMaxWidth(), Color(0xFFFCF3DA)) {
                Column(Modifier.padding(14.dp)) {
                    Text("💡 ${if (english) "DID YOU KNOW?" else "恐龍小秘密"}", fontSize = 17.sp, color = Sea, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(dino.fact(english), fontSize = 19.sp, lineHeight = 27.sp, color = Ink, fontWeight = FontWeight.Medium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                BigAction("🔊", if (english) "Listen" else "聽故事", Sea, Modifier.weight(1f), onSay)
                BigAction("✨", dino.action(english), Tangerine, Modifier.weight(1f), onAction)
            }
            BigAction("🌴", if (english) "Let's find dinosaurs" else "一起去探險", Pine, Modifier.fillMaxWidth(), onExplore)
            Text(if (english) "Sizes are approximate." else "身高和體重是約略估計值。", fontSize = 12.sp,
                color = Cream, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun FactPill(emoji: String, text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.height(67.dp), shape = RoundedCornerShape(20.dp), color = Cream, shadowElevation = 3.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 21.sp)
            Text(text, color = Ink, fontSize = 13.sp, maxLines = 1, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun BigAction(emoji: String, text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.height(53.dp).clickable(onClick = onClick), shape = RoundedCornerShape(18.dp),
        color = color, shadowElevation = 3.dp) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 23.sp)
            Spacer(Modifier.width(6.dp))
            Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ExploreScreen(english: Boolean, onFind: (Dino) -> Unit, onHint: (Dino) -> Unit) {
    var index by remember { mutableIntStateOf(Random.nextInt(dinosaurs.size)) }
    var found by remember { mutableStateOf(false) }
    val dino = dinosaurs[index]
    LaunchedEffect(index) { onHint(dino) }
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Panel(Modifier.weight(.67f).fillMaxHeight(), Color(0xFFE6F2E0)) {
            Box(Modifier.fillMaxSize().clickable {
                if (!found) { found = true; onFind(dino) }
            }) {
                DinoStage(dino, Modifier.fillMaxSize().padding(13.dp), Color(0xFFCDE9C7), big = true)
                if (!found) {
                    Box(Modifier.fillMaxSize().background(Color(0xDD4C8D75)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌿  🌴  🌿", fontSize = 64.sp)
                            Text("🐾 🐾 🐾", fontSize = 47.sp)
                            Text(if (english) "TAP TO FIND!" else "點一下，找恐龍！", fontSize = 24.sp,
                                color = Color.White, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                AnimatedVisibility(visible = found, enter = fadeIn(tween(450)) + scaleIn(), modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("🌟 🌟", modifier = Modifier.padding(20.dp), fontSize = 32.sp)
                }
            }
        }
        Panel(Modifier.weight(.33f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text(if (found) "🎉" else "🔎", fontSize = 45.sp)
                Text(if (found) dino.name(english) else if (english) "Who is hiding?" else "誰躲在樹叢裡？",
                    fontSize = 23.sp, color = Pine, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text(if (found) (if (english) "You earned a sticker!" else "得到一張恐龍貼紙！")
                     else (if (english) "Follow the footsteps" else "跟著腳印找找看"),
                    fontSize = 15.sp, color = Sea, textAlign = TextAlign.Center)
                BigAction(if (found) "➡️" else "👀", if (found) (if (english) "Next dino" else "繼續找")
                         else (if (english) "Look!" else "找到了嗎？"),
                    if (found) Sea else Tangerine, Modifier.fillMaxWidth()) {
                    if (found) {
                        index = (index + Random.nextInt(1, dinosaurs.size)) % dinosaurs.size
                        found = false
                    } else { found = true; onFind(dino) }
                }
            }
        }
    }
}

@Composable
private fun FeedScreen(english: Boolean, onPrompt: (Dino) -> Unit, onFeed: (Dino, Boolean) -> Unit) {
    var index by remember { mutableIntStateOf(Random.nextInt(dinosaurs.size)) }
    var solved by remember { mutableStateOf(false) }
    val dino = dinosaurs[index]
    LaunchedEffect(index) { onPrompt(dino) }
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Panel(Modifier.weight(.52f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(dino.name(english), color = Pine, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                DinoStage(dino, Modifier.weight(1f).fillMaxWidth(), Color(0xFFE6EDC7), big = true)
            }
        }
        Panel(Modifier.weight(.48f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween) {
                Text(if (solved) "💚" else "🍽️", fontSize = 30.sp)
                Text(if (solved) (if (english) "YUMMY!" else "吃飽飽！")
                     else (if (english) "What should we feed it?" else "牠喜歡吃什麼？"),
                    color = Pine, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                if (!solved) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FoodChoice("🥬", if (english) "Leaves" else "葉子", Modifier.weight(1f)) {
                            val correct = dino.diet != Diet.MEAT
                            onFeed(dino, correct)
                            if (correct) solved = true
                        }
                        FoodChoice("🍖", if (english) "Meat" else "肉肉", Modifier.weight(1f)) {
                            val correct = dino.diet != Diet.PLANTS
                            onFeed(dino, correct)
                            if (correct) solved = true
                        }
                    }
                } else {
                    Text("🎉 ⭐ 🎉", fontSize = 38.sp)
                    BigAction("➡️", if (english) "Feed another" else "餵下一隻", Sea, Modifier.fillMaxWidth()) {
                        index = (index + Random.nextInt(1, dinosaurs.size)) % dinosaurs.size
                        solved = false
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodChoice(icon: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Panel(modifier.height(118.dp).clickable(onClick = onClick), Color(0xFFFFE9CA)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 54.sp)
            Text(label, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun DigScreen(english: Boolean, onPrompt: (Dino) -> Unit, onFinish: (Dino) -> Unit) {
    var index by remember { mutableIntStateOf(Random.nextInt(dinosaurs.size)) }
    var taps by remember { mutableIntStateOf(0) }
    val dino = dinosaurs[index]
    val finished = taps >= 5
    LaunchedEffect(index) { onPrompt(dino) }
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Panel(Modifier.weight(.68f).fillMaxHeight(), Color(0xFFF9E5B7)) {
            Box(Modifier.fillMaxSize().clickable {
                if (!finished) { taps += 1; if (taps == 5) onFinish(dino) }
            }, contentAlignment = Alignment.Center) {
                DinoStage(dino, Modifier.fillMaxSize().padding(10.dp), Color(0xFFFFE9C7), big = true)
                if (!finished) {
                    Box(Modifier.fillMaxSize().background(Color(0xFFCB9B61).copy(alpha = (1f - taps * .17f).coerceIn(.05f, 1f))),
                        contentAlignment = Alignment.Center) {
                        Text(if (taps == 0) "🦴" else "🪨", fontSize = (83 - taps * 9).sp)
                    }
                    Text("⛏️", fontSize = 52.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp))
                } else Text("✨ 🦕 ✨", fontSize = 28.sp, modifier = Modifier.align(Alignment.TopCenter))
            }
        }
        Panel(Modifier.weight(.32f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text("⛏️", fontSize = 45.sp)
                Text(if (finished) dino.name(english) else if (english) "Dig for fossils!" else "挖出恐龍化石！",
                    fontSize = 22.sp, color = Pine, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text(if (finished) (if (english) "Awesome discovery!" else "發現寶藏了！")
                     else "${"●".repeat(taps)}${"○".repeat(5-taps)}",
                    fontSize = 23.sp, color = Sea, textAlign = TextAlign.Center)
                BigAction(if (finished) "➡️" else "⛏️", if (finished) (if (english) "Dig again" else "再挖一次")
                         else (if (english) "Tap to dig" else "挖一下"),
                    if (finished) Sea else Tangerine, Modifier.fillMaxWidth()) {
                    if (finished) {
                        index = (index + Random.nextInt(1, dinosaurs.size)) % dinosaurs.size
                        taps = 0
                    } else { taps += 1; if (taps == 5) onFinish(dino) }
                }
            }
        }
    }
}

@Composable
private fun QuizScreen(english: Boolean, voiceReady: Boolean, onSay: (Dino) -> Unit, onGuess: (Dino, Boolean) -> Unit) {
    var targetIndex by remember { mutableIntStateOf(Random.nextInt(dinosaurs.size)) }
    var solved by remember { mutableStateOf(false) }
    val target = dinosaurs[targetIndex]
    val choices = remember(targetIndex) { (listOf(target) + dinosaurs.filter { it.id != target.id }.shuffled().take(2)).shuffled() }
    LaunchedEffect(targetIndex, voiceReady, english) { if (voiceReady) onSay(target) }
    Column(Modifier.fillMaxSize()) {
        Panel(Modifier.fillMaxWidth().height(62.dp).clickable { onSay(target) }, Color(0xFFFFF3D9)) {
            Row(Modifier.fillMaxSize().padding(horizontal = 23.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("🔊 ${if (english) "Find ${target.en}!" else "請找出「${target.zh}」！"}",
                    fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Pine)
                Text(if (solved) "🎉 ⭐" else "👂", fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().weight(1f)) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            choices.forEach { option ->
                Panel(Modifier.weight(1f).fillMaxHeight().clickable {
                    if (!solved) {
                        val correct = option.id == target.id
                        onGuess(target, correct)
                        if (correct) solved = true
                    }
                }, if (solved && option.id == target.id) Color(0xFFDDF6DB) else Cream) {
                    Column(Modifier.fillMaxSize().padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        DinoStage(option, Modifier.fillMaxWidth().weight(1f), Color(0xFFE6EECF))
                        Spacer(Modifier.height(4.dp))
                        Text(option.name(english), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Pine, maxLines = 1)
                        if (solved && option.id == target.id) Text("⭐", fontSize = 16.sp)
                    }
                }
            }
        }
        if (solved) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)) {
                BigAction("➡️", if (english) "Next round" else "下一題", Sea, Modifier.width(230.dp)) {
                    targetIndex = (targetIndex + Random.nextInt(1, dinosaurs.size)) % dinosaurs.size
                    solved = false
                }
            }
        }
        }
    }
}

@Composable
private fun AlbumScreen(english: Boolean, discovered: Set<String>, onChoose: (Dino) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Surface(shape = RoundedCornerShape(19.dp), color = Cream, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text(if (english) "⭐ ${discovered.size} stickers collected! Explore, feed, dig and play to find more."
                     else "⭐ 收集到 ${discovered.size} 張貼紙！去探險、餵食、挖化石，找到更多恐龍吧！",
                    color = Pine, fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(5), modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(11.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            items(dinosaurs, key = { it.id }) { dino ->
                val found = dino.id in discovered
                Panel(Modifier.height(149.dp).fillMaxWidth().clickable(enabled = found) { onChoose(dino) },
                    if (found) Cream else Color(0xFFD7DECE)) {
                    Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.weight(1f).fillMaxWidth()) {
                            if (found) DinoStage(dino, Modifier.fillMaxSize(), Color(0xFFE4F0DC))
                            else Box(Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)).background(Color(0xFFBBCABD)),
                                contentAlignment = Alignment.Center) { Text("❔", fontSize = 44.sp) }
                            if (found) Text("⭐", fontSize = 19.sp, modifier = Modifier.align(Alignment.TopEnd))
                        }
                        Text(if (found) dino.name(english) else "???", fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold, color = Pine, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(english: Boolean, voice: Boolean, rate: Float, onLanguage: (Boolean) -> Unit,
    onVoice: (Boolean) -> Unit, onRate: (Float) -> Unit, onTry: () -> Unit) {
    Panel(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(19.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Text(if (english) "A cozy, quiet island for little explorers" else "給小小探險家的安心恐龍島",
                fontSize = 24.sp, color = Pine, fontWeight = FontWeight.ExtraBold)
            SettingRow("🌐", if (english) "English buttons and voice" else "使用英文介面與語音") {
                Switch(checked = english, onCheckedChange = onLanguage)
            }
            SettingRow("🔊", if (english) "Read aloud automatically" else "自動語音導覽") {
                Switch(checked = voice, onCheckedChange = onVoice)
            }
            SettingRow("🐢", if (english) "Speaking speed" else "說話速度") {
                Slider(value = rate, onValueChange = onRate, valueRange = .7f..1.05f,
                    modifier = Modifier.width(170.dp))
            }
            BigAction("🔉", if (english) "Try the voice" else "試聽語音", Sea, Modifier.width(210.dp), onTry)
            Text(if (english) "No ads · No account · Offline play · No countdowns\nVoice requires a Chinese or English TTS voice installed on the device."
                 else "沒有廣告 · 不需登入 · 可以離線玩 · 沒有倒數計時\n語音使用手機內建的中英文朗讀引擎，需安裝相應語音。",
                fontSize = 15.sp, lineHeight = 23.sp, color = Sea)
        }
    }
}

@Composable
private fun SettingRow(icon: String, title: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 55.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(icon, fontSize = 29.sp)
        Text(title, modifier = Modifier.weight(1f), fontSize = 19.sp, color = Ink, fontWeight = FontWeight.Bold)
        trailing()
    }
}
