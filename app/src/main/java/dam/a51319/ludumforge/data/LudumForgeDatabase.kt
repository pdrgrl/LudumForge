package dam.a51319.ludumforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dam.a51319.ludumforge.data.daos.ActionLogDao

@Database(entities = [ActionLog::class], version = 1, exportSchema = false)
abstract class LudumForgeDatabase : RoomDatabase() {

    abstract fun actionLogDao(): ActionLogDao

    companion object {
        @Volatile
        private var INSTANCE: LudumForgeDatabase? = null

        fun getDatabase(context: Context): LudumForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LudumForgeDatabase::class.java,
                    "ludumforge_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}