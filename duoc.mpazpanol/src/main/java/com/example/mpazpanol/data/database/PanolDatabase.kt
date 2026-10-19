package com.example.mpazpanol.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mpazpanol.data.dao.EquipoDao
import com.example.mpazpanol.data.dao.TicketDao
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.utils.InventarioMock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database singleton for MPAZ Pañol.
 */
// Agregamos Ticket: class a las entidades de la base de datos
@Database(entities = [Equipo::class, Ticket::class], version = 1, exportSchema = false)
abstract class PanolDatabase : RoomDatabase() {

    abstract fun EquipoDao(): EquipoDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: PanolDatabase? = null

        fun getDatabase(context: Context): PanolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PanolDatabase::class.java,
                    "panol_database"
                )

                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Esto evita que la pantalla se congele mientras se cargan los datos
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = INSTANCE?.EquipoDao()
                                InventarioMock.equiposIniciales.forEach { equipo ->
                                    dao?.insertEquipo(equipo)
                                }
                            }
                        }
                    })

                    .build() // El build() ahora queda al final

                INSTANCE = instance
                instance
            }
        }
    }
}