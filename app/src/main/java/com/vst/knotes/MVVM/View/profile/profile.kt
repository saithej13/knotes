package com.vst.knotes.MVVM.View.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.AddCategory
import com.vst.knotes.MVVM.View.category.Category
import com.vst.knotes.R
import com.vst.knotes.databinding.ProfileBinding

class profile : BaseFragment<ProfileBinding>() {
    var IsActive : Boolean = false;
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): ProfileBinding {
        return ProfileBinding.inflate(inflater, parent, false)
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
        binding.btnprofile.setOnClickListener {
            if (IsActive)
                context?.let { stopVibration(it) }
            else
                context?.let { startVibration(it) }
//            val bitmap: Bitmap = loadSampleImage()  // Replace with your actual image loading method
//            (activity as? BaseActivity)?.runInference(bitmap)

//            (activity as? BaseActivity)?.replaceFragment(Category::class.java.newInstance() as Fragment)


//            (activity as? BaseActivity)?.showCustomDialog(
//                title = "Delete Item",
//                message = "Are you sure you want to delete this item?",
//                onYesClick = {
//                    // Handle Yes action
//                    Toast.makeText(requireContext(), "Item Deleted", Toast.LENGTH_SHORT).show()
//                },
//                onNoClick = {
//                    // Handle No action
//                    Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show()
//                }
//            )
        }
    }

    fun startVibration(context: Context) {
//        val startIntent = Intent(context, VibrationService::class.java)
//        startIntent.putExtra("action", "start")
//        context.startService(startIntent)
    }

    // Function to stop the vibration service
    fun stopVibration(context: Context) {
//        val stopIntent = Intent(context, VibrationService::class.java)
//        stopIntent.putExtra("action", "stop")
//        context.startService(stopIntent)
    }
//    fun loadSampleImage(): Bitmap {
//        val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.sample_img_model) }
//        if (drawable is BitmapDrawable) {
//            return drawable.bitmap
//        } else {
//            throw IllegalArgumentException("Drawable resource is not a BitmapDrawable.")
//        }
//    }

}