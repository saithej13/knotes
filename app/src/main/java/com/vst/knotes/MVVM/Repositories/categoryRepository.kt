package com.vst.knotes.MVVM.Repositories

import android.util.Log
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper

class categoryRepository {
    fun insert(cat:categoryModel){
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtInsert = sqLiteDatabase.compileStatement(
                    "INSERT INTO tblcategory (CATEGORYNAME, CATEGORYIMAGEURL, CATEGORYDESCRIPTION, STOREID, ZONEID) VALUES (?, ?, ?, ?, ?)"
                )
                stmtInsert.bindString(1, cat.categoryname)
                stmtInsert.bindString(2, cat.categoryimageurl)
                stmtInsert.bindString(3, cat.categorydescription)
                stmtInsert.bindString(4, cat.storeid.toString())
                stmtInsert.bindString(5, cat.zoneid.toString())
                stmtInsert.executeInsert()
                stmtInsert.close()

            }

        } catch (e: Exception) {
            Log.e("categoryDA_insert", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }
    fun update(cat:categoryModel){
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtUpdate = sqLiteDatabase.compileStatement(
                    "UPDATE tblcategory SET CATEGORYNAME = ?," +
                            " CATEGORYIMAGEURL =?, CATEGORYDESCRIPTION =? ," +
                            " STOREID = ? , ZONEID =? WHERE CATEGORYID = ?"
                )
                stmtUpdate.bindString(1, cat.categoryname)
                stmtUpdate.bindString(2, cat.categoryimageurl)
                stmtUpdate.bindString(3, cat.categorydescription)
                stmtUpdate.bindString(4, cat.storeid.toString())
                stmtUpdate.bindString(5, cat.zoneid.toString())
                stmtUpdate.bindString(6, cat.categoryid)
                stmtUpdate.executeUpdateDelete()
                stmtUpdate.close()
            }

        } catch (e: Exception) {
            Log.e("categoryDA_update", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun delete(cat:categoryModel){
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtDelete = sqLiteDatabase.compileStatement(
                    "DELETE FROM tblcategory WHERE CATEGORYID = ?"
                )
                stmtDelete.bindString(1, cat.categoryid)
                stmtDelete.executeUpdateDelete()
                stmtDelete.close()
            }
        } catch (e: Exception) {
            Log.e("categoryDA_Delete", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }
    fun getAllCategories(STOREID:String,ZONEID:String):ArrayList<categoryModel>{
        var categories: ArrayList<categoryModel> = ArrayList<categoryModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query = "SELECT categoryid,categoryname,categoryimageurl,categorydescription,storeid,zoneid FROM tblcategory where storeid='"+STOREID+"' and zoneid='"+ZONEID+"'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val categorymodel = categoryModel()
                            categorymodel.categoryid = cursor.getString(0)
                            categorymodel.categoryname = cursor.getString(1)
                            categorymodel.categoryimageurl = cursor.getString(2)
                            categorymodel.categorydescription = cursor.getString(3)
                            categorymodel.storeid = cursor.getInt(4)
                            categorymodel.zoneid = cursor.getInt(5)
                            categories.add(categorymodel)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblcategory is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("categoryDA_getallcategories", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return categories
    }
    fun getCategoryById(categoryId:String):ArrayList<categoryModel>{
        var categories: ArrayList<categoryModel> = ArrayList<categoryModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query = "SELECT categoryid,categoryname,categoryimageurl,categorydescription,storeid,zoneid FROM tblcategory where categoryid='"+categoryId+"'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val categorymodel = categoryModel()
                            categorymodel.categoryid = cursor.getString(0)
                            categorymodel.categoryname = cursor.getString(1)
                            categorymodel.categoryimageurl = cursor.getString(2)
                            categorymodel.categorydescription = cursor.getString(3)
                            categorymodel.storeid = cursor.getInt(4)
                            categorymodel.zoneid = cursor.getInt(5)
                            categories.add(categorymodel)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblcategory is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("categoryDA_getcategorybyid", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return categories
    }
}