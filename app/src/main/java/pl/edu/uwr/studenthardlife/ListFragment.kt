package pl.edu.uwr.studenthardlife

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?){
        super.onViewCreated(itemView, savedInstanceState)

        val databaseDAO = MainActivity.getDatabase(itemView.context).TasksDao()
        val tasksAdapter = TasksListAdapter(
            itemView.context,
            MainActivity.getDatabase(itemView.context).TasksDao().selectAll() as MutableList<Task>
        )

        val rv = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        rv.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(itemView.context)
        }

        val addTaskFAB: View = itemView.findViewById(R.id.add_task_FAB)
        addTaskFAB.setOnClickListener{
            val testImageBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.android)

            val newTask = Task(
                0,
                "Subject",
                "Title",
                "Content",
                "Deadline",
                ImageData.getBytes(testImageBitmap)
            )

            lifecycleScope.launch {
                databaseDAO.insert(newTask)
            }

//            rv.adapter?.notifyDataSetChanged()
//            tasksAdapter.notifyItemInserted(tasksAdapter.itemCount)
//            itemView.findNavController().navigate(R.id.listFragment)
            (rv.adapter as TasksListAdapter).refresh(databaseDAO.selectAll() as MutableList<Task>)
        }
    }
}