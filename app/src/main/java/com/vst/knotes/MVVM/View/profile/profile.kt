package com.vst.knotes.MVVM.View.profile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.databinding.MyAccountBinding

class profile : BaseFragment<MyAccountBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): MyAccountBinding {
        return MyAccountBinding.inflate(inflater, parent, false)
    }

    override fun provideYourFragmentView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?, viewLifecycleOwner: LifecycleOwner): View? {
        return binding.root
    }


    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = "Your Limit to hold cash is exceeded. Your Account will be suspended until you pay the due. You will not receive any new order request from now. <u><font color='#2196F3'>Pay the Due</font></u>"
        binding.messageTv.text = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.attentionLL.setOnClickListener{ binding.attentionCV.visibility = View.GONE }

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabView = TextView(requireContext()).apply {
                text = when (position) {
                    0 -> "Payment History"
                    1 -> "Wallet Provided Earning"
                    else -> ""
                }
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
//                setPadding(16, 8, 16, 8)
            }
            tab.customView = tabView
        }.attach()
        tabLayout.setSelectedTabIndicatorHeight(8) // in pixels

    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PaymentHistoryFragment()
                1 -> WalletProvidedEarningFragment()
                else -> throw IllegalStateException("Invalid position")
            }
        }
    }

}