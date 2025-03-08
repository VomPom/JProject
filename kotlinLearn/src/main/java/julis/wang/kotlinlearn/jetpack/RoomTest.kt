package julis.wang.kotlinlearn.jetpack

import android.content.Context
import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wang.julis.jwbase.basecompact.IBaseTest
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/03/06 11:28
 *
 * @Description
 */
object RoomTest : IBaseTest() {
    override fun run(context: Context) {
        val db = providesUserDatabase(context)
        val userDao = db.userDao()
        GlobalScope.launch {
            userDao.apply {
                addEntry(User(10, 0, "bob"))
                addEntry(User(11, 1, "mary"))
                addEntry(User(12, 0, "jack"))
            }
            Logger.d("Current users:${userDao.getAllEntries().map { it.name }}")
            userDao.deleteEntry(User(1, 1, "mary"))
            Logger.d("after delete current users:${userDao.getAllEntries().map { it.name }}")
            userDao.updateEntry(User(2, 1, "jack"))
            Logger.d("after update jack current users:${userDao.getAllEntries().map { it.name }}")
        }
    }
}

fun providesUserDatabase(
    appContext: Context,
): UserDatabase {
    return Room.databaseBuilder(
        appContext,
        UserDatabase::class.java,
        UserDatabase.DB_NAME
    ).build()
}

@Database(
    entities = [
        User::class
    ],
    version = 1
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DB_NAME = "UserDatabase"
    }
}

@Dao
interface UserDao {
    @Insert
    fun addEntry(entry: User)

    @Delete
    fun deleteEntry(entry: User)

    @Update
    fun updateEntry(entry: User)

    @Query("SELECT * FROM Users")
    fun getAllEntries(): List<User>
}

@Entity(tableName = "Users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val gender: Int,
    var name: String
) : Parcelable

