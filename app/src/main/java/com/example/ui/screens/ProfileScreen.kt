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
fun ProfileScreen(
    currentUser: User?,
    language: AppLanguage,
    isFirebaseOnline: Boolean,
    onUpdateProfile: (name: String, efId: String, platform: GamingPlatform, avatar: String, bio: String) -> Unit,
    onOpenLanguageDialog: () -> Unit,
    onOpenFirebaseDialog: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    if (currentUser == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "يرجى تسجيل الدخول للوصول إلى ملفك الشخصي", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onNavigateToAuth) {
                    Text(text = Strings.get("login", language))
                }
            }
        }
        return
    }

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(currentUser) { mutableStateOf(currentUser.displayName) }
    var editEfId by remember(currentUser) { mutableStateOf(currentUser.efootballId) }
    var editPlatform by remember(currentUser) { mutableStateOf(currentUser.platform) }
    var editBio by remember(currentUser) { mutableStateOf(currentUser.bio) }

    fun copyId() {
        val clip = ClipData.newPlainText("eFootball ID", currentUser.efootballId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "${Strings.get("id_copied", language)}: ${currentUser.efootballId}", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = Strings.get("nav_profile", language),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        }

        // Main User Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarImage(avatarKey = currentUser.avatarUrl, modifier = Modifier.size(64.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = currentUser.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (currentUser.isOrganizer) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ChampionGold.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "منظم رسمي",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ChampionGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                PlatformBadge(platform = currentUser.platform)
                            }
                        }

                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Filled.Close else Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = NeonCyan
                            )
                        }
                    }

                    // eFootball ID Display + Copy
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceNavy)
                            .clickable { copyId() }
                            .padding(12.dp),
                        color = SurfaceNavy
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = Strings.get("efootball_id", language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = currentUser.efootballId,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan
                                )
                            }
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = NeonCyan)
                        }
                    }

                    if (currentUser.bio.isNotBlank()) {
                        Text(
                            text = currentUser.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Career Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${currentUser.matchesPlayed}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "مباريات لعبها", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = SurfaceBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${currentUser.matchesWon}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TurfGreen)
                            Text(text = "انتصارات", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = SurfaceBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${currentUser.tournamentsWon}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ChampionGold)
                            Text(text = "بطولات فاز بها 🏆", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }

        // Edit Profile Section
        if (isEditing) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "تعديل بيانات الحساب", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NeonCyan)

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(Strings.get("player_name", language)) },
                            modifier = Modifier.fillMaxWidth().testTag("edit_input_name"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editEfId,
                            onValueChange = { editEfId = it },
                            label = { Text(Strings.get("efootball_id", language)) },
                            modifier = Modifier.fillMaxWidth().testTag("edit_input_efid"),
                            singleLine = true
                        )

                        Text(text = Strings.get("platform", language), style = MaterialTheme.typography.labelMedium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(GamingPlatform.entries) { p ->
                                FilterChip(
                                    selected = editPlatform == p,
                                    onClick = { editPlatform = p },
                                    label = { Text(p.titleEn, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("نبذة عنك") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Button(
                            onClick = {
                                onUpdateProfile(editName, editEfId, editPlatform, currentUser.avatarUrl, editBio)
                                isEditing = false
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_save_profile"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "حفظ التغييرات", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cloud & App Settings Section
        item {
            Text(
                text = "الإعدادات والاتصال السحابي:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Firebase Cloud Status
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenFirebaseDialog() }
                    .padding(0.dp),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.CloudDone, contentDescription = null, tint = if (isFirebaseOnline) TurfGreen else ChampionGold)
                        Column {
                            Text(text = "Firebase Realtime Database", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = if (isFirebaseOnline) "متصل بالسحابة Online (مزامنة مباشرة)" else "وضع التخزين المحلي Offline", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
        }

        // Language Switcher
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenLanguageDialog() }
                    .padding(0.dp),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = NeonCyan)
                        Column {
                            Text(text = Strings.get("language", language), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = language.displayName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
        }

        // Log out button
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_logout"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LiveRed)
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = Strings.get("logout", language), fontWeight = FontWeight.Bold)
            }
        }
    }
}
