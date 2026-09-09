package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Standing
import com.example.model.Tournament
import com.example.ui.components.AvatarImage
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*

@Composable
fun StandingsScreen(
    tournaments: List<Tournament>,
    selectedTournamentId: String?,
    standings: List<Standing>,
    language: AppLanguage,
    onSelectTournament: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("standings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = Strings.get("nav_standings", language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "جدول الترتيب، النقاط، إحصائيات الفوز والأهداف في البطولة",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Tournament selector
        item {
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
                        }
                    )
                }
            }
        }

        // Table Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("table_rank", language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_player", language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        text = Strings.get("table_played", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_won", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TurfGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_drawn", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_lost", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = LiveRed,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_goals", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.get("table_points", language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = ChampionGold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Standing Rows
        itemsIndexed(standings) { index, item ->
            val rankNumber = index + 1
            val rankColor = when (rankNumber) {
                1 -> ChampionGold
                2 -> Color(0xFFC0C0C0)
                3 -> Color(0xFFCD7F32)
                else -> TextSecondary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("standing_row_${item.playerId}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rankNumber == 1) SurfaceElevated else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (rankNumber == 1) ChampionGold.copy(alpha = 0.5f) else SurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank Column with trophy for #1
                    Box(
                        modifier = Modifier.width(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rankNumber == 1) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = "1st", tint = ChampionGold, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "$rankNumber",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = rankColor
                            )
                        }
                    }

                    // Player Column
                    Row(
                        modifier = Modifier.weight(1.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvatarImage(avatarKey = item.playerAvatar, modifier = Modifier.size(32.dp))
                        Column {
                            Text(
                                text = item.playerName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.efootballId,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // P, W, D, L, GD, PTS
                    Text(
                        text = "${item.played}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${item.won}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TurfGreen,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${item.drawn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${item.lost}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LiveRed,
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (item.goalDifference > 0) "+${item.goalDifference}" else "${item.goalDifference}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ChampionGold.copy(alpha = 0.15f),
                        modifier = Modifier.width(36.dp)
                    ) {
                        Text(
                            text = "${item.points}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = ChampionGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
