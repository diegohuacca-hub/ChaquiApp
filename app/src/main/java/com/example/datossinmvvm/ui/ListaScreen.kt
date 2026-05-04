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

// Colores del diseño
val PurplePrimary = Color(0xFF4A3F8F)
val PurpleLight = Color(0xFFF0EEFF)

// Colores por tipo de incidente
fun tipoColor(tipo: TipoIncidente): Color = when (tipo) {
    TipoIncidente.HUECO -> Color(0xFFFFEBEB)
    TipoIncidente.SEMAFORO -> Color(0xFFFFF8E1)
    TipoIncidente.DERRUMBE -> Color(0xFFE8F5E9)
    TipoIncidente.INUNDACION -> Color(0xFFE3F2FD)
}

fun tipoIconColor(tipo: TipoIncidente): Color = when (tipo) {
    TipoIncidente.HUECO -> Color(0xFFE53935)
    TipoIncidente.SEMAFORO -> Color(0xFFF9A825)
    TipoIncidente.DERRUMBE -> Color(0xFF43A047)
    TipoIncidente.INUNDACION -> Color(0xFF1E88E5)
}

fun tipoEmoji(tipo: TipoIncidente): String = when (tipo) {
    TipoIncidente.HUECO -> "⚠"
    TipoIncidente.SEMAFORO -> "🚦"
    TipoIncidente.DERRUMBE -> "⛰"
    TipoIncidente.INUNDACION -> "🌧"
}

// Colores de urgencia
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

// Colores de estado
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: IncidenteViewModel,
    onNuevoIncidente: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    val incidentes by viewModel.incidentes.collectAsState()
    val filtroActivo by viewModel.filtroTipo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Incidentes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Text("⚡", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurplePrimary
                )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    "Filtrar por tipo",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FiltroChip(
                            label = "Todos",
                            selected = filtroActivo == null,
                            onClick = { viewModel.setFiltro(null) }
                        )
                    }
                    items(TipoIncidente.entries) { tipo ->
                        FiltroChip(
                            label = tipo.label,
                            selected = filtroActivo == tipo,
                            onClick = { viewModel.setFiltro(tipo) }
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
                        IncidenteCard(incidente = incidente, onClick = { onVerDetalle(incidente.id) })
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
fun IncidenteCard(incidente: Incidente, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono tipo
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tipoColor(incidente.tipo)),
                contentAlignment = Alignment.Center
            ) {
                Text(tipoEmoji(incidente.tipo), fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = incidente.tipo.label,
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
                    // Chip urgencia
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(urgenciaBgColor(incidente.urgencia))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${urgenciaIcon(incidente.urgencia)} ${incidente.urgencia.label}",
                            fontSize = 11.sp,
                            color = urgenciaTextColor(incidente.urgencia),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    // Chip estado
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(estadoBgColor(incidente.estado))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = incidente.estado.label,
                            fontSize = 11.sp,
                            color = estadoTextColor(incidente.estado),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
