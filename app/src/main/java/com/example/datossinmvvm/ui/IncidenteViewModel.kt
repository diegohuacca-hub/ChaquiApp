// ui/IncidenteViewModel.kt
package com.example.datossinmvvm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.datossinmvvm.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class IncidenteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = IncidenteDatabase.getDatabase(application).incidenteDao()

    // Filtro activo: null = mostrar todos
    private val _filtroTipo = MutableStateFlow<TipoIncidente?>(null)
    val filtroTipo: StateFlow<TipoIncidente?> = _filtroTipo.asStateFlow()

    // Lista reactiva: se recalcula automáticamente cuando cambia el filtro
    val incidentes: StateFlow<List<Incidente>> = _filtroTipo
        .flatMapLatest { tipo ->
            if (tipo == null) dao.getAll()
            else dao.getByTipo(tipo.name)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setFiltro(tipo: TipoIncidente?) {
        _filtroTipo.value = tipo
    }

    fun getIncidenteById(id: Int): Flow<Incidente?> = dao.getById(id)

    fun registrarIncidente(
        tipo: TipoIncidente,
        descripcion: String,
        ubicacion: String,
        urgencia: Urgencia
    ) {
        viewModelScope.launch {
            dao.insertar(
                Incidente(
                    tipo = tipo,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    urgencia = urgencia
                )
            )
        }
    }

    // DECISIÓN TÉCNICA: La lógica de transición de estado vive aquí,
    // no en el Composable. El Composable solo llama a esta función.
    fun avanzarEstado(incidente: Incidente) {
        val nuevoEstado = when (incidente.estado) {
            EstadoIncidente.REPORTADO -> EstadoIncidente.EN_ATENCION
            EstadoIncidente.EN_ATENCION -> EstadoIncidente.RESUELTO
            EstadoIncidente.RESUELTO -> EstadoIncidente.RESUELTO // ya no avanza
        }
        viewModelScope.launch {
            dao.actualizar(incidente.copy(estado = nuevoEstado))
        }
    }
}
