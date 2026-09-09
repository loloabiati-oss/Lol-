package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.GamingPlatform
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    language: AppLanguage,
    isLoading: Boolean,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (email: String, pass: String, name: String, efId: String, platform: GamingPlatform, avatar: String, isOrganizer: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    var loginEmail by remember { mutableStateOf("admin@efootball-hub.com") }
    var loginPassword by remember { mutableStateOf("123456") }

    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regEfId by remember { mutableStateOf("") }
    var regPlatform by remember { mutableStateOf(GamingPlatform.MOBILE) }
    var regIsOrganizer by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner),
                    contentDescription = "Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, PitchDark.copy(alpha = 0.8f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = Strings.get("app_title", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "منصة تنظيم وإدارة بطولات eFootball الرسمية",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan
                    )
                }
            }
        }

        // Tab Row: Login / Register
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = NeonCyan,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(Strings.get("login", language), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(Strings.get("register", language), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_register")
                )
            }
        }

        if (selectedTab == 0) {
            // Login Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = Strings.get("login_title", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text(Strings.get("email", language)) },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_login_email"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text(Strings.get("password", language)) },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("input_login_password"),
                            singleLine = true
                        )

                        Button(
                            onClick = { onLogin(loginEmail, loginPassword) },
                            enabled = !isLoading && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_login"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PitchDark)
                            } else {
                                Text(text = Strings.get("login", language), fontWeight = FontWeight.Bold, color = PitchDark)
                            }
                        }

                        // Quick demo buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    loginEmail = "admin@efootball-hub.com"
                                    loginPassword = "123"
                                    onLogin(loginEmail, loginPassword)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "دخول كمنظم (Demo)", style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = {
                                    loginEmail = "salah@efootball.com"
                                    loginPassword = "123"
                                    onLogin(loginEmail, loginPassword)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "دخول كلاعب (Demo)", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        } else {
            // Register Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = Strings.get("register_title", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text(Strings.get("player_name", language)) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_name"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text(Strings.get("email", language)) },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text(Strings.get("password", language)) },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_password"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = regEfId,
                            onValueChange = { regEfId = it },
                            label = { Text(Strings.get("efootball_id", language)) },
                            placeholder = { Text("مثال: 782-419-503") },
                            leadingIcon = { Icon(Icons.Filled.SportsEsports, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_reg_efid"),
                            singleLine = true
                        )

                        Text(text = Strings.get("platform", language), style = MaterialTheme.typography.labelMedium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(GamingPlatform.entries) { p ->
                                FilterChip(
                                    selected = regPlatform == p,
                                    onClick = { regPlatform = p },
                                    label = { Text(p.titleEn, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        // Organizer toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "حساب مسؤول / منظم بطولات؟", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = regIsOrganizer,
                                onCheckedChange = { regIsOrganizer = it },
                                modifier = Modifier.testTag("switch_is_organizer")
                            )
                        }

                        Button(
                            onClick = {
                                onRegister(regEmail, regPassword, regName, regEfId, regPlatform, "avatar_1", regIsOrganizer)
                            },
                            enabled = !isLoading && regEmail.isNotBlank() && regPassword.isNotBlank() && regName.isNotBlank() && regEfId.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_register"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PitchDark)
                            } else {
                                Text(text = Strings.get("register", language), fontWeight = FontWeight.Bold, color = PitchDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
