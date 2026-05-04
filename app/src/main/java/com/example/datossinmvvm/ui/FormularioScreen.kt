package com.example.datossinmvvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datossinmvvm.data.Urgencia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: IncidenteViewModel,
    onVolver: () -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()

    var categoriaSeleccionada by remember(categorias) {
        mutableStateOf(categorias.firstOrNull())
    }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var urgencia by remember { mutableStateOf(Urgencia.MEDIA) }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var errorDescripcion by remember { mutableStateOf(false) }
    var errorUbicacion by remember { mutableStateOf(false) }
    var errorCategoria by remember { mutableStateOf(false) }

    // Actualizar categoría seleccionada cuando cargan las categorías
    LaunchedEffect(categorias) {
        if (categoriaSeleccionada == null && categorias.isNotEmpty()) {
            categoriaSeleccionada = categorias.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo incidente", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Selector de categoría
            Column {
                Text("Tipo de incidente", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), modifier = Modifier.padding(bottom = 8.dp))

                if (categorias.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF8E1))
                            .padding(16.dp)
                    ) {
                        Text(
                            "No hay categorías. Créalas desde el ícono 🏷 en la pantalla principal.",
                            fontSize = 13.sp,
                            color = Color(0xFFF57F17)
                        )
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = categoriaExpanded,
                        onExpandedChange = { categoriaExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = categoriaSeleccionada?.let { "${it.emoji}  ${it.nombre}" } ?: "Selecciona un tipo",
                            onValueChange = {},
                            readOnly = true,
                            isError = errorCategoria,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoriaExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoriaExpanded,
                            onDismissRequest = { categoriaExpanded = false }
                        ) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text("${cat.emoji}  ${cat.nombre}") },
                                    onClick = {
                                        categoriaSeleccionada = cat
                                        errorCategoria = false
                                        categoriaExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Descripción
            Column {
                Text("Descripción", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { if (it.length <= 200) { descripcion = it; errorDescripcion = false } },
                    placeholder = { Text("Describe el problema...", color = Color.LightGray) },
                    isError = errorDescripcion,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    supportingText = {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if (errorDescripcion) Text("Campo requerido", color = MaterialTheme.colorScheme.error)
                            else Spacer(Modifier.weight(1f))
                            Text("${descripcion.length}/200", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                )
            }

            // Ubicación
            Column {
                Text("Ubicación (calle / referencia)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { if (it.length <= 100) { ubicacion = it; errorUbicacion = false } },
                    placeholder = { Text("Ej: Av. Los Pinos con Jr. Sucre", color = Color.LightGray) },
                    isError = errorUbicacion,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    supportingText = {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if (errorUbicacion) Text("Campo requerido", color = MaterialTheme.colorScheme.error)
                            else Spacer(Modifier.weight(1f))
                            Text("${ubicacion.length}/100", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                )
            }

            // Urgencia visual
            Column {
                Text("Urgencia", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), modifier = Modifier.padding(bottom = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Urgencia.entries.forEach { u ->
                        val selected = urgencia == u
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) urgenciaBgColor(u) else Color.White)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) urgenciaTextColor(u) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { urgencia = u }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(urgenciaIcon(u), fontSize = 22.sp, color = urgenciaTextColor(u), fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(u.label, fontSize = 13.sp, color = urgenciaTextColor(u), fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    errorCategoria = categoriaSeleccionada == null
                    errorDescripcion = descripcion.isBlank()
                    errorUbicacion = ubicacion.isBlank()
                    if (!errorCategoria && !errorDescripcion && !errorUbicacion) {
                        viewModel.registrarIncidente(
                            categoriaId = categoriaSeleccionada!!.id,
                            descripcion = descripcion.trim(),
                            ubicacion = ubicacion.trim(),
                            urgencia = urgencia
                        )
                        onVolver()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Guardar incidente", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
