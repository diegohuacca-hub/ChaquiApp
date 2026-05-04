package com.example.datossinmvvm.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Categoria ya no es enum — es una entidad Room
@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val emoji: String = "⚠"
)

enum class Urgencia(val label: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta")
}

enum class EstadoIncidente(val label: String) {
    REPORTADO("Reportado"),
    EN_ATENCION("En atención"),
    RESUELTO("Resuelto")
}

@Entity(
    tableName = "incidentes",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoriaId")]
)
data class Incidente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoriaId: Int,
    val descripcion: String,
    val ubicacion: String,
    val urgencia: Urgencia,
    val estado: EstadoIncidente = EstadoIncidente.REPORTADO,
    val fechaReporte: Long = System.currentTimeMillis()
)

// Data class para join Incidente + Categoria (usado en UI)
data class IncidenteConCategoria(
    val incidente: Incidente,
    val categoria: Categoria
)
