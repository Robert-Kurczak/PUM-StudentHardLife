package pl.edu.uwr.studenthardlife

import androidx.room.*

//---Tables---
@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo val Subject: String,
    @ColumnInfo val Title: String,
    @ColumnInfo val Content: String,
    @ColumnInfo val Deadline: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val Image: ByteArray
)
//------

//---DAOs---
@Dao
interface TasksDao {
    @Insert
    suspend fun insert(task: Task)

    @Insert
    suspend fun insertAll(tasksList: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM Tasks WHERE ID = :ID")
    suspend fun deleteByID(ID: Int)

    @Query("SELECT * FROM Tasks WHERE ID = :ID")
    fun selectByID(ID: Int): Task

    @Query("SELECT * FROM Tasks")
    fun selectAll(): List<Task>
}
//------

@Database(entities = [Task::class], version = 1)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun TasksDao(): TasksDao
}
