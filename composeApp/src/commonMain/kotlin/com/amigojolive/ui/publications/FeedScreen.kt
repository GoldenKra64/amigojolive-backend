package com.amigojolive.ui.publications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.amigojolive.domain.model.Publication
import com.amigojolive.navigation.CreateEditPublicationScreen
import com.amigojolive.navigation.PublicationDetailScreen
import com.amigojolive.ui.components.AmigojoSnackbarHost
import com.amigojolive.ui.components.LoadingOverlay

/** Feed comunitario: todas las publicaciones. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(viewModel: PublicationsViewModel) {
    val navigator = LocalNavigator.currentOrThrow
    val state     by viewModel.state.collectAsState()
    val snackbar  = remember { SnackbarHostState() }

    LaunchedEffect(state.error, state.actionSuccess) {
        (state.error ?: state.actionSuccess)?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Feed comunitario") }, navigationIcon = {
            IconButton(onClick = { navigator.pop() }) { Icon(Icons.Default.ArrowBack, null) }
        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.push(CreateEditPublicationScreen()) }) {
                Icon(Icons.Default.Add, "Nueva publicación")
            }
        },
        snackbarHost = { AmigojoSnackbarHost(snackbar) },
    ) { padding ->
        if (state.loading) { LoadingOverlay(Modifier.padding(padding)); return@Scaffold }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.publications, key = { it.id }) { pub ->
                PublicationCard(pub, onClick = { navigator.push(PublicationDetailScreen(pub.id)) })
            }
        }
    }
}

/** Mis aportes: filtrando por author.id == currentUserId */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPublicationsContent(viewModel: PublicationsViewModel) {
    val navigator = LocalNavigator.currentOrThrow
    val state     by viewModel.state.collectAsState()
    val myPubs    = viewModel.myPublications()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis aportes") }, navigationIcon = {
            IconButton(onClick = { navigator.pop() }) { Icon(Icons.Default.ArrowBack, null) }
        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.push(CreateEditPublicationScreen()) }) {
                Icon(Icons.Default.Add, "Nueva publicación")
            }
        },
    ) { padding ->
        if (state.loading) { LoadingOverlay(Modifier.padding(padding)); return@Scaffold }
        if (myPubs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no tienes publicaciones.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(myPubs, key = { it.id }) { pub ->
                PublicationCard(pub, onClick = { navigator.push(PublicationDetailScreen(pub.id)) })
            }
        }
    }
}

@Composable
fun PublicationCard(pub: Publication, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(pub.title, style = MaterialTheme.typography.titleMedium, maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(pub.content, style = MaterialTheme.typography.bodySmall, maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (pub.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pub.tags.take(3).forEach { tag ->
                        FilterChip(selected = false, onClick = {}, label = { Text(tag.name) })
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                val authorName = if (pub.isAnonymous) "Anónimo"
                else pub.author?.profile?.fullName ?: pub.author?.role ?: "Docente"
                Text(authorName, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
