package pl.edu.uwr.studenthardlife

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

open class TasksListAdapter(private val context: Context, private val tasksList: MutableList<Task>)
    : RecyclerView.Adapter<TasksListAdapter.TasksListViewHolder>() {

    fun refresh(refreshList: MutableList<Task>){
        tasksList.clear()
        tasksList.addAll(refreshList)
        notifyItemInserted(refreshList.size)
    }

    //Refs to layout
    class TasksListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val subject: TextView = view.findViewById(R.id.task_subject)
        val title: TextView = view.findViewById(R.id.task_title)
        val deadline: TextView = view.findViewById(R.id.task_deadline)
    }

    //Work on refs to layout
    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int){
        val currentTask = tasksList[position]

        holder.title.text = currentTask.Title
        holder.subject.text = currentTask.Subject
        holder.deadline.text = currentTask.Deadline

        holder.itemView.setOnClickListener{ view ->
            val bundle = bundleOf("ID" to currentTask.ID)

            view.findNavController().navigate(R.id.detailFragment, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder{
        return TasksListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.tasks_list_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = tasksList.size
}