package com.vst.knotes.MVVM.View.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.category.Category
import com.vst.knotes.databinding.ProfileBinding
import com.vst.knotes.databinding.SupportBinding

class support : BaseFragment<SupportBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): SupportBinding {
        return SupportBinding.inflate(inflater, parent, false)
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


    }
}