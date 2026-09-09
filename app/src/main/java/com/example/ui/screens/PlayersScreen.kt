package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.GamingPlatform
import com.example.model.User
import com.example.ui.components.AvatarImage
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*

@Composable
fun PlayersScreen(
    players: List<User>,
    searchQuery: String,
    language: AppLanguage,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var selectedPlatformFilter by remember { mutableStateOf<GamingPlatform?>(null) }

    val filteredPlayers = remember(players, searchQuery, selectedPlatformFilter) {
        players.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.displayName.contains(searchQuery, ignoreCase = true) ||
                    p.efootballId.contains(searchQuery, ignoreCase = true)
            val matchesPlatform = selectedPlatformFilter == null || p.platform == selectedPlatformFilter
            matchesQuery && matchesPlatform
        }
    }

    fun copyId(efId: String) {
        val clip = ClipData.newPlainText("eFootball ID", efId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "${Strings.get("id_copied", language)}: $efId", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("players_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = Strings.get("nav_players", language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "دليل لاعبي ومحترفي eFootball ومعرفاتهم للتواصل والمباريات",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(Strings.get("search_players", language)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = NeonCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_players"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Platform filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedPlatformFilter == null,
                        onClick = { selectedPlatformFilter = null },
                        label = { Text(Strings.get("all", language)) }
                    )
                }
                items(GamingPlatform.entries) { platform ->
                    FilterChip(
                        selected = selectedPlatformFilter == platform,
                        onClick = { selectedPlatformFilter = platform },
                        label = { Text(platform.titleEn) }
                    )
                }
            }
        }

        // Players Cards
        items(filteredPlayers) { player ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_card_${player.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarImage(avatarKey = player.avatarUrl, modifier = Modifier.size(52.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = player.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (player.isOrganizer) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ChampionGold.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "منظم",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ChampionGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                PlatformBadge(platform = player.platform)
                            }
                        }

                        // eFootball ID Copy Action
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceNavy)
                                .clickable { copyId(player.efootballId) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            color = SurfaceNavy
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = player.efootballId,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    if (player.bio.isNotBlank()) {
                        Text(
                            text = player.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Mini Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${player.matchesPlayed}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "مباريات", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(20.dp).width(1.dp), color = SurfaceBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${player.matchesWon}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TurfGreen)
                            Text(text = "انتصارات", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(20.dp).width(1.dp), color = SurfaceBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${player.tournamentsWon}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ChampionGold)
                            Text(text = "كؤوس 🏆", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
