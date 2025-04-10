package com.vst.knotes.Utils

import android.content.Context
import android.content.SharedPreferences

const val MilkApplicationCheck: String = "MilkApplicationCheck"
const val ISSQLITEDOWNLOADED: String = "ISSQLITEDOWNLOADED"
const val SQLITEFILEPATH : String = "SQLITEFILEPATH"

class Preference(context: Context?) {

    private val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private val edit: SharedPreferences.Editor? = sharedPreferences?.edit()

    fun saveStringInPreference(strKey: String, strValue: String) {
        edit?.putString(strKey, strValue)?.apply()
    }

    fun saveIntInPreference(strKey: String, value: Int) {
        edit?.putInt(strKey, value)?.apply()
    }

    fun saveBooleanInPreference(strKey: String, value: Boolean) {
        edit?.putBoolean(strKey, value)?.apply()
    }
    fun commitPreference() {
        edit?.apply()
    }

    fun removeFromPreference(strKey: String) {
        edit?.remove(strKey)?.apply()
    }

    fun getStringFromPreference(strKey: String, defaultValue: String): String {
        return sharedPreferences?.getString(strKey, defaultValue) ?: defaultValue
    }

    fun getBooleanFromPreference(strKey: String, defaultValue: Boolean): Boolean {
        return sharedPreferences?.getBoolean(strKey, defaultValue) ?: defaultValue
    }

    fun getIntFromPreference(strKey: String, defaultValue: Int): Int {
        return sharedPreferences?.getInt(strKey, defaultValue) ?: defaultValue
    }

    fun clearAllPreferences() {
        edit?.clear()?.apply()
    }
}




/*
class Preference(private val context: Context?) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val edit: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveStringInPreference(strKey: String, strValue: String) {
        edit.putString(strKey, strValue)
    }

    fun saveIntInPreference(strKey: String, value: Int) {
        edit.putInt(strKey, value)
    }

    fun saveBooleanInPreference(strKey: String, value: Boolean) {
        edit.putBoolean(strKey, value)
    }

    fun removeFromPreference(strKey: String) {
        edit.remove(strKey)
    }

    fun commitPreference() {
        edit.apply()
    }

    fun getStringFromPreference(strKey: String, defaultValue: String): String {
        return sharedPreferences.getString(strKey, defaultValue) ?: defaultValue
    }

    fun getBooleanFromPreference(strKey: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(strKey, defaultValue)
    }

    fun getIntFromPreference(strKey: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(strKey, defaultValue)
    }
}*/
