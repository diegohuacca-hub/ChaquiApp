package com.example.datossinmvvm.ui

import androidx.compose.foundation.background
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
import com.example.datossinmvvm.data.EstadoIncidente
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    incidenteId: Int,
    viewModel: IncidenteViewModel,
    onVolver: () -> Unit
) {
    val incidente by viewModel.getIncidenteById(incidenteId).collectAsState(initial = null)
    val categorias by viewModel.categorias.collectAsState()
    val categoria = categorias.find { it.id == incidente?.categoriaId }
    val catIndex = categorias.indexOf(categoria)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del incidente", fontWeight = FontWeight.Bold, color = Color.White) },
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
        incidente?.let { inc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(categoriaBgColor(catIndex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(categoria?.emoji ?: "⚠", fontSize = 32.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                categoria?.nombre ?: "Sin categoría",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(urgenciaBgColor(inc.urgencia))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "${urgenciaIcon(inc.urgencia)} ${inc.urgencia.label}",
                                        fontSize = 12.sp,
                                        color = urgenciaTextColor(inc.urgencia),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(estadoBgColor(inc.estado))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        inc.estado.label,
                                        fontSize = 12.sp,
                                        color = estadoTextColor(inc.estado),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetalleItem(emoji = "📋", label = "Descripción", valor = inc.descripcion)
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        DetalleItem(emoji = "📍", label = "Ubicación", valor = inc.ubicacion)
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        val sdf = SimpleDateFormat("d 'de' MMMM 'de' yyyy, hh:mm a", Locale("es", "PE"))
                        DetalleItem(emoji = "📅", label = "Fecha de registro", valor = sdf.format(Date(inc.fechaReporte)))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEF0FF)),
                                contentAlignment = Alignment.Center
                            ) { Text("🚩", fontSize = 18.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Estado actual", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1A1A))
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(estadoBgColor(inc.estado)).padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(inc.estado.label, fontSize = 13.sp, color = estadoTextColor(inc.estado), fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    when (inc.estado) {
                                        EstadoIncidente.REPORTADO -> "El incidente ha sido registrado."
                                        EstadoIncidente.EN_ATENCION -> "El incidente está siendo atendido."
                                        EstadoIncidente.RESUELTO -> "El incidente fue resuelto."
                                    },
                                    fontSize = 12.sp, color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (inc.estado != EstadoIncidente.RESUELTO) {
                    Button(
                        onClick = { viewModel.avanzarEstado(inc) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Text("↺  Cambiar estado", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {}, enabled = false,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("✓  Incidente resuelto", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PurplePrimary)
        }
    }
}

@Composable
fun DetalleItem(emoji: String, label: String, valor: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEEF0FF)),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 18.sp) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1A1A))
            Spacer(Modifier.height(3.dp))
            Text(valor, fontSize = 13.sp, color = Color(0xFF555555), lineHeight = 18.sp)
        }
    }
}
