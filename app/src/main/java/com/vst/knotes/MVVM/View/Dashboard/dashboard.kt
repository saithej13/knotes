package com.vst.knotes.MVVM.View.Dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.databinding.DashboardScreenBinding


class dashboard : BaseFragment<DashboardScreenBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): DashboardScreenBinding {
        return DashboardScreenBinding.inflate(inflater, parent, false)
    }
    override fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}