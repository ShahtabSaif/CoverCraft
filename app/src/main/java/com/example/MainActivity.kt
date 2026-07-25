package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.SavedPagesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CoverPageViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CoverPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val iconSelected: Any, val iconUnselected: Any) {
    object Editor : Screen("editor", "Editor", Icons.Default.Edit, Icons.Outlined.Edit)
    object Presets : Screen("presets", "Presets", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Library : Screen("library", "My Pages", Icons.Default.Folder, Icons.Outlined.Folder)
    object Profiles : Screen("profiles", "Profiles", Icons.Default.Person, Icons.Outlined.Person)
}

@Composable
fun MainAppScreen(viewModel: CoverPageViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(
        Screen.Editor,
        Screen.Presets,
        Screen.Library,
        Screen.Profiles
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.iconSelected as androidx.compose.ui.graphics.vector.ImageVector else screen.iconUnselected as androidx.compose.ui.graphics.vector.ImageVector,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Editor.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Editor.route) {
                EditorScreen(
                    viewModel = viewModel,
                    onNavigateToLibrary = {
                        navController.navigate(Screen.Library.route)
                    }
                )
            }
            composable(Screen.Presets.route) {
                PresetsScreen(
                    viewModel = viewModel,
                    onNavigateToEditor = {
                        navController.navigate(Screen.Editor.route)
                    }
                )
            }
            composable(Screen.Library.route) {
                SavedPagesScreen(
                    viewModel = viewModel,
                    onNavigateToEditor = {
                        navController.navigate(Screen.Editor.route)
                    }
                )
            }
            composable(Screen.Profiles.route) {
                ProfilesScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
