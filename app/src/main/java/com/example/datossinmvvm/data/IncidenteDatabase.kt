package com.example.datossinmvvm.data

import android.content.Context
import androidx.room.*
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Incidente::class, Categoria::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class IncidenteDatabase : RoomDatabase() {

    abstract fun incidenteDao(): IncidenteDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: IncidenteDatabase? = null

        fun getDatabase(context: Context): IncidenteDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    IncidenteDatabase::class.java,
                    "chaqui_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insertar categorías por defecto al crear la BD
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.categoriaDao()
                                    dao.insertar(Categoria(nombre = "Hueco en pista", emoji = "⚠"))
                                    dao.insertar(Categoria(nombre = "Semáforo dañado", emoji = "🚦"))
                                    dao.insertar(Categoria(nombre = "Derrumbe", emoji = "⛰"))
                                    dao.insertar(Categoria(nombre = "Inundación", emoji = "🌧"))
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }
    }
}