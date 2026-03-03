package dev.parcelview.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.parcelview.feature.parcels.ParcelDetail
import dev.parcelview.feature.parcels.ParcelsList
import dev.parcelview.feature.parcels.impl.ParcelDetailScreen
import dev.parcelview.feature.parcels.impl.ParcelsListScreen
import dev.parcelview.feature.scanner.Scanner
import dev.parcelview.feature.scanner.impl.ScannerScreen
import dev.parcelview.feature.settings.Settings
import dev.parcelview.feature.settings.impl.SettingsScreen

private enum class Tab { PARCELS, SCANNER, SETTINGS }

@Composable
fun App() {
    MaterialTheme {
        val backStack = remember { mutableStateListOf<Any>(ParcelsList) }
        val selectedTab = remember { mutableStateOf(Tab.PARCELS) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab.value == Tab.PARCELS,
                        onClick = {
                            selectedTab.value = Tab.PARCELS
                            backStack.clear()
                            backStack.add(ParcelsList)
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Parcels") },
                        label = { Text("Parcels") }
                    )
                    NavigationBarItem(
                        selected = selectedTab.value == Tab.SCANNER,
                        onClick = {
                            selectedTab.value = Tab.SCANNER
                            backStack.clear()
                            backStack.add(Scanner)
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Scanner") },
                        label = { Text("Scanner") }
                    )
                    NavigationBarItem(
                        selected = selectedTab.value == Tab.SETTINGS,
                        onClick = {
                            selectedTab.value = Tab.SETTINGS
                            backStack.clear()
                            backStack.add(Settings)
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is ParcelsList -> NavEntry(key) {
                            ParcelsListScreen(
                                onParcelClick = { trackingId ->
                                    backStack.add(ParcelDetail(trackingId))
                                }
                            )
                        }
                        is ParcelDetail -> NavEntry(key) {
                            ParcelDetailScreen(
                                trackingId = key.trackingId,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                        is Scanner -> NavEntry(key) {
                            ScannerScreen()
                        }
                        is Settings -> NavEntry(key) {
                            SettingsScreen()
                        }
                        else -> error("Unknown route: $key")
                    }
                }
            )
        }
    }
}
