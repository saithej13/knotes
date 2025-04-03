package com.vst.knotes.Utils

import android.util.Log

class StringUtils {
    companion object {
        fun getInt(str: String): Int {
            try {
                return str.toInt()
            } catch (e: Exception) {
            }
            return getIntSlow(str)
        }
        fun getIntSlow(str: String?): Int {
            var str = str
            var value = 0

            if (str == null || str.equals("", ignoreCase = true) || str.contains("T") || str.equals(
                    "null",
                    ignoreCase = true
                ) || str.contains(":")
            ) return value

            str = str.replace(",", "")

            if (str.contains(".")) return getFloat(str) as Int

            try {
                value = str.toInt()
            } catch (e: java.lang.Exception) {
                value = getFloat(str) as Int
                Log.d("StringUtils", "Error occurred while parsing as integer$e")
            }
            return value
        }

        fun getFloat(string: String): Float {
            try {
                return string.toFloat()
            } catch (e: java.lang.Exception) {
            }

            return getFloatSlow(string)
        }
        fun getFloatSlow(string: String?): Float {
            var string = string
            var value = 0f

            if (string == null || string.equals("", ignoreCase = true) || string.equals(
                    ".",
                    ignoreCase = true
                ) || string.equals("null", ignoreCase = true) || string.contains("T")
            ) return value

            string = string.replace(",", "")

            try {
                value = string.toFloat()
            } catch (e: java.lang.Exception) {
                Log.d("StringUtils", "Error occurred while getFloat$e")
            }

            return value
        }
    }
}