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

    private val db = IncidenteDatabase.getDatabase(application)
    private val incidenteDao = db.incidenteDao()
    private val categoriaDao = db.categoriaDao()

    // Filtro activo por categoría (null = todos)
    private val _filtroCategoria = MutableStateFlow<Int?>(null)
    val filtroCategoria: StateFlow<Int?> = _filtroCategoria.asStateFlow()

    // Lista de categorías disponibles
    val categorias: StateFlow<List<Categoria>> = categoriaDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Lista de incidentes filtrada reactivamente
    val incidentes: StateFlow<List<Incidente>> = _filtroCategoria
        .flatMapLatest { categoriaId ->
            if (categoriaId == null) incidenteDao.getAll()
            else incidenteDao.getByCategoriaId(categoriaId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFiltro(categoriaId: Int?) {
        _filtroCategoria.value = categoriaId
    }

    fun getIncidenteById(id: Int): Flow<Incidente?> = incidenteDao.getById(id)

    fun registrarIncidente(
        categoriaId: Int,
        descripcion: String,
        ubicacion: String,
        urgencia: Urgencia
    ) {
        viewModelScope.launch {
            incidenteDao.insertar(
                Incidente(
                    categoriaId = categoriaId,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    urgencia = urgencia
                )
            )
        }
    }

    fun avanzarEstado(incidente: Incidente) {
        val nuevoEstado = when (incidente.estado) {
            EstadoIncidente.REPORTADO -> EstadoIncidente.EN_ATENCION
            EstadoIncidente.EN_ATENCION -> EstadoIncidente.RESUELTO
            EstadoIncidente.RESUELTO -> EstadoIncidente.RESUELTO
        }
        viewModelScope.launch {
            incidenteDao.actualizar(incidente.copy(estado = nuevoEstado))
        }
    }

    // Crear nueva categoría personalizada
    fun crearCategoria(nombre: String, emoji: String) {
        viewModelScope.launch {
            categoriaDao.insertar(Categoria(nombre = nombre.trim(), emoji = emoji))
        }
    }

    // Eliminar categoría (solo si no tiene incidentes asociados)
    fun eliminarCategoria(categoria: Categoria, onError: () -> Unit) {
        viewModelScope.launch {
            val count = categoriaDao.contarIncidentes(categoria.id)
            if (count > 0) {
                onError()
            } else {
                categoriaDao.eliminar(categoria)
            }
        }
    }
}
