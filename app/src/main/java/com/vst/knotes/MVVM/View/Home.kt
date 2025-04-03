package com.vst.knotes.MVVM.View

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vst.knotes.MVVM.View.Dashboard.dashboard
import com.vst.knotes.MVVM.View.profile.profile
import com.vst.knotes.R
import com.vst.knotes.databinding.HomeFragmentBinding

class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, parent, false)
    }

    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View? {
        return binding.root
    }


}



