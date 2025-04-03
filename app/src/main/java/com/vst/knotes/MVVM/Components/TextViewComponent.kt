package com.vst.knotes.MVVM.Components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class TextViewComponent(private val context: Context) {
    fun createTextView(properties: Map<String, String>): TextView {
        return TextView(context).apply {
            text = properties["TEXTVIEWNAME"] ?: "Default Text"
            setTextColor(Color.parseColor(properties["TEXTVIEWCOLOR"] ?: "#000000"))
            textSize = properties["TEXTVIEWSIZE"]?.replace("sp", "")?.toFloat() ?: 16f
            gravity = parseGravity(properties["GRAVITY"] ?: "center")
            setTypeface(typeface, (properties["TEXTSTYLE"]?:Typeface.BOLD) as Int)//Typeface.BOLD
            layoutParams = LinearLayout.LayoutParams(
                if (properties["LAYOUT_WIDTH"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                if (properties["LAYOUT_HEIGHT"] == "match_parent") ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = parseGravity(properties["LAYOUT_GRAVITY"] ?: "center")
            }
            visibility = if (properties["VISIBILITY"] == "GONE") View.GONE else View.VISIBLE
        }
    }
    private fun parseGravity(gravity: String): Int {
        return when (gravity.lowercase()) {
            "center" -> Gravity.CENTER
            "left" -> Gravity.LEFT
            "right" -> Gravity.RIGHT
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            "start" -> Gravity.START
            "end" -> Gravity.END
            else -> Gravity.CENTER // Default value
        }
    }
}