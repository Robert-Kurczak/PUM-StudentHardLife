package pl.edu.uwr.studenthardlife

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

object ImageData{
    fun getBytes(bitmap: Bitmap): ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun getImage(image: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
}

class MainActivity : AppCompatActivity(){
    companion object MainDatabase{
        @Volatile
        private var INSTANCE: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TasksDatabase::class.java,
                    "TasksDatabase"
                ).allowMainThreadQueries().build().also { INSTANCE = it }
                instance
            }
        }
    }

    private lateinit var dbDao: TasksDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbDao = getDatabase(this).TasksDao()

        if(dbDao.selectAll().isEmpty()){
            val testImageBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.android)

            lifecycleScope.launch {
                val exampleTasks = List(5) {
                    Task(
                        0,
                        "Subject #$it",
                        "Title #$it",
                        "Content #$it",
                        (it % 20 + 1).toString() + ".1.1970",
                        ImageData.getBytes(testImageBitmap)
                    )
                }

                dbDao.insertAll(exampleTasks)
            }
        }

    }
}