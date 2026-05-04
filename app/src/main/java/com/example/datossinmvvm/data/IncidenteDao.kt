// data/IncidenteDao.kt
package com.example.datossinmvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenteDao {

    // Todos los incidentes ordenados por urgencia (Alta primero)
    @Query("SELECT * FROM incidentes ORDER BY CASE urgencia WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END ASC")
    fun getAll(): Flow<List<Incidente>>

    // Filtrados por tipo, mismo orden por urgencia
    @Query("SELECT * FROM incidentes WHERE tipo = :tipo ORDER BY CASE urgencia WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END ASC")
    fun getByTipo(tipo: String): Flow<List<Incidente>>

    @Query("SELECT * FROM incidentes WHERE id = :id")
    fun getById(id: Int): Flow<Incidente?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(incidente: Incidente)

    @Update
    suspend fun actualizar(incidente: Incidente)

    @Delete
    suspend fun eliminar(incidente: Incidente)
}
