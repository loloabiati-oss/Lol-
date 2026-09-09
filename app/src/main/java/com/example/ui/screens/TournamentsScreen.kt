package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.*
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    tournaments: List<Tournament>,
    allPlayers: List<User>,
    selectedTournamentId: String?,
    currentUser: User?,
    language: AppLanguage,
    onSelectTournament: (String) -> Unit,
    onJoinTournament: (String) -> Unit,
    onStartTournament: (String) -> Unit,
    onCreateTournamentClick: () -> Unit,
    onNavigateToMatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<TournamentStatus?>(null) }
    val filteredTournaments = remember(tournaments, selectedFilter) {
        if (selectedFilter == null) tournaments else tournaments.filter { it.status == selectedFilter }
    }

    val selectedTournament = tournaments.find { it.id == selectedTournamentId } ?: tournaments.firstOrNull()

    var showDetailsSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("tournaments_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTournamentClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = PitchDark,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("fab_create_tournament")
            ) {
                Icon(Icons.Filled.Add, contentDescription = Strings.get("create_tournament", language))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.get("nav_tournaments", language),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "اكتشف بطولات eFootball الرسمية وانضم للمنافسة",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text(Strings.get("all", language)) },
                            modifier = Modifier.testTag("filter_all")
                        )
                    }
                    items(TournamentStatus.entries) { status ->
                        FilterChip(
                            selected = selectedFilter == status,
                            onClick = { selectedFilter = status },
                            label = { Text(status.titleAr) },
                            modifier = Modifier.testTag("filter_${status.name}")
                        )
                    }
                }
            }

            // Selected Tournament Detailed Spotlight Banner
            if (selectedTournament != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selected_tournament_spotlight"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TournamentStatusBadge(status = selectedTournament.status, language = language)
                                Text(
                                    text = "${selectedTournament.participantCount} / ${selectedTournament.maxPlayers} لاعب",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = selectedTournament.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )

                            Text(
                                text = selectedTournament.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            // Rules & Format
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceNavy)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Gavel, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "${Strings.get("rules", language)}: ${selectedTournament.rules}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "${Strings.get("prizes", language)}: ${selectedTournament.prizes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ChampionGoldLight
                                    )
                                }
                            }

                            // Participants Mini Roster Preview
                            Text(
                                text = "${Strings.get("participants", language)} (${selectedTournament.participantCount}):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            val participantsList = allPlayers.filter { selectedTournament.participantIds.contains(it.id) }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(participantsList) { player ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            AvatarImage(avatarKey = player.avatarUrl, modifier = Modifier.size(24.dp))
                                            Column {
                                                Text(text = player.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                Text(text = player.efootballId, style = MaterialTheme.typography.labelSmall, color = NeonCyan, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Action buttons: Join or Organizer Start
                            val isJoined = currentUser?.let { selectedTournament.participantIds.contains(it.id) } == true
                            val isOrganizer = currentUser?.id == selectedTournament.organizerId || currentUser?.isOrganizer == true

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (selectedTournament.status == TournamentStatus.REGISTRATION_OPEN) {
                                    Button(
                                        onClick = { onJoinTournament(selectedTournament.id) },
                                        enabled = !isJoined && selectedTournament.participantCount < selectedTournament.maxPlayers,
                                        modifier = Modifier.weight(1f).testTag("btn_spotlight_join"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isJoined) TurfGreen else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (isJoined) Strings.get("already_joined", language) else Strings.get("join_tournament", language),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (isOrganizer && selectedTournament.status == TournamentStatus.REGISTRATION_OPEN) {
                                    Button(
                                        onClick = { onStartTournament(selectedTournament.id) },
                                        modifier = Modifier.weight(1f).testTag("btn_spotlight_start"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ChampionGold)
                                    ) {
                                        Text(
                                            text = Strings.get("start_tournament_now", language),
                                            color = PitchDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (selectedTournament.status != TournamentStatus.REGISTRATION_OPEN) {
                                    Button(
                                        onClick = onNavigateToMatches,
                                        modifier = Modifier.weight(1f).testTag("btn_spotlight_matches"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.SportsSoccer, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "عرض جدول المباريات")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // List of other tournaments
            items(filteredTournaments) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    language = language,
                    currentUser = currentUser,
                    onSelect = { onSelectTournament(tournament.id) },
                    onJoin = { onJoinTournament(tournament.id) }
                )
            }
        }
    }
}
