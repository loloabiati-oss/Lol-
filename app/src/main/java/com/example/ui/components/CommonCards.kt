package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun PlatformBadge(platform: GamingPlatform, modifier: Modifier = Modifier) {
    val (icon, color) = when (platform) {
        GamingPlatform.MOBILE -> Icons.Filled.PhoneAndroid to NeonCyan
        GamingPlatform.PLAYSTATION -> Icons.Filled.SportsEsports to Color(0xFF2E6DB4)
        GamingPlatform.XBOX -> Icons.Filled.Gamepad to Color(0xFF107C10)
        GamingPlatform.PC -> Icons.Filled.Computer to Color(0xFF9C27B0)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = platform.titleEn, tint = color, modifier = Modifier.size(14.dp))
            Text(text = platform.titleEn, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TournamentStatusBadge(status: TournamentStatus, language: AppLanguage) {
    val (bg, textCol, labelKey) = when (status) {
        TournamentStatus.REGISTRATION_OPEN -> Triple(TurfGreen.copy(alpha = 0.15f), TurfGreen, "status_registration_open")
        TournamentStatus.STARTED -> Triple(NeonCyan.copy(alpha = 0.15f), NeonCyan, "status_started")
        TournamentStatus.IN_PROGRESS -> Triple(ChampionGold.copy(alpha = 0.15f), ChampionGold, "status_in_progress")
        TournamentStatus.COMPLETED -> Triple(Color(0xFF64748B).copy(alpha = 0.2f), Color(0xFF94A3B8), "status_completed")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, textCol.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textCol)
            )
            Text(
                text = Strings.get(labelKey, language),
                style = MaterialTheme.typography.labelSmall,
                color = textCol,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MatchStatusBadge(status: MatchStatus, language: AppLanguage) {
    val (color, icon, text) = when (status) {
        MatchStatus.SCHEDULED -> Triple(TextSecondary, Icons.Filled.Schedule, Strings.get("match_status_scheduled", language))
        MatchStatus.WAITING_FOR_READY -> Triple(ChampionGold, Icons.Filled.HourglassEmpty, Strings.get("match_status_waiting_ready", language))
        MatchStatus.PLAYERS_READY -> Triple(TurfGreen, Icons.Filled.PlayArrow, Strings.get("match_status_live", language))
        MatchStatus.IN_PROGRESS -> Triple(NeonCyan, Icons.Filled.SportsSoccer, Strings.get("match_status_live", language))
        MatchStatus.RESULT_SUBMITTED -> Triple(Color(0xFFFFA000), Icons.Filled.CheckCircleOutline, Strings.get("match_status_under_review", language))
        MatchStatus.COMPLETED -> Triple(TurfGreen, Icons.Filled.CheckCircle, Strings.get("match_status_completed", language))
        MatchStatus.DISPUTED -> Triple(LiveRed, Icons.Filled.ReportProblem, "مرفوضة / نزاع")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AvatarImage(avatarKey: String, modifier: Modifier = Modifier) {
    val resId = when (avatarKey) {
        "avatar_1", "hero_banner" -> R.drawable.hero_banner
        else -> R.drawable.ic_app_icon
    }
    Image(
        painter = painterResource(id = resId),
        contentDescription = "Avatar",
        modifier = modifier
            .clip(CircleShape)
            .border(1.5.dp, NeonCyan, CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TournamentCard(
    tournament: Tournament,
    language: AppLanguage,
    currentUser: User?,
    onSelect: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isJoined = currentUser?.let { tournament.participantIds.contains(it.id) } == true
    val isFull = tournament.participantCount >= tournament.maxPlayers

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner),
                    contentDescription = tournament.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, PitchDark.copy(alpha = 0.85f))
                            )
                        )
                )

                // Status Badge at top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TournamentStatusBadge(status = tournament.status, language = language)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PitchDark.copy(alpha = 0.75f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${tournament.participantCount}/${tournament.maxPlayers}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Tournament Title at bottom of banner
                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // Body
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = tournament.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Format & Prizes info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(16.dp))
                        Text(
                            text = tournament.format.titleAr,
                            style = MaterialTheme.typography.labelMedium,
                            color = ChampionGoldLight
                        )
                    }

                    Text(
                        text = tournament.startDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                // Prizes row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceNavy)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(16.dp))
                    Text(
                        text = tournament.prizes,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Join / View Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSelect,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_view_tournament_${tournament.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "تفاصيل البطولة")
                    }

                    if (tournament.status == TournamentStatus.REGISTRATION_OPEN) {
                        Button(
                            onClick = onJoin,
                            enabled = !isJoined && !isFull,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_join_tournament_${tournament.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isJoined) TurfGreen else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (isJoined) Strings.get("already_joined", language)
                                else if (isFull) Strings.get("tournament_full", language)
                                else Strings.get("join_tournament", language),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(
    match: Match,
    language: AppLanguage,
    currentUser: User?,
    onToggleReady: () -> Unit,
    onSubmitScore: () -> Unit,
    onViewProof: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyId(efootballId: String) {
        val clip = ClipData.newPlainText("eFootball ID", efootballId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "${Strings.get("id_copied", language)} ($efootballId)", Toast.LENGTH_SHORT).show()
    }

    val isUserP1 = currentUser?.id == match.player1Id
    val isUserP2 = currentUser?.id == match.player2Id
    val isUserInMatch = isUserP1 || isUserP2

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header: Round and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#M${match.matchNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = match.roundName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                MatchStatusBadge(status = match.status, language = language)
            }

            // VS Arena: Player 1 VS Player 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceNavy)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box {
                        AvatarImage(avatarKey = match.player1Avatar, modifier = Modifier.size(50.dp))
                        if (match.player1Ready) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(TurfGreen)
                                    .border(1.5.dp, SurfaceNavy, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.player1Name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    PlatformBadge(platform = match.player1Platform)
                    Spacer(modifier = Modifier.height(4.dp))
                    // eFootball ID + Copy
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceElevated)
                            .clickable { copyId(match.player1EfId) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = match.player1EfId, style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(11.dp))
                    }
                }

                // Middle: Score or VS
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    if (match.player1Score != null && match.player2Score != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${match.player1Score} - ${match.player2Score}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = LiveRed.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LiveRed.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = LiveRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = match.scheduledTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Player 2
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box {
                        AvatarImage(avatarKey = match.player2Avatar, modifier = Modifier.size(50.dp))
                        if (match.player2Ready) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(TurfGreen)
                                    .border(1.5.dp, SurfaceNavy, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.player2Name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    PlatformBadge(platform = match.player2Platform)
                    Spacer(modifier = Modifier.height(4.dp))
                    // eFootball ID + Copy
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceElevated)
                            .clickable { copyId(match.player2EfId) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = match.player2EfId, style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(11.dp))
                    }
                }
            }

            // Notice when both ready
            if (match.status == MatchStatus.PLAYERS_READY || (match.player1Ready && match.player2Ready)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TurfGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TurfGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.SportsSoccer, contentDescription = null, tint = TurfGreen, modifier = Modifier.size(20.dp))
                        Text(
                            text = Strings.get("both_ready_msg", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TurfGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Buttons row for participant players
            if (isUserInMatch) {
                val isMyReady = if (isUserP1) match.player1Ready else match.player2Ready
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ready button
                    Button(
                        onClick = onToggleReady,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMyReady) TurfGreen else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ready_match_${match.id}")
                    ) {
                        Icon(
                            imageVector = if (isMyReady) Icons.Filled.Check else Icons.Filled.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMyReady) Strings.get("btn_you_are_ready", language) else Strings.get("btn_ready", language),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Submit Score button (enabled after both are ready or already in progress)
                    Button(
                        onClick = onSubmitScore,
                        enabled = match.status != MatchStatus.COMPLETED,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChampionGold),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_submit_score_match_${match.id}")
                    ) {
                        Icon(Icons.Filled.Scoreboard, contentDescription = null, tint = PitchDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = Strings.get("submit_result", language), color = PitchDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // View Proof button if proof exists
            if (!match.proofImageUrl.isNullOrBlank()) {
                OutlinedButton(
                    onClick = onViewProof,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_view_proof_${match.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "عرض سكرين شوت النتيجة")
                }
            }
        }
    }
}

@Composable
fun SubmitScoreDialog(
    match: Match,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (p1Score: Int, p2Score: Int, proofUrl: String?) -> Unit
) {
    var p1Score by remember { mutableStateOf(match.player1Score?.toString() ?: "0") }
    var p2Score by remember { mutableStateOf(match.player2Score?.toString() ?: "0") }
    var proofAttached by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Strings.get("submit_result", language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "${match.player1Name}  ضد  ${match.player2Name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = p1Score,
                        onValueChange = { if (it.length <= 2 && it.all { char -> char.isDigit() }) p1Score = it },
                        label = { Text(match.player1Name, maxLines = 1) },
                        modifier = Modifier.weight(1f).testTag("input_score_p1"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = p2Score,
                        onValueChange = { if (it.length <= 2 && it.all { char -> char.isDigit() }) p2Score = it },
                        label = { Text(match.player2Name, maxLines = 1) },
                        modifier = Modifier.weight(1f).testTag("input_score_p2"),
                        singleLine = true
                    )
                }

                // Screenshot Proof selector
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { proofAttached = true }
                        .testTag("btn_attach_proof"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (proofAttached) TurfGreen.copy(alpha = 0.15f) else SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (proofAttached) TurfGreen else SurfaceBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (proofAttached) Icons.Filled.CheckCircle else Icons.Filled.PhotoCamera,
                            contentDescription = null,
                            tint = if (proofAttached) TurfGreen else NeonCyan
                        )
                        Text(
                            text = if (proofAttached) Strings.get("proof_attached", language) else Strings.get("upload_proof", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (proofAttached) TurfGreen else TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "ملاحظة: سيتم إرسال النتيجة إلى المنظم لمراجعة لقطة الشاشة واعتمادها رسميًا.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s1 = p1Score.toIntOrNull() ?: 0
                    val s2 = p2Score.toIntOrNull() ?: 0
                    val proof = if (proofAttached) "proof_captured" else null
                    onSubmit(s1, s2, proof)
                },
                modifier = Modifier.testTag("btn_confirm_submit_score")
            ) {
                Text(text = Strings.get("submit_result", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = Strings.get("cancel", language))
            }
        }
    )
}

@Composable
fun ProofViewerDialog(
    match: Match,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = Strings.get("proof_screenshot", language), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner),
                    contentDescription = "Match Score Proof",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "النتيجة المسجلة: ${match.player1Score ?: 0} - ${match.player2Score ?: 0}",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                match.organizerNote?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = Strings.get("close", language))
            }
        }
    )
}

@Composable
fun CreateTournamentDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, maxPlayers: Int, format: TournamentFormat, startDate: String, prizes: String, rules: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var maxPlayers by remember { mutableIntStateOf(16) }
    var selectedFormat by remember { mutableStateOf(TournamentFormat.KNOCKOUT) }
    var prizes by remember { mutableStateOf("10,000 كوينز + كأس البطولة 🏆") }
    var rules by remember { mutableStateOf("مدة المباراة 10 دقائق، حالة اللاعبين عادية، رفع سكرين شوت النتيجة إلزامي.") }
    var startDate by remember { mutableStateOf("اليوم 21:00 GMT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = Strings.get("create_tournament", language), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(Strings.get("tournament_name", language)) },
                    modifier = Modifier.fillMaxWidth().testTag("input_tournament_title"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("وصف البطولة") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tournament_desc"),
                    maxLines = 2
                )

                // Max Players selection
                Text(text = Strings.get("max_players", language), style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(4, 8, 16, 32).forEach { count ->
                        FilterChip(
                            selected = maxPlayers == count,
                            onClick = { maxPlayers = count },
                            label = { Text("$count") },
                            modifier = Modifier.testTag("chip_players_$count")
                        )
                    }
                }

                // Format selection
                Text(text = Strings.get("tournament_format", language), style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        TournamentFormat.KNOCKOUT to "خروج مغلوب",
                        TournamentFormat.GROUPS_KNOCKOUT to "مجموعات",
                        TournamentFormat.ROUND_ROBIN to "دوري"
                    ).forEach { (fmt, label) ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = { selectedFormat = fmt },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = prizes,
                    onValueChange = { prizes = it },
                    label = { Text(Strings.get("prizes", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text(Strings.get("rules", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, desc.ifBlank { "بطولة eFootball رسمية" }, maxPlayers, selectedFormat, startDate, prizes, rules)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("btn_submit_create_tournament")
            ) {
                Text(text = Strings.get("create_tournament", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = Strings.get("cancel", language))
            }
        }
    )
}

@Composable
fun LanguageSelectDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "اختر اللغة / Select Language", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { lang ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLanguage(lang) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == lang) MaterialTheme.colorScheme.primaryContainer else SurfaceElevated
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentLanguage == lang) NeonCyan else SurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
                            )
                            if (currentLanguage == lang) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = NeonCyan)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء")
            }
        }
    )
}
