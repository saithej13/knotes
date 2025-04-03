package com.vst.knotes.MVVM.View.DyamicUi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.Components.DynamicUIManager
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.R
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.DynamiclayoutBinding

class DynamicFragment : BaseFragment<DynamiclayoutBinding>() {
    private lateinit var dynamicUIManager: DynamicUIManager

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): DynamiclayoutBinding {
        return DynamiclayoutBinding.inflate(inflater, parent, false)
    }

    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dynamicUIManager = DynamicUIManager(requireContext())

        val screenName = arguments?.getString("SCREEN_NAME") ?: "default_screen"
        val (screenId, parentLayoutType) = getScreenDetails("SCREEN1")

        if (screenId != -1L) {
            val parentContainer = binding.root.findViewById<ViewGroup>(R.id.dynamicContainer)
                ?: return // Exit if the container is null

            val parentLayout: ViewGroup = when (parentLayoutType) {
                "LinearLayout" -> LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                }
                "RelativeLayout" -> RelativeLayout(requireContext())
                else -> LinearLayout(requireContext()) // Default fallback
            }

            parentContainer.addView(parentLayout) // Add parent layout dynamically
            dynamicUIManager.createDynamicScreen(screenId, parentLayout)
        }
    }

    private fun getScreenDetails(screenName: String): Pair<Long, String?> {
        var screenId: Long = 0L
        var parentLayout: String? = null

        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query = """
                    SELECT s.ID, c.COMPONENT_TYPE 
                    FROM TBLSCREENS s
                    LEFT JOIN TBLSCREEN_COMPONENTS c ON s.ID = c.SCREEN_ID 
                    WHERE s.SCREEN_NAME = ?
                    ORDER BY c.ID 
                """
                sqLiteDatabase.rawQuery(query, arrayOf(screenName))?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        screenId = cursor.getLong(0)
                        parentLayout = cursor.getString(1) ?: "LinearLayout"
                    } else {
                        Log.e("SQLITE", "tblscreens is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return Pair(screenId, parentLayout)
    }
}
