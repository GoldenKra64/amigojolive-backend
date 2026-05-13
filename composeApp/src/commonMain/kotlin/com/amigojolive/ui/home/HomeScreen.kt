package com.amigojolive.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.amigojolive.domain.model.UserSummary
import com.amigojolive.navigation.*
import com.amigojolive.ui.components.LoadingOverlay
import com.amigojolive.ui.components.SectionCard

/**
 * Pantalla de inicio del Panel del Docente.
 * Muestra métricas derivadas (publicaciones propias, distribución por etiquetas)
 * más accesos directos a las secciones principales.
 * No incluye accesos a /admin/* — la barra de docente está aislada del flujo admin.
 */
@Composable
fun TeacherHomeContent(viewModel: HomeViewModel, currentUser: UserSummary, onLogout: () -> Unit = {}) {
    val navigator = LocalNavigator.currentOrThrow
    val state     by viewModel.state.collectAsState()

    if (state.loading) { LoadingOverlay(); return }

    TeacherScaffold(
        currentUser = currentUser,
        onLogout = onLogout,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    "Hola, ${currentUser.profile?.fullName ?: currentUser.email}",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    "Panel del Docente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Mis publicaciones",
                        value = state.totalPublications.toString(),
                        icon = Icons.Default.Article,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Etiquetas usadas",
                        value = state.tagDistribution.size.toString(),
                        icon = Icons.Default.Tag,
                    )
                }
            }

            if (state.tagDistribution.isNotEmpty()) {
                item {
                    SectionCard("Distribución por etiqueta") {
                        state.tagDistribution.entries.take(5).forEach { (tag, count) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(tag, style = MaterialTheme.typography.bodyMedium)
                                Text("$count pub.", style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            LinearProgressIndicator(
                                progress = { count.toFloat() / (state.totalPublications.coerceAtLeast(1)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            item {
                SectionCard("Accesos rápidos") {
                    QuickAccessButton(Icons.Default.Add,        "Nueva publicación") { navigator.push(CreateEditPublicationScreen()) }
                    QuickAccessButton(Icons.Default.Feed,       "Feed comunitario")  { navigator.push(FeedScreen) }
                    QuickAccessButton(Icons.Default.Folder,     "Mis aportes")       { navigator.push(MyPublicationsScreen) }
                    QuickAccessButton(Icons.Default.SmartToy,   "Asistente")         { navigator.push(ChatbotScreen) }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickAccessButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ── Scaffold del docente con Bottom Navigation Bar ────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScaffold(
    currentUser: UserSummary,
    onLogout: () -> Unit,
    content: @Composable () -> Unit,
) {
    val navigator   = LocalNavigator.currentOrThrow
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AmigojoLive") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            onClick = { menuExpanded = false; navigator.push(ProfileScreen) },
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            leadingIcon = { Icon(Icons.Default.Logout, null) },
                            onClick = { menuExpanded = false; onLogout() },
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navigator.push(TeacherHomeScreen) },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigator.push(FeedScreen) },
                    icon = { Icon(Icons.Default.Feed, null) },
                    label = { Text("Feed") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigator.push(ChatbotScreen) },
                    icon = { Icon(Icons.Default.SmartToy, null) },
                    label = { Text("Asistente") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigator.push(ProfileScreen) },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                )
            }
        },
        content = { padding ->
            Box(Modifier.padding(padding)) { content() }
        },
    )
}
