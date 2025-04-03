package com.vst.knotes.MVVM.Repositories

import android.util.Log
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper

class ProductRepository {
    fun insert(product: productModel) {
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtInsert = sqLiteDatabase.compileStatement(
                    "INSERT INTO tblproducts ( PRODUCTNAME, PRODUCTIMAGEURL," +
                            " PRODUCTDESCRIPTION, PRODUCTTYPE,ISACTIVE,STOREID,ZONEID,UOM," +
                            "UNITSPERCASE,BRAND,CATEGORYID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                )
                stmtInsert.bindString(1, product.productname)
                stmtInsert.bindString(2, product.productimageurl)
                stmtInsert.bindString(3, product.productdescription)
                stmtInsert.bindString(4, product.producttype.toString())
                stmtInsert.bindString(5, product.isactive.toString())
                stmtInsert.bindString(6, product.storeid.toString())
                stmtInsert.bindString(7, product.zoneid.toString())
                stmtInsert.bindString(8, product.uom.toString())
                stmtInsert.bindString(9, product.unitspercase.toString())
                stmtInsert.bindString(10, product.brand.toString())
                stmtInsert.bindString(11, product.categoryid.toString())
                stmtInsert.executeInsert()
                stmtInsert.close()

            }
        } catch (e: Exception) {
            Log.e("productDA_insert", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun update(product: productModel) {
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtUpdate = sqLiteDatabase.compileStatement(
                    "UPDATE tblproducts SET productname = ?," +
                            " productimageurl =?, productdescription =? ," +
                            " producttype = ? , isactive =?, storeid =? , zoneid =?," +
                            "uom =? , unitspercase =? , brand =? , categoryid = ? WHERE productid = ?"
                )
                stmtUpdate.bindString(1, product.productname)
                stmtUpdate.bindString(2, product.productimageurl)
                stmtUpdate.bindString(3, product.productdescription)
                stmtUpdate.bindString(4, product.producttype.toString())
                stmtUpdate.bindString(5, product.isactive.toString())
                stmtUpdate.bindString(6, product.storeid.toString())
                stmtUpdate.bindString(7, product.zoneid.toString())
                stmtUpdate.bindString(8, product.uom.toString())
                stmtUpdate.bindString(9, product.unitspercase.toString())
                stmtUpdate.bindString(10, product.brand.toString())
                stmtUpdate.bindString(11, product.categoryid.toString())
                stmtUpdate.bindString(12, product.productid)
                stmtUpdate.executeUpdateDelete()
                stmtUpdate.close()
            }
        } catch (e: Exception) {
            Log.e("productDA_update", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun delete(product: productModel) {
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtDelete = sqLiteDatabase.compileStatement(
                    "DELETE FROM tblproducts WHERE productid = ?"
                )
                stmtDelete.bindString(1, product.productid)
                stmtDelete.executeUpdateDelete()
                stmtDelete.close()
            }
        } catch (e: Exception) {
            Log.e("productDA_Delete", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun getAllCategories(storeid: String, zoneid: String): ArrayList<categoryModel> {
        var hashcategory: ArrayList<categoryModel> = ArrayList<categoryModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT * FROM tblcategory where storeid = '" + storeid + "' and zoneid='" + zoneid + "'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        var index: Int = 0
                        do {
                            var category = categoryModel()
                            category.categoryid = cursor.getString(0)
                            category.categoryname = cursor.getString(1)
                            category.categoryimageurl = cursor.getString(2)
                            category.categorydescription = cursor.getString(3)
                            category.storeid = cursor.getInt(4)
                            category.zoneid = cursor.getInt(5)
                            hashcategory.add(index++, category)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("productDASQLITE", "tblmenu is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("productDASQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashcategory;
    }

    fun getAllProducts(STOREID: String, ZONEID: String): ArrayList<productModel> {
        var hashproduct: ArrayList<productModel> = ArrayList<productModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT productid,productname,productimageurl,productdescription,producttype,isactive,storeid,zoneid,categoryid,uom,unitspercase,brand FROM tblproducts where storeid='" + STOREID + "' and zoneid='" + ZONEID + "'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val product = productModel()
                            product.productid = cursor.getString(0)
                            product.productname = cursor.getString(1)
                            product.productimageurl = cursor.getString(2)
                            product.productdescription = cursor.getString(3)
                            product.producttype = cursor.getInt(4)
                            product.isactive = cursor.getInt(5)
                            product.storeid = cursor.getInt(6)
                            product.zoneid = cursor.getInt(7)
                            product.categoryid = cursor.getInt(8)
                            product.uom = cursor.getInt(9)
                            product.unitspercase = cursor.getInt(10)
                            product.brand = cursor.getInt(11)
                            hashproduct.add(product)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblproducts is EMPTY!")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashproduct;
    }

    fun getproductById(STOREID: String, ZONEID: String, productid : String): ArrayList<productModel> {
        var hashproduct: ArrayList<productModel> = ArrayList<productModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT productid,productname,productimageurl,productdescription,producttype,isactive,storeid,zoneid,categoryid,uom,unitspercase,brand FROM tblproducts where storeid='" + STOREID + "' and zoneid='" + ZONEID + "' and productid='"+productid+"'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val product = productModel()
                            product.productid = cursor.getString(0)
                            product.productname = cursor.getString(1)
                            product.productimageurl = cursor.getString(2)
                            product.productdescription = cursor.getString(3)
                            product.producttype = cursor.getInt(4)
                            product.isactive = cursor.getInt(5)
                            product.storeid = cursor.getInt(6)
                            product.zoneid = cursor.getInt(7)
                            product.categoryid = cursor.getInt(8)
                            product.uom = cursor.getInt(9)
                            product.unitspercase = cursor.getInt(10)
                            product.brand = cursor.getInt(11)
                            hashproduct.add(product)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblproducts is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashproduct;
    }
}