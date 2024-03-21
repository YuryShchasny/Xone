package com.test.xone.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.test.xone.R
import com.test.xone.databinding.ActivityMainBinding
import com.test.xone.di.ViewModelFactory
import com.test.xone.domain.ImageEntity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_PREFS = "app_prefs"
        private const val SECTION_PREF = "section_pref"
        private const val LOCATION_PREF = "location_pref"
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val component by lazy {
        (application as MyApplication).component
    }

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setEditText()
        initImagePickerLauncher()
        initRecyclerView()
        setObservable()
        setListeners()

    }

    private fun setEditText() {
        binding.editTextSection.setText(
            getSharedPreferences(APP_PREFS, MODE_PRIVATE).getString(
                SECTION_PREF,
                String()
            )
        )
        binding.editTextLocation.setText(
            getSharedPreferences(APP_PREFS, MODE_PRIVATE).getString(
                LOCATION_PREF,
                String()
            )
        )

    }

    private fun setListeners() {
        binding.imageViewAddImage.setOnClickListener {
            getImageFromGallery()
        }
        binding.editTextSection.addTextChangedListener {
            getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit()
                .putString(SECTION_PREF, it.toString()).apply()
        }

        binding.editTextLocation.addTextChangedListener {
            getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit()
                .putString(LOCATION_PREF, it.toString()).apply()
        }
    }

    private fun setObservable() {
        viewModel.recyclerViewDeleteMode.observe(this) {isDeleteMode ->
            binding.buttonDelete.visibility = if (isDeleteMode) {
                View.VISIBLE
            } else {
                View.GONE
            }
            initRecyclerView(isDeleteMode)
            onBackPressedDispatcher.addCallback {
                if(isDeleteMode) {
                    viewModel.setRecyclerViewMode(false)
                }
                else {
                    finish()
                }
            }
        }
    }


    private fun showImage(uri: String) {
        val showImageDialog = ShowImageDialog(this@MainActivity, uri)
        showImageDialog.setCanceledOnTouchOutside(true)
        showImageDialog.show()
    }

    private fun initRecyclerView(deleteMode: Boolean = false) {
        val screenWidth = resources.displayMetrics.widthPixels
        val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
        val marginWidth = resources.getDimensionPixelSize(R.dimen.margin)
        val spanCount = (screenWidth - marginWidth * 4) / itemWidth
        val layoutManager = GridLayoutManager(this@MainActivity, spanCount)
        binding.recyclerViewImages.layoutManager = layoutManager

        val adapter = ImageItemListAdapter(this@MainActivity, deleteMode)
        binding.recyclerViewImages.adapter = adapter
        adapter.onClickListener = { item, position ->
            if (deleteMode) {
                item.isChecked = !item.isChecked
                adapter.notifyItemChanged(position)
            } else {
                showImage(item.imageUri)
            }
        }
        adapter.onLongClickListener = {
            if (deleteMode) {
                adapter.currentList.forEach { image ->
                    image.isChecked = false
                }
                viewModel.setRecyclerViewMode(false)
            } else {
                it.isChecked = true
                viewModel.setRecyclerViewMode(true)
            }
        }
        viewModel.imageList.observe(this@MainActivity) {
            adapter.submitList(it)
        }
        binding.buttonDelete.setOnClickListener {
            adapter.currentList.forEach {
                if (it.isChecked) {
                    viewModel.deleteImage(it)
                }
            }
            viewModel.setRecyclerViewMode(false)
        }
    }

    private fun initImagePickerLauncher() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        val data: Intent = result.data!!
                        val selectedImages: ArrayList<Uri> = arrayListOf()

                        // When multiple images are selected
                        if (data.clipData != null) {
                            data.clipData?.let {
                                val count = it.itemCount
                                for (i in 0 until count) {
                                    val imageUri = it.getItemAt(i).uri
                                    selectedImages.add(imageUri)
                                }
                            }
                        } else {
                            // If one image is selected
                            val imageUri = data.data
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                        // Processing selected images
                        selectedImages.forEachIndexed { index, uri ->
                            val newUri = copyImageToLocalStorage(uri, index)
                            newUri?.let {
                                viewModel.addImage(it.toString())
                            }
                        }
                    }
                }
            }
    }

    private fun copyImageToLocalStorage(originalUri: Uri, index: Int): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(originalUri)
            val newUriName = ImageEntity.getUniqueName(index)
            val outputStream = FileOutputStream(File(filesDir, newUriName))
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Uri.fromFile(File(filesDir, newUriName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imagePickerLauncher.launch(intent)
    }
}