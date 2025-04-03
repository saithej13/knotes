package com.vst.knotes.MVVM.View

import ScreenItem
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
import com.vst.knotes.MVVM.Adapter.IntroViewPagerAdapter
import com.vst.knotes.R
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.Utils.MyApplication
import com.vst.knotes.Utils.Preference
import com.vst.knotes.databinding.LoginBinding
import com.vst.knotes.databinding.OnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: OnboardingBinding
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var preference: Preference
    private lateinit var screenItemList: MutableList<ScreenItem>
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenItemList = mutableListOf()
        preference = Preference(applicationContext)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='tblonboarding'"
                sqLiteDatabase.rawQuery(checkTableQuery, null).use { cursor ->
                    if (cursor.count == 0) {
                        Log.e("SQLITE", "Table tblonboarding does NOT exist!")
                        return@let
                    }
                }

                val query = "SELECT * FROM tblonboarding"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            screenItemList.add(
                                ScreenItem(
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4),
                                    0
                                )
                            )
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblonboarding is EMPTY!")
                    }
                    Log.e("tblonboarding", ""+screenItemList.size)
                }
            }

        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
//        val mList = mutableListOf(
//            ScreenItem(
//                "Add Farmers / Customers",
//                "We conduct rigorous quality testing to ensure that the milk we deliver is of the highest quality. Our team of experts conducts 26 tests every day to ensure that the milk is pure, fresh, and meets the highest standards of quality.",
//                "",
//                R.drawable.img1
//            ),
//            ScreenItem(
//                "Collect / Sale Milk",
//                "We offer hassle-free doorstep delivery of fresh milk every day, providing you with added convenience and ensuring that you never run out of milk.",
//                "",
//                R.drawable.img2
//            ),
//            ScreenItem(
//                "Easy Managing Reports",
//                "We offer personalized subscription plans that are tailored to your individual needs. With our flexible plans, you can easily manage your milk delivery schedule and customize your orders to ensure that you always have fresh milk on hand. Our user-friendly mobile app makes it easy to adjust your subscription plan, skip deliveries, or make any necessary changes to your order.",
//                "",
//                R.drawable.img3
//            )
//        )

        // setup viewpager
        introViewPagerAdapter = IntroViewPagerAdapter(this, screenItemList)
        binding.screenViewpager.adapter = introViewPagerAdapter

        // setup tablayout with viewpager
        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)

        // Next button click listener
        binding.btnNext.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < screenItemList.size - 1) {
                position++
                binding.screenViewpager.currentItem = position
            }
            if (position == screenItemList.size - 1) { // when we reach the last screen
                loadLastScreen()
            }
        }

        // TabLayout add change listener
        binding.tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == screenItemList.size - 1) {
                    loadLastScreen()
                }
                else{
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnGetStarted.visibility = View.INVISIBLE
                    binding.tvSkip.visibility = View.VISIBLE
                    binding.tabIndicator.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Get Started button click listener
        binding.btnGetStarted.setOnClickListener {
//            startActivity(Intent(this, Login::class.java))
            startActivity(Intent(this,BaseActivity::class.java))
            savePrefsData()
            finish()
        }

        // Skip button click listener
        binding.tvSkip.setOnClickListener { binding.screenViewpager.currentItem = screenItemList.size - 1 }
    }

    private fun loadLastScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnGetStarted.visibility = View.VISIBLE
        binding.tvSkip.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE

        // Add animation to the Get Started button
        binding.btnGetStarted.animation =
            AnimationUtils.loadAnimation(this, R.anim.button_animation)
    }

    private fun savePrefsData() {
        preference.saveBooleanInPreference("isIntroOpened", true)
        preference.commitPreference()
    }

    private fun restorePrefData(): Boolean {
        return preference.getBooleanFromPreference("isIntroOpened", false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
