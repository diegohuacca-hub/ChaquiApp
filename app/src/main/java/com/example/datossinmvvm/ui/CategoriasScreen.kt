package com.example.datossinmvvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datossinmvvm.data.Categoria

// Emojis disponibles para elegir
val EMOJIS_DISPONIBLES = listOf(
    "⚠", "🚦", "⛰", "🌧", "🔥", "💧", "🚧", "🛑",
    "⚡", "🌊", "🏚", "🌉", "🛤", "🚨", "🔩", "🪨"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: IncidenteViewModel,
    onVolver: () -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()

    var mostrarDialog by remember { mutableStateOf(false) }
    var mostrarErrorDialog by remember { mutableStateOf(false) }
    var nombreNueva by remember { mutableStateOf("") }
    var emojiNuevo by remember { mutableStateOf("⚠") }
    var errorNombre by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva categoría", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        if (categorias.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏷", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Sin categorías aún", color = Color.Gray, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { mostrarDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Crear primera categoría")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${categorias.size} categorías disponibles",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(categorias, key = { it.id }) { categoria ->
                    CategoriaItem(
                        categoria = categoria,
                        onEliminar = {
                            viewModel.eliminarCategoria(categoria) {
                                mostrarErrorDialog = true
                            }
                        }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // Dialog crear categoría
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialog = false
                nombreNueva = ""
                emojiNuevo = "⚠"
                errorNombre = false
            },
            title = { Text("Nueva categoría", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Campo nombre
                    OutlinedTextField(
                        value = nombreNueva,
                        onValueChange = { nombreNueva = it; errorNombre = false },
                        label = { Text("Nombre de la categoría") },
                        placeholder = { Text("Ej: Poste caído") },
                        isError = errorNombre,
                        supportingText = { if (errorNombre) Text("El nombre es requerido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary
                        )
                    )

                    // Selector de emoji
                    Text("Elige un ícono:", fontSize = 13.sp, color = Color.Gray)
                    EmojiGrid(
                        emojis = EMOJIS_DISPONIBLES,
                        seleccionado = emojiNuevo,
                        onSeleccionar = { emojiNuevo = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreNueva.isBlank()) {
                            errorNombre = true
                        } else {
                            viewModel.crearCategoria(nombreNueva, emojiNuevo)
                            mostrarDialog = false
                            nombreNueva = ""
                            emojiNuevo = "⚠"
                            errorNombre = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialog = false
                    nombreNueva = ""
                    emojiNuevo = "⚠"
                    errorNombre = false
                }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog error al eliminar
    if (mostrarErrorDialog) {
        AlertDialog(
            onDismissRequest = { mostrarErrorDialog = false },
            title = { Text("No se puede eliminar") },
            text = { Text("Esta categoría tiene incidentes asociados. Primero elimina o reasigna esos incidentes.") },
            confirmButton = {
                TextButton(onClick = { mostrarErrorDialog = false }) {
                    Text("Entendido", color = PurplePrimary)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CategoriaItem(categoria: Categoria, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEF0FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(categoria.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                categoria.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmojiGrid(emojis: List<String>, seleccionado: String, onSeleccionar: (String) -> Unit) {
    val rows = emojis.chunked(8)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { emoji ->
                    val isSelected = emoji == seleccionado
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFEEF0FF) else Color(0xFFF5F5F5))
                            .then(
                                if (isSelected) Modifier.padding(1.dp) else Modifier
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFE8E6FF) else Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { onSeleccionar(emoji) },
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(emoji, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
