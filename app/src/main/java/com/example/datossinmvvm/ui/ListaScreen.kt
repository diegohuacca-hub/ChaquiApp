package com.example.datossinmvvm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datossinmvvm.data.*

val PurplePrimary = Color(0xFF4A3F8F)

fun urgenciaBgColor(urgencia: Urgencia): Color = when (urgencia) {
    Urgencia.ALTA -> Color(0xFFFFEBEE)
    Urgencia.MEDIA -> Color(0xFFFFFDE7)
    Urgencia.BAJA -> Color(0xFFE8F5E9)
}

fun urgenciaTextColor(urgencia: Urgencia): Color = when (urgencia) {
    Urgencia.ALTA -> Color(0xFFD32F2F)
    Urgencia.MEDIA -> Color(0xFFF57F17)
    Urgencia.BAJA -> Color(0xFF388E3C)
}

fun urgenciaIcon(urgencia: Urgencia): String = when (urgencia) {
    Urgencia.ALTA -> "↑"
    Urgencia.MEDIA -> "="
    Urgencia.BAJA -> "↓"
}

fun estadoBgColor(estado: EstadoIncidente): Color = when (estado) {
    EstadoIncidente.REPORTADO -> Color(0xFFFFEBEE)
    EstadoIncidente.EN_ATENCION -> Color(0xFFE3F2FD)
    EstadoIncidente.RESUELTO -> Color(0xFFE8F5E9)
}

fun estadoTextColor(estado: EstadoIncidente): Color = when (estado) {
    EstadoIncidente.REPORTADO -> Color(0xFFD32F2F)
    EstadoIncidente.EN_ATENCION -> Color(0xFF1565C0)
    EstadoIncidente.RESUELTO -> Color(0xFF2E7D32)
}

// Color de fondo para el ícono según índice de categoría
val iconBgColors = listOf(
    Color(0xFFFFEBEB), Color(0xFFFFF8E1), Color(0xFFE8F5E9),
    Color(0xFFE3F2FD), Color(0xFFF3E5F5), Color(0xFFE0F7FA),
    Color(0xFFFCE4EC), Color(0xFFE8EAF6)
)

fun categoriaBgColor(index: Int): Color = iconBgColors[index % iconBgColors.size]

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: IncidenteViewModel,
    onNuevoIncidente: () -> Unit,
    onVerDetalle: (Int) -> Unit,
    onGestionarCategorias: () -> Unit
) {
    val incidentes by viewModel.incidentes.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val filtroActivo by viewModel.filtroCategoria.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Incidentes", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                },
                actions = {
                    IconButton(onClick = onGestionarCategorias) {
                        Text("🏷", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoIncidente,
                containerColor = PurplePrimary,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Chips de filtro por categoría
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("Filtrar por tipo", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FiltroChip(label = "Todos", selected = filtroActivo == null, onClick = { viewModel.setFiltro(null) })
                    }
                    items(categorias) { cat ->
                        FiltroChip(
                            label = "${cat.emoji} ${cat.nombre}",
                            selected = filtroActivo == cat.id,
                            onClick = { viewModel.setFiltro(cat.id) }
                        )
                    }
                }
            }

            if (incidentes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Sin incidentes reportados", color = Color.Gray, fontSize = 15.sp)
                        Text("Toca + para agregar uno", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(incidentes, key = { it.id }) { incidente ->
                        val categoria = categorias.find { it.id == incidente.categoriaId }
                        val catIndex = categorias.indexOf(categoria)
                        IncidenteCard(
                            incidente = incidente,
                            categoria = categoria,
                            categoriaIndex = catIndex,
                            onClick = { onVerDetalle(incidente.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun FiltroChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) PurplePrimary else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (selected) Color.White else Color(0xFF444444),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun IncidenteCard(
    incidente: Incidente,
    categoria: Categoria?,
    categoriaIndex: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoriaBgColor(categoriaIndex)),
                contentAlignment = Alignment.Center
            ) {
                Text(categoria?.emoji ?: "⚠", fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoria?.nombre ?: "Sin categoría",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = incidente.ubicacion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(urgenciaBgColor(incidente.urgencia))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "${urgenciaIcon(incidente.urgencia)} ${incidente.urgencia.label}",
                            fontSize = 11.sp,
                            color = urgenciaTextColor(incidente.urgencia),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(estadoBgColor(incidente.estado))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            incidente.estado.label,
                            fontSize = 11.sp,
                            color = estadoTextColor(incidente.estado),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}