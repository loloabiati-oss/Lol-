package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.ui.Screen

data class NavItem(
    val screen: Screen,
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val mainNavItems = listOf(
    NavItem(Screen.Home, "nav_home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home_tab"),
    NavItem(Screen.Tournaments, "nav_tournaments", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_tournaments_tab"),
    NavItem(Screen.Matches, "nav_matches", Icons.Filled.SportsSoccer, Icons.Outlined.SportsSoccer, "nav_matches_tab"),
    NavItem(Screen.Standings, "nav_standings", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, "nav_standings_tab"),
    NavItem(Screen.Players, "nav_players", Icons.Filled.Groups, Icons.Outlined.Groups, "nav_players_tab"),
    NavItem(Screen.Chat, "nav_chat", Icons.Filled.Chat, Icons.Outlined.Chat, "nav_chat_tab"),
    NavItem(Screen.Admin, "nav_admin", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_admin_tab"),
    NavItem(Screen.Profile, "nav_profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile_tab")
)

@Composable
fun AppBottomNavigationBar(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    // Show top 5 primary items in bottom bar for compact screens, with overflow to profile/admin
    val compactNavItems = listOf(
        NavItem(Screen.Home, "nav_home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home_tab"),
        NavItem(Screen.Tournaments, "nav_tournaments", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_tournaments_tab"),
        NavItem(Screen.Matches, "nav_matches", Icons.Filled.SportsSoccer, Icons.Outlined.SportsSoccer, "nav_matches_tab"),
        NavItem(Screen.Standings, "nav_standings", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, "nav_standings_tab"),
        NavItem(Screen.Chat, "nav_chat", Icons.Filled.Chat, Icons.Outlined.Chat, "nav_chat_tab"),
        NavItem(Screen.Profile, "nav_profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile_tab")
    )

    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_bottom_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        compactNavItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                modifier = Modifier.testTag(item.testTag),
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = Strings.get(item.titleKey, language)
                    )
                },
                label = {
                    Text(
                        text = Strings.get(item.titleKey, language),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("app_nav_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Icon(
                imageVector = Icons.Filled.SportsSoccer,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(36.dp)
            )
        }
    ) {
        mainNavItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationRailItem(
                modifier = Modifier.testTag(item.testTag),
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = Strings.get(item.titleKey, language)
                    )
                },
                label = {
                    Text(
                        text = Strings.get(item.titleKey, language),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
