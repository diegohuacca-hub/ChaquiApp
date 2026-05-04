// data/Incidente.kt
package com.example.datossinmvvm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoIncidente(val label: String) {
    HUECO("Hueco"),
    SEMAFORO("Semáforo dañado"),
    DERRUMBE("Derrumbe"),
    INUNDACION("Inundación")
}

enum class Urgencia(val label: String, val orden: Int) {
    BAJA("Baja", 3),
    MEDIA("Media", 2),
    ALTA("Alta", 1)
}

enum class EstadoIncidente(val label: String) {
    REPORTADO("Reportado"),
    EN_ATENCION("En atención"),
    RESUELTO("Resuelto")
}

@Entity(tableName = "incidentes")
data class Incidente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: TipoIncidente,
    val descripcion: String,
    val ubicacion: String,
    val urgencia: Urgencia,
    val estado: EstadoIncidente = EstadoIncidente.REPORTADO,
    val fechaReporte: Long = System.currentTimeMillis()
)
