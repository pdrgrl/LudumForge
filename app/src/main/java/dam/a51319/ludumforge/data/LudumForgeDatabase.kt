package dam.a51319.ludumforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dam.a51319.ludumforge.data.daos.*
import dam.a51319.ludumforge.models.*

@Database(
    entities = [Project::class, Task::class, User::class, Team::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // Registers our Date and List converters
abstract class LudumForgeDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao

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
                    .fallbackToDestructiveMigration() // Useful during early development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}