package com.amigojolive.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.amigojolive.ui.components.AmigojoButton
import com.amigojolive.ui.components.AmigojoTextField
import com.amigojolive.ui.components.AmigojoSnackbarHost

@Composable
fun RegisterScreenContent(viewModel: AuthViewModel) {
    val navigator   = LocalNavigator.currentOrThrow
    val state       by viewModel.state.collectAsState()
    val snackbar    = remember { SnackbarHostState() }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var fullName    by remember { mutableStateOf("") }
    var area        by remember { mutableStateOf("") }

    val sent = state.navigateTo == NavigationTarget.REGISTER_SUCCESS

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.clearError() }
    }

    if (sent) {
        Scaffold { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("✓ Solicitud enviada", style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("Un administrador revisará tu solicitud y activará tu cuenta.",
                    style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = { viewModel.clearNavigationTarget(); navigator.pop() }) {
                    Text("Volver al inicio de sesión")
                }
            }
        }
        return
    }

    Scaffold(snackbarHost = { AmigojoSnackbarHost(snackbar) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Solicitar acceso", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            AmigojoTextField(fullName,  { fullName = it },  "Nombre completo")
            Spacer(Modifier.height(12.dp))
            AmigojoTextField(area,      { area = it },      "Área / departamento")
            Spacer(Modifier.height(12.dp))
            AmigojoTextField(email,     { email = it },     "Correo electrónico")
            Spacer(Modifier.height(12.dp))
            AmigojoTextField(password,  { password = it },  "Contraseña", isPassword = true)
            Spacer(Modifier.height(24.dp))
            AmigojoButton(
                text = "Enviar solicitud",
                loading = state.loading,
                onClick = { viewModel.registerRequest(email, password, fullName, area) },
            )
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { navigator.pop() }) { Text("Cancelar") }
        }
    }
}
