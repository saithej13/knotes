package com.vst.knotes.MVVM.Adapter

import ScreenItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import com.vst.knotes.R
import com.vst.knotes.databinding.LayoutScreenOnboardingBinding
import com.vst.knotes.databinding.OnboardingBinding

class IntroViewPagerAdapter(
    private val mContext: Context,
    private val mListScreen: List<ScreenItem>
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(mContext)
        val binding = LayoutScreenOnboardingBinding.inflate(inflater, container, false)

        binding.introTitle.text = mListScreen[position].title
        binding.introDescription.text = mListScreen[position].description
//        binding.introImg.setImageResource(mListScreen[position].screenImg)
        Picasso.get()
            .load(mListScreen[position].screenImgPath) // Ensure this is a valid image URL
//            .placeholder(R.drawable.placeholder) // Optional: Show while loading
            .error(R.drawable.fab) // Optional: Show on error
            .into(binding.introImg)

        container.addView(binding.root)
        return binding.root
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}