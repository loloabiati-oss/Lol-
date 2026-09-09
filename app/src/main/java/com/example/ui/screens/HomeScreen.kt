package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Match
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.User
import com.example.ui.Screen
import com.example.ui.components.MatchCard
import com.example.ui.components.TournamentCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    tournaments: List<Tournament>,
    recentMatches: List<Match>,
    currentUser: User?,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onSelectTournament: (String) -> Unit,
    onJoinTournament: (String) -> Unit,
    onCreateTournamentClick: () -> Unit,
    onToggleReady: (String) -> Unit,
    onSubmitScoreClick: (Match) -> Unit,
    onViewProofClick: (Match) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Stadium Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner),
                    contentDescription = "eFootball Stadium Arena",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Stadium gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PitchDark.copy(alpha = 0.5f),
                                    PitchDark
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "eFootball 2026 Esports Arena",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = Strings.get("app_title", language),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Text(
                        text = Strings.get("app_subtitle", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Disclaimer Card: App is an organizing platform, matches played in eFootball
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = Strings.get("disclaimer_text", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStatCard(
                    title = Strings.get("stats_tournaments", language),
                    value = "${tournaments.size}",
                    icon = Icons.Filled.EmojiEvents,
                    accentColor = ChampionGold,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = Strings.get("stats_active_tournaments", language),
                    value = "${tournaments.count { it.status == TournamentStatus.IN_PROGRESS }}",
                    icon = Icons.Filled.PlayCircle,
                    accentColor = TurfGreen,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = Strings.get("stats_matches", language),
                    value = "${recentMatches.size}",
                    icon = Icons.Filled.SportsSoccer,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Buttons Row (Create Tournament, My Matches)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCreateTournamentClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_home_create_tournament"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("create_tournament", language), fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onNavigate(Screen.Matches) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_home_view_matches"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.SportsSoccer, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("nav_matches", language))
                }
            }
        }

        // Available Tournaments Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("featured_tournaments", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate(Screen.Tournaments) }) {
                    Text(text = Strings.get("all", language), color = NeonCyan)
                }
            }
        }

        items(tournaments.take(3)) { tournament ->
            TournamentCard(
                tournament = tournament,
                language = language,
                currentUser = currentUser,
                onSelect = {
                    onSelectTournament(tournament.id)
                    onNavigate(Screen.Tournaments)
                },
                onJoin = { onJoinTournament(tournament.id) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Latest Matches Section
        if (recentMatches.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("recent_matches", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { onNavigate(Screen.Matches) }) {
                        Text(text = Strings.get("all", language), color = NeonCyan)
                    }
                }
            }

            items(recentMatches.take(2)) { match ->
                MatchCard(
                    match = match,
                    language = language,
                    currentUser = currentUser,
                    onToggleReady = { onToggleReady(match.id) },
                    onSubmitScore = { onSubmitScoreClick(match) },
                    onViewProof = { onViewProofClick(match) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
