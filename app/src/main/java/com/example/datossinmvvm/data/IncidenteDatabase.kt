// data/IncidenteDatabase.kt
package com.example.datossinmvvm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Incidente::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class IncidenteDatabase : RoomDatabase() {

    abstract fun incidenteDao(): IncidenteDao

    companion object {
        @Volatile
        private var INSTANCE: IncidenteDatabase? = null

        fun getDatabase(context: Context): IncidenteDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    IncidenteDatabase::class.java,
                    "chaqui_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
