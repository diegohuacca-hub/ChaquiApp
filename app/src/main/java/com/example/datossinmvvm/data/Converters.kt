package com.example.datossinmvvm.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromUrgencia(value: String): Urgencia = Urgencia.valueOf(value)

    @TypeConverter
    fun urgenciaToString(urgencia: Urgencia): String = urgencia.name

    @TypeConverter
    fun fromEstado(value: String): EstadoIncidente = EstadoIncidente.valueOf(value)

    @TypeConverter
    fun estadoToString(estado: EstadoIncidente): String = estado.name
}
