package com.vst.knotes.MVVM.View.category

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.R
import com.vst.knotes.Utils.ImageCaptureListener
import com.vst.knotes.Utils.StringUtils
import com.vst.knotes.databinding.AddcategoryBinding
import java.io.File

class AddCategory : BaseFragment<AddcategoryBinding>(), ImageCaptureListener {
    private lateinit var imageView: ImageView
    private lateinit var imgPath: String
    private lateinit var viewModel: categoryVM
//    var categories: ArrayList<categoryModel> = ArrayList<categoryModel>()

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): AddcategoryBinding {
        return AddcategoryBinding.inflate(inflater, parent, false)
    }

    companion object {
        fun newInstance(categoryId: String): AddCategory {
            return AddCategory().apply {
                arguments = Bundle().apply {
                    putString("categoryid", categoryId)
                }
            }
        }
    }

    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = binding.ivImage

        val factory = CategoryViewModelFactory(categoryRepository())
        viewModel = ViewModelProvider(this, factory).get(categoryVM::class.java)

        val categoryId = arguments?.getString("categoryid") ?: ""
        if (categoryId.isNotEmpty()) {
            loadData(categoryId)
        }

        binding.capturecategoryimage.setOnClickListener {
            captureImage()
        }

        // Set up submit button to save or update the category
        binding.tvSubmit.setOnClickListener {
            submit(categoryId)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Set image capture listener to activity
        (activity as BaseActivity).setImageCaptureListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove image capture listener when the fragment is destroyed
        (activity as BaseActivity).removeImageCaptureListener()
    }

    // Capture image functionality
    fun captureImage() {
        // Call the activity to show image selection dialog
        (activity as BaseActivity).showImageSelectionDialog(this@AddCategory)
    }

    fun loadData(categoryId: String) {
        viewModel.fetchCategoryById(categoryId)

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                val category = categories[0]
                binding.categoryName.setText(category.categoryname)
                binding.categoryDescription.setText(category.categorydescription)

                if (!category.categoryimageurl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(File(category.categoryimageurl))
                        .error(R.drawable.fab)
                        .into(imageView)
                    imgPath = category.categoryimageurl
                } else {
                    imageView.setImageResource(R.drawable.fab)
                    imgPath = ""
                }
            }
        }
    }

    fun submit(categoryId: String) {
        val category = categoryModel().apply {
            categoryname = binding.categoryName.text.toString()
            categorydescription = binding.categoryDescription.text.toString()
            categoryimageurl = imgPath
            storeid = StringUtils.getInt(STOREID)
            zoneid = StringUtils.getInt(ZONEID)
        }

        if (categoryId.isNotEmpty()) {
            category.categoryid = categoryId
            viewModel.update(category)
            showCategoryDialog("Category Updated Successfully")
        } else {
            viewModel.insert(category)
            showCategoryDialog("Category Saved Successfully")
        }
    }

    private fun showCategoryDialog(message: String) {
        (activity as BaseActivity).showCustomDialog(
            "Alert", message, "Ok", "", {
                (activity as BaseActivity).supportFragmentManager.popBackStack()
            }, {
                // No click handler (if needed)
            })
    }

    override fun onImageCaptured(bitmap: Bitmap, imagePath: String) {
        imageView.setImageBitmap(bitmap)
        imgPath = imagePath
    }
}