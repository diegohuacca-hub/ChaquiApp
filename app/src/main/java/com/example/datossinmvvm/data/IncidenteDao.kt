package com.example.datossinmvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun getAll(): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: Categoria)

    @Delete
    suspend fun eliminar(categoria: Categoria)

    @Query("SELECT COUNT(*) FROM incidentes WHERE categoriaId = :categoriaId")
    suspend fun contarIncidentes(categoriaId: Int): Int
}

@Dao
interface IncidenteDao {

    @Query("""
        SELECT * FROM incidentes 
        ORDER BY CASE urgencia 
            WHEN 'ALTA' THEN 1 
            WHEN 'MEDIA' THEN 2 
            ELSE 3 
        END ASC
    """)
    fun getAll(): Flow<List<Incidente>>

    @Query("""
        SELECT * FROM incidentes 
        WHERE categoriaId = :categoriaId
        ORDER BY CASE urgencia 
            WHEN 'ALTA' THEN 1 
            WHEN 'MEDIA' THEN 2 
            ELSE 3 
        END ASC
    """)
    fun getByCategoriaId(categoriaId: Int): Flow<List<Incidente>>

    @Query("SELECT * FROM incidentes WHERE id = :id")
    fun getById(id: Int): Flow<Incidente?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(incidente: Incidente)

    @Update
    suspend fun actualizar(incidente: Incidente)
}