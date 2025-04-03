package com.vst.knotes.MVVM.Repositories

import android.util.Log
import com.vst.knotes.MVVM.Model.categoryModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import java.util.HashMap


class SalesRepository {

    fun getPricing() {

    }

    fun getCategories(storeid: String, zoneid: String): HashMap<String, ArrayList<categoryModel>> {
        val hmCategories: HashMap<String, ArrayList<categoryModel>> =
            HashMap<String, ArrayList<categoryModel>>()
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
                            hmCategories.put(cursor.getString(0), hashcategory)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblmenu is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hmCategories;
    }

    fun getarrCategories(storeid: String, zoneid: String): ArrayList<categoryModel> {
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
                        Log.e("SQLITE", "tblmenu is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashcategory;
    }

    fun getProducts(storeid: String, zoneid: String): HashMap<Int, ArrayList<productModel>> {
        val hmProducts: HashMap<Int, ArrayList<productModel>> =
            HashMap<Int, ArrayList<productModel>>()
        var products: ArrayList<productModel> = ArrayList<productModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT PRODUCTID,PRODUCTNAME,PRODUCTIMAGEURL,PRODUCTDESCRIPTION,PRODUCTTYPE,ISACTIVE,STOREID,ZONEID,CATEGORYID,UOM,UNITSPERCASE,BRAND FROM tblproducts where storeid = '" + storeid + "' and zoneid='" + zoneid + "'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        var index: Int = 0
                        do {
                            var product = productModel()
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
                            if (hmProducts.get(product.categoryid) == null) {
                                products = ArrayList()
                            } else {
                                products = hmProducts.get(product.categoryid)!!;
                            }
                            products.add(product);
                            hmProducts.put(product.categoryid, products);

                        } while (cursor.moveToNext());

                    } else {
                        Log.e("SQLITE", "tblmenu is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hmProducts;
    }
}