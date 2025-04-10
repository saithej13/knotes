package com.vst.knotes.MVVM.View

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.vst.knotes.MVVM.View.Dashboard.dashboard
import com.vst.knotes.MVVM.View.menu.menu
import com.vst.knotes.MVVM.View.profile.profile
import com.vst.knotes.MVVM.View.support.support
import com.vst.knotes.R

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var datePickerDialog: DatePickerDialog? = null
    private val CHANNEL_ID: String = "my_channel"
    private val NOTIFICATION_ID: Int = 1
    private var notificationManager: NotificationManager? = null
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    val STOREID="1"
    val ZONEID="1"

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = inflateBinding(inflater, parent) // Assign `_binding` before use
        notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        createNotificationChannel()

        return provideYourFragmentView(inflater, parent, savedInstanceState, viewLifecycleOwner) ?: binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

    abstract fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): VB

    abstract fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View?

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "My Channel Description"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
    override fun onResume() {
        super.onResume()
        if (activity is BaseActivity) {
            val mainActivity = activity as BaseActivity
            if (this is dashboard || this is menu || this is profile|| this is support) {
                mainActivity.showBottomNavigation()
            } else {
                mainActivity.hideBottomNavigation()
            }
        }
    }


    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment) // Replace with your container ID
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }



}
