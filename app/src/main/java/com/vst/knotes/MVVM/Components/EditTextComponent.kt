package com.vst.knotes.MVVM.Components

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout

class EditTextComponent(private val context: Context) {
    fun createEditText(properties: Map<String, String>): EditText {
        return EditText(context).apply {
            hint = properties["HINT"] ?: "Enter text"
            setTextColor(Color.parseColor(properties["TEXTVIEWCOLOR"] ?: "#000000"))
            textSize = properties["TEXTVIEWSIZE"]?.replace("sp", "")?.toFloat() ?: 16f
            layoutParams = LinearLayout.LayoutParams(
                if (properties["LAYOUT_WIDTH"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                if (properties["LAYOUT_HEIGHT"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            )
            visibility = if (properties["VISIBILITY"] == "GONE") View.GONE else View.VISIBLE
        }
    }
}