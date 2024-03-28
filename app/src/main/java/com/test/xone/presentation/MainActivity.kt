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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.test.xone.R
import com.test.xone.databinding.ActivityMainBinding
import com.test.xone.di.ViewModelFactory
import com.test.xone.domain.ImageEntity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
    private lateinit var adapter: ImageItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObservable()
        setEditText()
        initImagePickerLauncher()
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
        binding.buttonDelete.setOnClickListener {
            adapter.currentList.forEach {
                if (it.isChecked) {
                    viewModel.deleteImage(it)
                }
            }
            viewModel.setLoadingState()
        }
    }

    private fun setObservable() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.list.collect {
                    viewModel.setContentState(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is State.Content -> {
                            binding.buttonDelete.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            initRecyclerView(false)
                            onBackPressedDispatcher.addCallback {
                                finish()
                            }
                            adapter.submitList(state.currencyList)
                            adapter.onClickListener = { item, _ ->
                                showImage(item.imageUri)
                            }
                            adapter.onLongClickListener = {
                                it.isChecked = true
                                viewModel.setDeleteState(adapter.currentList)
                            }
                        }


                        is State.Deleting -> {
                            binding.buttonDelete.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            onBackPressedDispatcher.addCallback {
                                val list = state.currencyList
                                    .map { it.copy(isChecked = false) }
                                viewModel.setContentState(list)
                            }
                            initRecyclerView(true)
                            adapter.submitList(state.currencyList)
                            adapter.onClickListener = { item, position ->
                                item.isChecked = !item.isChecked
                                adapter.notifyItemChanged(position)
                            }
                            adapter.onLongClickListener = {
                                onBackPressedDispatcher.onBackPressed()
                            }
                        }


                        State.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
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

        adapter = ImageItemListAdapter(this@MainActivity, deleteMode)
        binding.recyclerViewImages.adapter = adapter
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