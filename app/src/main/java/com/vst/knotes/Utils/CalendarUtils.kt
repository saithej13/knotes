package com.vst.knotes.Utils

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarUtils {
    companion object {
        val DATE_STD_PATTERN: String = "yyyy-MM-dd"
        fun getOrderSummaryDate(year: Int, month: Int, day: Int): String {
            var month = month
            month = month + 1
            val date = (if (year < 10) "0$year" else year).toString() + "-" +
                    (if ((month) < 10) "0$month" else (month)) + "-" +
                    (if (day < 10) "0$day" else day)

            return date
        }
        fun getFormatedDatefromString(strDate: String): String? {
            var strDate = strDate
            var formatedDate: String? = "N/A"
            if (!TextUtils.isEmpty(strDate)) {
                try {
                    formatedDate = strDate

                    if (strDate.contains(" ")) strDate = strDate.replace(" ", "T")

                    if (strDate.contains("T")) {
                        val arrDate = strDate.split("T".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0].split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        formatedDate =
                            (arrDate[2] + " " + getMonthFromNumber(StringUtils.getInt(arrDate[1]))).toString() + ", " + arrDate[0]
                    } else {
                        val arrDate = strDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        formatedDate =
                            (arrDate[2] + " " + getMonthFromNumber(StringUtils.getInt(arrDate[1]))).toString() + ", " + arrDate[0]
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    formatedDate = null
                }
            }
            return formatedDate
        }
        fun getMonthFromNumber(intMonth: Int): String {
            var strMonth = ""

            when (intMonth) {
                1 -> strMonth = "Jan"
                2 -> strMonth = "Feb"
                3 -> strMonth = "Mar"
                4 -> strMonth = "Apr"
                5 -> strMonth = "May"
                6 -> strMonth = "Jun"
                7 -> strMonth = "Jul"
                8 -> strMonth = "Aug"
                9 -> strMonth = "Sep"
                10 -> strMonth = "Oct"
                11 -> strMonth = "Nov"
                12 -> strMonth = "Dec"
            }
            return strMonth
        }

        fun getDiffBtwDatesInDays(startDate: String?, endDate: String?): Int {
            val calendar1: Calendar = Calendar.getInstance()
            val calendar2: Calendar = Calendar.getInstance()

            calendar1.setTime(
                CalendarUtils.getDateFromString(
                    startDate,
                    CalendarUtils.DATE_STD_PATTERN
                )
            )
            calendar2.setTime(
                CalendarUtils.getDateFromString(
                    endDate,
                    CalendarUtils.DATE_STD_PATTERN
                )
            )

            val milliseconds1: Long = calendar1.getTimeInMillis()
            val milliseconds2: Long = calendar2.getTimeInMillis()

            val diff = milliseconds2 - milliseconds1
            val diffDays = (diff / (24 * 60 * 60 * 1000)).toInt()

            return diffDays
        }

        fun getDateFromString(date: String?, pattern: String?): Date {
            var dateObj: Date = Date()
            val sdf: SimpleDateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
            try {
                if (!TextUtils.isEmpty(date)) dateObj = sdf.parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateObj
        }
    }



}