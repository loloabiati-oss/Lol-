package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Match
import com.example.model.MatchStatus
import com.example.model.Tournament
import com.example.model.User
import com.example.ui.components.MatchCard
import com.example.ui.theme.*

@Composable
fun MatchesScreen(
    tournaments: List<Tournament>,
    selectedTournamentId: String?,
    matches: List<Match>,
    currentUser: User?,
    language: AppLanguage,
    onSelectTournament: (String) -> Unit,
    onToggleReady: (String) -> Unit,
    onSubmitScoreClick: (Match) -> Unit,
    onViewProofClick: (Match) -> Unit,
    onStartTournament: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyMyMatches by remember { mutableStateOf(false) }
    var selectedRoundFilter by remember { mutableStateOf<String?>(null) }

    val filteredMatches = remember(matches, showOnlyMyMatches, selectedRoundFilter, currentUser) {
        matches.filter { m ->
            val matchUserFilter = if (showOnlyMyMatches && currentUser != null) {
                m.player1Id == currentUser.id || m.player2Id == currentUser.id
            } else true
            val roundFilter = if (selectedRoundFilter != null) {
                m.roundName == selectedRoundFilter
            } else true
            matchUserFilter && roundFilter
        }
    }

    val distinctRounds = remember(matches) {
        matches.map { it.roundName }.distinct()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("matches_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = Strings.get("nav_matches", language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "جدول المباريات، حالة الجاهزية وإرسال نتائج eFootball المعتمدة",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Tournament Selector Chips
        item {
            Text(
                text = "اختر البطولة:",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tournaments) { tour ->
                    FilterChip(
                        selected = tour.id == selectedTournamentId,
                        onClick = { onSelectTournament(tour.id) },
                        label = { Text(tour.title, maxLines = 1) },
                        leadingIcon = {
                            if (tour.id == selectedTournamentId) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        modifier = Modifier.testTag("chip_tournament_${tour.id}")
                    )
                }
            }
        }

        // Filters: My Matches & Rounds
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = showOnlyMyMatches,
                    onClick = { showOnlyMyMatches = !showOnlyMyMatches },
                    label = { Text("مبارياتي فقط") },
                    leadingIcon = {
                        Icon(
                            if (showOnlyMyMatches) Icons.Filled.Check else Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.testTag("filter_my_matches")
                )

                distinctRounds.forEach { round ->
                    FilterChip(
                        selected = selectedRoundFilter == round,
                        onClick = { selectedRoundFilter = if (selectedRoundFilter == round) null else round },
                        label = { Text(round, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        // Empty matches state
        if (matches.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SportsSoccer,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "لم يتم إنشاء مباريات لهذه البطولة بعد",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "عند بدء البطولة بواسطة المنظم، سيتم توليد جدول المواجهات تلقائيًا وتوزيع اللاعبين على الأدوار.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        if (selectedTournamentId != null && currentUser?.isOrganizer == true) {
                            Button(
                                onClick = { onStartTournament(selectedTournamentId) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(text = Strings.get("start_tournament_now", language), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredMatches) { match ->
                MatchCard(
                    match = match,
                    language = language,
                    currentUser = currentUser,
                    onToggleReady = { onToggleReady(match.id) },
                    onSubmitScore = { onSubmitScoreClick(match) },
                    onViewProof = { onViewProofClick(match) }
                )
            }
        }
    }
}
