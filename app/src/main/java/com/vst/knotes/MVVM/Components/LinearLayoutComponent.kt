package com.vst.knotes.MVVM.Components

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout

class LinearLayoutComponent (private val context: Context) {
    fun createLinearLayout(properties: Map<String, String>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = if (properties["ORIENTATION"] == "vertical") LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                if (properties["LAYOUT_WIDTH"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                if (properties["LAYOUT_HEIGHT"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}