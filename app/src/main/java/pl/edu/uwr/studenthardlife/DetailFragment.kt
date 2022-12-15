package pl.edu.uwr.studenthardlife

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.launch


class DetailFragment : Fragment() {
    private lateinit var image: ImageButton

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it){ launchCamera() }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            image.setImageBitmap(imageBitmap)
        }
    }

    private fun openCamera(){
        when {
            ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> {
            launchCamera()
        }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA) -> {
                showMessageOKCancel("Allow access")
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCamera.launch(intent)
    }

    private fun showMessageOKCancel(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseDAO = MainActivity.getDatabase(requireContext()).TasksDao()
        val taskID = arguments?.getInt("ID")!!
        val task = databaseDAO.selectByID(taskID)

        val subject: TextView = view.findViewById(R.id.task_subject_details)
        val deadline: TextView = view.findViewById(R.id.task_deadline_details)
        val title: TextView = view.findViewById(R.id.task_title_details)
        val content: TextView = view.findViewById(R.id.task_content_details)
        image = view.findViewById(R.id.task_image_details)

        subject.text = task.Subject
        deadline.text = task.Deadline
        title.text = task.Title
        content.text = task.Content
        image.setImageBitmap(ImageData.getImage(task.Image))

        val saveButton: Button = view.findViewById(R.id.save_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)

        image.setOnClickListener{
            openCamera()
        }

        saveButton.setOnClickListener{
            val updatedTask = Task(
                taskID,
                subject.text.toString(),
                title.text.toString(),
                content.text.toString(),
                deadline.text.toString(),
                ImageData.getBytes(image.drawable.toBitmap())
            )

            lifecycleScope.launch{
                databaseDAO.update(updatedTask)
            }

            view.findNavController().navigate(R.id.listFragment)
        }

        deleteButton.setOnClickListener{
            lifecycleScope.launch{
                databaseDAO.deleteByID(taskID)
            }

            view.findNavController().navigate(R.id.listFragment)
        }
    }
}