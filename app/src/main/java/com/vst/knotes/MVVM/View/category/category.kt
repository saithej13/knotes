package com.vst.knotes.MVVM.View.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.Repositories.categoryRepository
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.CategoryBinding

class Category : BaseFragment<CategoryBinding>() {

    private lateinit var viewModel: categoryVM
    private var categories: ArrayList<categoryModel> = ArrayList()

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): CategoryBinding {
        return CategoryBinding.inflate(inflater, parent, false)
    }

    companion object {
        fun newInstance(): Category {
            return Category()
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

        val factory = CategoryViewModelFactory(categoryRepository())
        viewModel = ViewModelProvider(this, factory).get(categoryVM::class.java)

        viewModel.categories.observe(viewLifecycleOwner, { categoriesList ->
            categories = categoriesList as ArrayList<categoryModel>
            setupRecyclerView()
        })

        binding.fab.setOnClickListener {
            (activity as? BaseActivity)?.replaceFragment(AddCategory.newInstance("") as Fragment)
        }

        try {
            viewModel.fetchAllCategories(STOREID, ZONEID)
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        if (categories.isNotEmpty()) {
            binding.recyclerview.adapter = categoryadapter(
                requireContext(),
                categories,
                object : categoryadapter.EditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        Log.d("onEditIconClick", "$position")
                        (activity as BaseActivity).replaceFragment(AddCategory.newInstance(position.toString()) as Fragment)
                    }
                },
                object : categoryadapter.DeleteIconClickListener {
                    override fun onDeleteIconClick(position: Int, name: String) {
                        Log.d("onDeleteIconClick", "$position")
                        (activity as BaseActivity).showCustomDialog(
                            "Alert",
                            "Do You Want to Delete Category $name?",
                            "Yes",
                            "No",
                            {
                                val categoryModel = categoryModel().apply {
                                    categoryid = position.toString()
                                }
                                viewModel.delete(categoryModel)
                            },
                            {
                                // No clicked, just dismiss
                            }
                        )
                    }
                })
            binding.recyclerview.visibility = View.VISIBLE
            binding.nodata.visibility = View.GONE
        } else {
            binding.recyclerview.visibility = View.GONE
            binding.nodata.visibility = View.VISIBLE
        }
    }
}