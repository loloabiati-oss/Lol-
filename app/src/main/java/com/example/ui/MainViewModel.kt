package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TournamentRepository
import com.example.data.local.AppDatabase
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Tournaments : Screen("tournaments")
    data object Matches : Screen("matches")
    data object Standings : Screen("standings")
    data object Players : Screen("players")
    data object Chat : Screen("chat")
    data object Admin : Screen("admin")
    data object Profile : Screen("profile")
    data object Auth : Screen("auth")
}

data class UiState(
    val currentScreen: Screen = Screen.Home,
    val language: AppLanguage = AppLanguage.ARABIC,
    val currentUser: User? = null,
    val tournaments: List<Tournament> = emptyList(),
    val selectedTournamentId: String? = null,
    val matches: List<Match> = emptyList(),
    val standings: List<Standing> = emptyList(),
    val allPlayers: List<User> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val isFirebaseOnline: Boolean = true,
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false,
    val showCreateTournamentDialog: Boolean = false,
    val activeMatchForScore: Match? = null,
    val activeMatchForProof: Match? = null,
    val showLanguageDialog: Boolean = false,
    val showFirebaseConfigDialog: Boolean = false,
    val playerSearchQuery: String = ""
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = TournamentRepository(db)

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            repository.checkFirebaseStatus()
        }

        // Observe Current User
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }

        // Observe Tournaments
        viewModelScope.launch {
            repository.allTournaments.collect { list ->
                _uiState.update { state ->
                    val selected = state.selectedTournamentId ?: list.firstOrNull()?.id
                    state.copy(tournaments = list, selectedTournamentId = selected)
                }
            }
        }

        // Observe All Users / Players
        viewModelScope.launch {
            repository.allUsers.collect { users ->
                _uiState.update { it.copy(allPlayers = users) }
            }
        }

        // Observe Matches for selected tournament
        viewModelScope.launch {
            _uiState.map { it.selectedTournamentId }
                .distinctUntilChanged()
                .collect { tId ->
                    if (tId != null) {
                        launch {
                            repository.getMatchesByTournament(tId).collect { mList ->
                                _uiState.update { it.copy(matches = mList) }
                            }
                        }
                        launch {
                            repository.getStandingsByTournament(tId).collect { sList ->
                                _uiState.update { it.copy(standings = sList) }
                            }
                        }
                        launch {
                            repository.getChatMessages(tId).collect { cList ->
                                _uiState.update { it.copy(chatMessages = cList) }
                            }
                        }
                    }
                }
        }

        // Observe Firebase status
        viewModelScope.launch {
            repository.firebaseOnline.collect { online ->
                _uiState.update { it.copy(isFirebaseOnline = online) }
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang, showLanguageDialog = false) }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectTournament(tournamentId: String) {
        _uiState.update { it.copy(selectedTournamentId = tournamentId) }
    }

    fun setPlayerSearch(query: String) {
        _uiState.update { it.copy(playerSearchQuery = query) }
    }

    fun showCreateTournament(show: Boolean) {
        _uiState.update { it.copy(showCreateTournamentDialog = show) }
    }

    fun showScoreDialog(match: Match?) {
        _uiState.update { it.copy(activeMatchForScore = match) }
    }

    fun showProofDialog(match: Match?) {
        _uiState.update { it.copy(activeMatchForProof = match) }
    }

    fun showLanguageDialog(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }

    fun showFirebaseDialog(show: Boolean) {
        _uiState.update { it.copy(showFirebaseConfigDialog = show) }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun register(
        email: String,
        pass: String,
        displayName: String,
        efootballId: String,
        platform: GamingPlatform,
        avatarUrl: String,
        isOrganizer: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.registerUser(email, pass, displayName, efootballId, platform, avatarUrl, isOrganizer)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                showMessage("تم إنشاء الحساب بنجاح! مرحبًا بك في منصة eFootball")
                navigateTo(Screen.Home)
            }.onFailure { err ->
                showMessage("فشل التسجيل: ${err.message}")
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.loginUser(email, pass)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                showMessage("تم تسجيل الدخول بنجاح! مرحبًا ${it.displayName}")
                navigateTo(Screen.Home)
            }.onFailure { err ->
                showMessage("فشل تسجيل الدخول: ${err.message}")
            }
        }
    }

    fun logout() {
        repository.logout()
        showMessage("تم تسجيل الخروج")
        navigateTo(Screen.Auth)
    }

    fun updateProfile(displayName: String, efootballId: String, platform: GamingPlatform, avatarUrl: String, bio: String) {
        viewModelScope.launch {
            val res = repository.updateProfile(displayName, efootballId, platform, avatarUrl, bio)
            res.onSuccess {
                showMessage("تم تحديث الملف الشخصي بنجاح ✓")
            }.onFailure {
                showMessage("حدث خطأ أثناء التحديث")
            }
        }
    }

    fun createTournament(
        title: String,
        description: String,
        imageUrl: String,
        maxPlayers: Int,
        format: TournamentFormat,
        startDate: String,
        prizes: String,
        rules: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.createTournament(title, description, imageUrl, maxPlayers, format, startDate, prizes, rules)
            _uiState.update { it.copy(isLoading = false, showCreateTournamentDialog = false) }
            result.onSuccess { t ->
                _uiState.update { it.copy(selectedTournamentId = t.id) }
                showMessage("تم إنشاء البطولة بنجاح! يمكنك الآن بدء دعوة اللاعبين.")
            }.onFailure { err ->
                showMessage("تعذر إنشاء البطولة: ${err.message}")
            }
        }
    }

    fun joinTournament(tournamentId: String) {
        viewModelScope.launch {
            val result = repository.joinTournament(tournamentId)
            result.onSuccess {
                showMessage("تم الانضمام إلى البطولة بنجاح! حظاً موفقاً في eFootball.")
            }.onFailure { err ->
                showMessage("تعذر الانضمام: ${err.message}")
            }
        }
    }

    fun startTournament(tournamentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.startTournamentAndGenerateMatches(tournamentId)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { matches ->
                showMessage("تم بدء البطولة وتوليد ${matches.size} مباريات تلقائيًا!")
                navigateTo(Screen.Matches)
            }.onFailure { err ->
                showMessage("تعذر بدء البطولة: ${err.message}")
            }
        }
    }

    fun toggleReady(matchId: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val result = repository.togglePlayerReady(matchId, user.id)
            result.onSuccess { m ->
                if (m.status == MatchStatus.PLAYERS_READY) {
                    showMessage("كلا اللاعبين جاهزان! افتح لعبة eFootball والعب الآن!")
                } else {
                    showMessage("تم تغيير حالة الجاهزية بنجاح ✓")
                }
            }.onFailure { err ->
                showMessage("خطأ: ${err.message}")
            }
        }
    }

    fun submitScore(matchId: String, p1Score: Int, p2Score: Int, proofUrl: String?) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val result = repository.submitMatchScore(matchId, p1Score, p2Score, proofUrl, user.id)
            _uiState.update { it.copy(activeMatchForScore = null) }
            result.onSuccess {
                showMessage("تم إرسال النتيجة ($p1Score - $p2Score) وإرفاق الإثبات! بانتظار مراجعة المنظم.")
            }.onFailure { err ->
                showMessage("تعذر إرسال النتيجة: ${err.message}")
            }
        }
    }

    fun adminReviewScore(matchId: String, accept: Boolean, note: String?) {
        viewModelScope.launch {
            val result = repository.adminReviewScore(matchId, accept, note)
            result.onSuccess { m ->
                if (accept) {
                    showMessage("تم قبول واعتماد نتيجة المباراة وتحديث جدول الترتيب بنجاح ✓")
                } else {
                    showMessage("تم رفض النتيجة وإرسال إشعار للاعبين لإعادة الإرسال.")
                }
            }.onFailure { err ->
                showMessage("حدث خطأ أثناء المراجعة: ${err.message}")
            }
        }
    }

    fun adminRemoveParticipant(tournamentId: String, playerId: String) {
        viewModelScope.launch {
            val result = repository.adminRemoveParticipant(tournamentId, playerId)
            result.onSuccess {
                showMessage("تم استبعاد اللاعب من البطولة.")
            }.onFailure {
                showMessage("تعذر إزالة اللاعب.")
            }
        }
    }

    fun sendChatMessage(tournamentId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val result = repository.sendChatMessage(tournamentId, null, text.trim())
            result.onFailure { err ->
                showMessage("تعذر إرسال الرسالة: ${err.message}")
            }
        }
    }
}
