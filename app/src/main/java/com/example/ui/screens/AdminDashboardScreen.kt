package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.*
import com.example.ui.components.AvatarImage
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    tournaments: List<Tournament>,
    selectedTournamentId: String?,
    matches: List<Match>,
    allPlayers: List<User>,
    currentUser: User?,
    language: AppLanguage,
    onSelectTournament: (String) -> Unit,
    onStartTournament: (String) -> Unit,
    onReviewScore: (matchId: String, accept: Boolean, note: String?) -> Unit,
    onRemoveParticipant: (tournamentId: String, playerId: String) -> Unit,
    onCreateTournamentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTournament = tournaments.find { it.id == selectedTournamentId } ?: tournaments.firstOrNull()

    // Matches with submitted results waiting for approval
    val pendingReviewMatches = remember(matches) {
        matches.filter { it.status == MatchStatus.RESULT_SUBMITTED }
    }

    var rejectMatchTarget by remember { mutableStateOf<Match?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ChampionGold)
                        Text(
                            text = Strings.get("nav_admin", language),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "إدارة البطولات، مراجعة نتائج المباريات، واعتماد الفائزين",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onCreateTournamentClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("admin_btn_create_tournament")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "بطولة جديدة", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tournament Selector Chips
        item {
            Text(text = "البطولة المحددة للإدارة:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
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
                        }
                    )
                }
            }
        }

        // Section 1: Pending Result Review Queue (High Priority)
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = LiveRed.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${pendingReviewMatches.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LiveRed
                        )
                    }
                }
                Text(
                    text = "النتائج بانتظار المراجعة والاعتماد:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        if (pendingReviewMatches.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TurfGreen)
                        Text(
                            text = "لا توجد نتائج معلقة حاليًا، جميع المباريات تم اعتمادها.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(pendingReviewMatches) { match ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_review_card_${match.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مباراة #${match.matchNumber} (${match.roundName})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ChampionGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "بانتظار موافقة المنظم",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampionGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Score preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceNavy)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${match.player1Name} (${match.player1EfId})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${match.player1Score ?: 0}  -  ${match.player2Score ?: 0}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan
                            )
                            Text(
                                text = "${match.player2Name} (${match.player2EfId})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Proof image screenshot thumbnail
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_banner),
                                contentDescription = "Proof",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text(text = "إثبات سكرين شوت مرفق من اللعبة ✓", style = MaterialTheme.typography.labelSmall, color = TurfGreen, fontWeight = FontWeight.Bold)
                                Text(text = "مقدمة بواسطة: ${if (match.submittedByPlayerId == match.player1Id) match.player1Name else match.player2Name}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }

                        // Actions: Accept / Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onReviewScore(match.id, true, null) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_accept_score_${match.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "اعتماد النتيجة", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { rejectMatchTarget = match },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_reject_score_${match.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LiveRed)
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "رفض النتيجة")
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Tournament Lifecycle & Bracket Management
        if (selectedTournament != null) {
            item {
                Text(
                    text = "حالة البطولة وجدول المواجهات:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = selectedTournament.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الحالة الحالية: ${selectedTournament.status.titleAr} | عدد المشاركين: ${selectedTournament.participantCount}/${selectedTournament.maxPlayers}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        if (selectedTournament.status == TournamentStatus.REGISTRATION_OPEN) {
                            Button(
                                onClick = { onStartTournament(selectedTournament.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_btn_start_tournament"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ChampionGold)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = PitchDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "بدء البطولة وتوليد الأدوار تلقائيًا", color = PitchDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section 3: Participants Roster & Moderation
            item {
                Text(
                    text = "قائمة المشاركين وإمكانية الاستبعاد:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val roster = allPlayers.filter { selectedTournament.participantIds.contains(it.id) }
            items(roster) { player ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarImage(avatarKey = player.avatarUrl, modifier = Modifier.size(36.dp))
                            Column {
                                Text(text = player.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(text = "${player.efootballId} • ${player.platform.titleEn}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }

                        if (player.id != selectedTournament.organizerId) {
                            IconButton(
                                onClick = { onRemoveParticipant(selectedTournament.id, player.id) }
                            ) {
                                Icon(Icons.Filled.PersonRemove, contentDescription = "Remove", tint = LiveRed)
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (rejectMatchTarget != null) {
        AlertDialog(
            onDismissRequest = { rejectMatchTarget = null },
            title = { Text("رفض النتيجة وإرسال السبب") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("يرجى كتابة سبب الرفض ليتم إشعار اللاعبين به:")
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("سبب الرفض") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectMatchTarget?.let { match ->
                            onReviewScore(match.id, false, rejectionReason.ifBlank { "الصورة المرفقة غير واضحة، يرجى إعادة الإرسال." })
                        }
                        rejectMatchTarget = null
                        rejectionReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("تأكيد الرفض")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectMatchTarget = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
