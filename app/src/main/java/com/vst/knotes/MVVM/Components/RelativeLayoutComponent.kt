package com.vst.knotes.MVVM.Components

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout

class RelativeLayoutComponent(private val context: Context) {
    fun createRelativeLayout(properties: Map<String, String>): RelativeLayout {
        return RelativeLayout(context).apply {
            gravity = if (properties["GRAVITY"] == "center") RelativeLayout.CENTER_IN_PARENT else RelativeLayout.ALIGN_LEFT
            layoutParams = RelativeLayout.LayoutParams(
                if (properties["LAYOUT_WIDTH"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                if (properties["LAYOUT_HEIGHT"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}