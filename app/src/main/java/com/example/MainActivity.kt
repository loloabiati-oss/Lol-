package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(uiState.snackbarMessage) {
                uiState.snackbarMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearSnackbar()
                }
            }

            // RTL support for Arabic, LTR for English/French
            val layoutDirection = if (uiState.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MyApplicationTheme {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWideScreen = maxWidth >= 600.dp

                        Row(modifier = Modifier.fillMaxSize()) {
                            // Tablet / Desktop Navigation Rail
                            if (isWideScreen && uiState.currentScreen != Screen.Auth) {
                                AppNavigationRail(
                                    currentScreen = uiState.currentScreen,
                                    language = uiState.language,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            Scaffold(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                topBar = {
                                    TopAppBar(
                                        title = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.SportsSoccer,
                                                    contentDescription = "Logo",
                                                    tint = NeonCyan,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = Strings.get("app_title", uiState.language),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Black,
                                                    color = TextPrimary
                                                )
                                            }
                                        },
                                        actions = {
                                            // Firebase Cloud live status indicator
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (uiState.isFirebaseOnline) TurfGreen.copy(alpha = 0.15f) else ChampionGold.copy(alpha = 0.15f),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (uiState.isFirebaseOnline) TurfGreen.copy(alpha = 0.5f) else ChampionGold.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { viewModel.showFirebaseDialog(true) }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(if (uiState.isFirebaseOnline) TurfGreen else ChampionGold)
                                                    )
                                                    Text(
                                                        text = if (uiState.isFirebaseOnline) "Firebase Online" else "Offline Mode",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (uiState.isFirebaseOnline) TurfGreen else ChampionGold,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            // Language button
                                            IconButton(
                                                onClick = { viewModel.showLanguageDialog(true) },
                                                modifier = Modifier.testTag("btn_top_language")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Language,
                                                    contentDescription = Strings.get("language", uiState.language),
                                                    tint = NeonCyan
                                                )
                                            }

                                            // Admin icon shortcut
                                            IconButton(
                                                onClick = { viewModel.navigateTo(Screen.Admin) },
                                                modifier = Modifier.testTag("btn_top_admin")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.AdminPanelSettings,
                                                    contentDescription = "Admin",
                                                    tint = ChampionGold
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                },
                                bottomBar = {
                                    // Mobile bottom bar (hidden in wide screen or auth screen)
                                    if (!isWideScreen && uiState.currentScreen != Screen.Auth) {
                                        AppBottomNavigationBar(
                                            currentScreen = uiState.currentScreen,
                                            language = uiState.language,
                                            onNavigate = { viewModel.navigateTo(it) }
                                        )
                                    }
                                },
                                snackbarHost = { SnackbarHost(snackbarHostState) },
                                containerColor = MaterialTheme.colorScheme.background
                            ) { paddingValues ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                                ) {
                                    when (uiState.currentScreen) {
                                        Screen.Home -> HomeScreen(
                                            tournaments = uiState.tournaments,
                                            recentMatches = uiState.matches,
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            onNavigate = { viewModel.navigateTo(it) },
                                            onSelectTournament = { viewModel.selectTournament(it) },
                                            onJoinTournament = { viewModel.joinTournament(it) },
                                            onCreateTournamentClick = { viewModel.showCreateTournament(true) },
                                            onToggleReady = { viewModel.toggleReady(it) },
                                            onSubmitScoreClick = { viewModel.showScoreDialog(it) },
                                            onViewProofClick = { viewModel.showProofDialog(it) }
                                        )

                                        Screen.Tournaments -> TournamentsScreen(
                                            tournaments = uiState.tournaments,
                                            allPlayers = uiState.allPlayers,
                                            selectedTournamentId = uiState.selectedTournamentId,
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            onSelectTournament = { viewModel.selectTournament(it) },
                                            onJoinTournament = { viewModel.joinTournament(it) },
                                            onStartTournament = { viewModel.startTournament(it) },
                                            onCreateTournamentClick = { viewModel.showCreateTournament(true) },
                                            onNavigateToMatches = { viewModel.navigateTo(Screen.Matches) }
                                        )

                                        Screen.Matches -> MatchesScreen(
                                            tournaments = uiState.tournaments,
                                            selectedTournamentId = uiState.selectedTournamentId,
                                            matches = uiState.matches,
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            onSelectTournament = { viewModel.selectTournament(it) },
                                            onToggleReady = { viewModel.toggleReady(it) },
                                            onSubmitScoreClick = { viewModel.showScoreDialog(it) },
                                            onViewProofClick = { viewModel.showProofDialog(it) },
                                            onStartTournament = { viewModel.startTournament(it) }
                                        )

                                        Screen.Standings -> StandingsScreen(
                                            tournaments = uiState.tournaments,
                                            selectedTournamentId = uiState.selectedTournamentId,
                                            standings = uiState.standings,
                                            language = uiState.language,
                                            onSelectTournament = { viewModel.selectTournament(it) }
                                        )

                                        Screen.Players -> PlayersScreen(
                                            players = uiState.allPlayers,
                                            searchQuery = uiState.playerSearchQuery,
                                            language = uiState.language,
                                            onSearchChange = { viewModel.setPlayerSearch(it) }
                                        )

                                        Screen.Chat -> ChatScreen(
                                            tournaments = uiState.tournaments,
                                            selectedTournamentId = uiState.selectedTournamentId,
                                            messages = uiState.chatMessages,
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            onSelectTournament = { viewModel.selectTournament(it) },
                                            onSendMessage = { text ->
                                                uiState.selectedTournamentId?.let { tId ->
                                                    viewModel.sendChatMessage(tId, text)
                                                }
                                            }
                                        )

                                        Screen.Admin -> AdminDashboardScreen(
                                            tournaments = uiState.tournaments,
                                            selectedTournamentId = uiState.selectedTournamentId,
                                            matches = uiState.matches,
                                            allPlayers = uiState.allPlayers,
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            onSelectTournament = { viewModel.selectTournament(it) },
                                            onStartTournament = { viewModel.startTournament(it) },
                                            onReviewScore = { mId, accept, note ->
                                                viewModel.adminReviewScore(mId, accept, note)
                                            },
                                            onRemoveParticipant = { tId, pId ->
                                                viewModel.adminRemoveParticipant(tId, pId)
                                            },
                                            onCreateTournamentClick = { viewModel.showCreateTournament(true) }
                                        )

                                        Screen.Profile -> ProfileScreen(
                                            currentUser = uiState.currentUser,
                                            language = uiState.language,
                                            isFirebaseOnline = uiState.isFirebaseOnline,
                                            onUpdateProfile = { name, efId, platform, avatar, bio ->
                                                viewModel.updateProfile(name, efId, platform, avatar, bio)
                                            },
                                            onOpenLanguageDialog = { viewModel.showLanguageDialog(true) },
                                            onOpenFirebaseDialog = { viewModel.showFirebaseDialog(true) },
                                            onLogout = { viewModel.logout() },
                                            onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) }
                                        )

                                        Screen.Auth -> AuthScreen(
                                            language = uiState.language,
                                            isLoading = uiState.isLoading,
                                            onLogin = { email, pass -> viewModel.login(email, pass) },
                                            onRegister = { email, pass, name, efId, platform, avatar, isOrg ->
                                                viewModel.register(email, pass, name, efId, platform, avatar, isOrg)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Dialogs
                    if (uiState.showCreateTournamentDialog) {
                        CreateTournamentDialog(
                            language = uiState.language,
                            onDismiss = { viewModel.showCreateTournament(false) },
                            onCreate = { title, desc, maxPlayers, format, startDate, prizes, rules ->
                                viewModel.createTournament(title, desc, "hero_banner", maxPlayers, format, startDate, prizes, rules)
                            }
                        )
                    }

                    uiState.activeMatchForScore?.let { match ->
                        SubmitScoreDialog(
                            match = match,
                            language = uiState.language,
                            onDismiss = { viewModel.showScoreDialog(null) },
                            onSubmit = { s1, s2, proof ->
                                viewModel.submitScore(match.id, s1, s2, proof)
                            }
                        )
                    }

                    uiState.activeMatchForProof?.let { match ->
                        ProofViewerDialog(
                            match = match,
                            language = uiState.language,
                            onDismiss = { viewModel.showProofDialog(null) }
                        )
                    }

                    if (uiState.showLanguageDialog) {
                        LanguageSelectDialog(
                            currentLanguage = uiState.language,
                            onSelectLanguage = { viewModel.setLanguage(it) },
                            onDismiss = { viewModel.showLanguageDialog(false) }
                        )
                    }

                    if (uiState.showFirebaseConfigDialog) {
                        AlertDialog(
                            onDismissRequest = { viewModel.showFirebaseDialog(false) },
                            title = {
                                Text("اتصال Firebase السحابي", fontWeight = FontWeight.Bold)
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "يستخدم التطبيق Firebase Realtime Database لحفظ وتحديث المستخدمين والبطولات والمباريات والنتائج والدردشة بشكل مباشر Online مع مزامنة محلية تلقائية عبر Room Database.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceElevated,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(text = "حالة الاتصال: Online متصل ومفعل ✓", color = TurfGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                            Text(text = "الخدمة: Firebase Auth + RTDB Endpoint", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { viewModel.showFirebaseDialog(false) }) {
                                    Text("تم")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
