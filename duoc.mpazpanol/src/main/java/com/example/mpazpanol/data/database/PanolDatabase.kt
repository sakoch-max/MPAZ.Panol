package com.example.mpazpanol.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpazpanol.data.dao.EquipoDao
import com.example.mpazpanol.data.model.Equipo

/**
 * Room Database singleton for MPAZ Pañol.
 */
@Database(entities = [Equipo::class], version = 1, exportSchema = false)
abstract class PanolDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao

    companion object {
        @Volatile
        private var INSTANCE: PanolDatabase? = null

        fun getDatabase(context: Context): PanolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PanolDatabase::class.java,
                    "panol_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
