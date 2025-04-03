package com.vst.knotes.MVVM.Components

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.product.AddProduct
import com.vst.knotes.MVVM.View.product.product
import com.vst.knotes.MVVM.View.product.productadapter
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper

class DynamicUIManager(private val context: Context) {
    private val textViewComponent = TextViewComponent(context)
    private val editTextComponent = EditTextComponent(context)
    private val linearLayoutComponent = LinearLayoutComponent(context)
    private val relativeLayoutComponent = RelativeLayoutComponent(context)

    fun getScreenComponents(screenId: Long): List<Map<String, String>> {
        val components = mutableListOf<Map<String, String>>()
        val componentMap = mutableMapOf<String, MutableList<Map<String, Any>>>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query = """
                SELECT 
                    c.ID AS COMPONENT_ID, c.COMPONENT_TYPE, c.PARENT_ID,
                    -- TextView attributes
                    t.TEXTVIEWID, t.TEXTVIEWNAME, t.TEXTVIEWCOLOR, t.TEXTVIEWSIZE, t.TEXTVIEWSTYLE, t.LAYOUT_WIDTH, t.LAYOUT_HEIGHT, t.VISIBILITY, t.LAYOUT_MARGIN, t.LAYOUT_PADDING, t.LAYOUT_GRAVITY, t.GRAVITY,
                    -- EditText attributes
                    e.EDITTEXTID AS EDITTEXT_ID, e.EDITTEXTNAME AS EDITTEXT_NAME, e.EDITTEXTCOLOR AS EDITTEXT_COLOR, e.EDITTEXTSIZE AS EDITTEXT_SIZE, e.LAYOUT_WIDTH AS EDITTEXT_WIDTH, e.LAYOUT_HEIGHT AS EDITTEXT_HEIGHT, e.HINT AS EDITTEXT_HINT, e.VISIBILITY AS EDITTEXT_VISIBILITY, e.LAYOUT_MARGIN AS EDITTEXT_MARGIN, e.LAYOUT_PADDING AS EDITTEXT_PADDING, e.LAYOUT_GRAVITY AS EDITTEXT_GRAVITY, e.GRAVITY AS EDITTEXT_GRAVITY,
                    -- LinearLayout attributes
                    l.LAYOUT_WIDTH AS LINEAR_WIDTH, l.LAYOUT_HEIGHT AS LINEAR_HEIGHT, l.VISIBILITY AS LINEAR_VISIBILITY, l.ORIENTATION, l.WEIGHT_SUM, l.GRAVITY AS LINEAR_GRAVITY, l.LAYOUT_MARGIN AS LINEAR_MARGIN, l.LAYOUT_PADDING AS LINEAR_PADDING,
                    -- RelativeLayout attributes
                    r.LAYOUT_WIDTH AS RELATIVE_WIDTH, r.LAYOUT_HEIGHT AS RELATIVE_HEIGHT, r.VISIBILITY AS RELATIVE_VISIBILITY, r.LAYOUT_MARGIN AS RELATIVE_MARGIN, r.LAYOUT_PADDING AS RELATIVE_PADDING, r.ALIGN_PARENT_START, r.ALIGN_PARENT_END, r.ALIGN_PARENT_TOP, r.ALIGN_PARENT_BOTTOM, r.CENTER_IN_PARENT, r.CENTER_HORIZONTAL, r.CENTER_VERTICAL,
                    -- RecyclerView attributes
                    rv.RECYCLERVIEWID, rv.LAYOUT_WIDTH AS RECYCLER_WIDTH, rv.LAYOUT_HEIGHT AS RECYCLER_HEIGHT, rv.VISIBILITY AS RECYCLER_VISIBILITY, rv.LAYOUT_MARGIN AS RECYCLER_MARGIN, rv.LAYOUT_PADDING AS RECYCLER_PADDING, rv.LAYOUT_GRAVITY AS RECYCLER_GRAVITY, rv.GRAVITY AS RECYCLER_GRAVITY
                FROM TBLSCREEN_COMPONENTS c
                LEFT JOIN TBLTEXTVIEW t ON c.ID = t.COMPONENT_ID
                LEFT JOIN TBLEDITTEXT e ON c.ID = e.COMPONENT_ID
                LEFT JOIN TBLLINEARLAYOUT l ON c.ID = l.COMPONENT_ID
                LEFT JOIN TBLRELATIVELAYOUT r ON c.ID = r.COMPONENT_ID
                LEFT JOIN TBLRECYCLERVIEW rv ON c.ID = rv.COMPONENT_ID
                WHERE c.SCREEN_ID = ?
            """
                sqLiteDatabase.rawQuery(query, arrayOf(screenId.toString()))?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val component = mutableMapOf<String, String>()
                            val componentId = cursor.getString(0)
                            val parentId = cursor.getString(2) ?: "0"

                            component["COMPONENT_ID"] = componentId
                            component["COMPONENT_TYPE"] = cursor.getString(1) ?: ""
                            component["PARENT_ID"] = parentId
                            component["CHILDREN"] = mutableListOf<Map<String, String>>().toString() // Placeholder for children

                            when (component["COMPONENT_TYPE"]) {
                                "TextView" -> {
                                    component["TEXTVIEWID"] = cursor.getString(3) ?: ""
                                    component["TEXTVIEWNAME"] = cursor.getString(4) ?: ""
                                    component["TEXTVIEWCOLOR"] = cursor.getString(5) ?: "#000000"
                                    component["TEXTVIEWSIZE"] = cursor.getString(6) ?: "16sp"
                                    component["TEXTVIEWSTYLE"] = cursor.getString(7) ?: ""
                                    component["LAYOUT_WIDTH"] = cursor.getString(8) ?: "wrap_content"
                                    component["LAYOUT_HEIGHT"] = cursor.getString(9) ?: "wrap_content"
                                    component["VISIBILITY"] = cursor.getString(10) ?: "VISIBLE"
                                }
                                "EditText" -> {
                                    component["TEXTVIEWID"] = cursor.getString(15) ?: ""
                                    component["TEXTVIEWNAME"] = cursor.getString(16) ?: ""
                                    component["TEXTVIEWCOLOR"] = cursor.getString(17) ?: "#000000"
                                    component["TEXTVIEWSIZE"] = cursor.getString(18) ?: "16sp"
                                    component["HINT"] = cursor.getString(21) ?: ""
                                    component["VISIBILITY"] = cursor.getString(22) ?: "VISIBLE"
                                }
                                "LinearLayout" -> {
                                    component["LAYOUT_WIDTH"] = cursor.getString(27) ?: "match_parent"
                                    component["LAYOUT_HEIGHT"] = cursor.getString(28) ?: "wrap_content"
                                    component["ORIENTATION"] = cursor.getString(30) ?: "vertical"
                                }
                                "RelativeLayout" -> {
                                    component["LAYOUT_WIDTH"] = cursor.getString(35) ?: "match_parent"
                                    component["LAYOUT_HEIGHT"] = cursor.getString(36) ?: "wrap_content"
                                }
                                "RecyclerView" -> {
                                    component["LAYOUT_WIDTH"] = cursor.getString(45) ?: "match_parent"
                                    component["LAYOUT_HEIGHT"] = cursor.getString(46) ?: "wrap_content"
                                }
                            }

                            // Store components in a map for easy lookup
                            componentMap[componentId] = componentMap.getOrDefault(componentId, mutableListOf())
                            componentMap[parentId] = componentMap.getOrDefault(parentId, mutableListOf())
                            componentMap[parentId]?.add(component)

                            // If it's a root component (no parent), add it to the main list
                            if (parentId == "0") {
                                components.add(component)
                            }
                        } while (cursor.moveToNext())
                    } else {
                        Log.e("SQLITE", "TBLSCREEN_COMPONENTS is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }

        return buildHierarchy(components, componentMap)
    }

    fun buildHierarchy(parents: List<Map<String, String>>, componentMap: Map<String, MutableList<Map<String, Any>>>): List<Map<String, String>> {
        return parents.map { parent ->
            val componentId = parent["COMPONENT_ID"] ?: ""
            val children = componentMap[componentId] ?: mutableListOf()

            parent.toMutableMap().apply {
                this["CHILDREN"] = children.toString() // Convert child components to a String representation
            }
        }
    }

    fun createDynamicScreen(screenId: Long, parentLayout: ViewGroup) {
        val components = getScreenComponents(screenId)
        val componentMap = mutableMapOf<String, ViewGroup>()
        for (component in components) {
            val componentId = component["COMPONENT_ID"] ?: continue  // Ensure non-null ID
            val view: ViewGroup? = when (component["COMPONENT_TYPE"]) {
                "LinearLayout" -> linearLayoutComponent.createLinearLayout(component)
                "RelativeLayout" -> relativeLayoutComponent.createRelativeLayout(component)
                else -> null
            }
            if (view != null) {
                componentMap[componentId] = view
                parentLayout.addView(view)
            }
        }
        for (component in components) {
            val childView: View? = when (component["COMPONENT_TYPE"]) {
                "TextView" -> textViewComponent.createTextView(component)
                "EditText" -> editTextComponent.createEditText(component)
                else -> null
            }
            val parentId = component["PARENT_ID"]
            if (childView != null && parentId != "0" && componentMap.containsKey(parentId)) {
                componentMap[parentId]?.addView(childView)
            } else if (childView != null) {
                parentLayout.addView(childView)
            }
        }
    }
}