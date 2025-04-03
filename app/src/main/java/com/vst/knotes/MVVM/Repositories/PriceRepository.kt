package com.vst.knotes.MVVM.Repositories

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vst.knotes.MVVM.Model.pricingModel
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.R
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper

class PriceRepository {
    fun insert(price: pricingModel) {
        try{
            var ActivePriceExists : Boolean = false
            //first we should check whether active price is available for the product for the period
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT * FROM tblpricing where storeid = '" + price.storeid + "' and zoneid='" + price.zoneid + "' and productid='"+price.productid+"' " +
                            "and (Date('"+price.startdate+"')BETWEEN startdate and enddate)"+
                            "or (Date('"+price.enddate+"')BETWEEN startdate and enddate)"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        ActivePriceExists = true
                    } else {
                        Log.e("priceDASQLITE", "tblpricing table Empty!")
                    }
                }
            }
            if(ActivePriceExists)
            {
                throw Exception("Active Price Already Exists for the Selected Product with Selected Period!")
            }
            else {
                DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                    val stmtInsert = sqLiteDatabase.compileStatement(
                        "INSERT INTO tblpricing (PRODUCTID, PRICE, SGST,CGST,IGST,OTHERTAX,DISCOUNT, STOREID, ZONEID,STARTDATE,ENDDATE) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    )
                    stmtInsert.bindString(1, "" + price.productid)//PRODUCTID
                    stmtInsert.bindString(2, "" + price.price)//PRICE
                    stmtInsert.bindString(3, "" + price.sgst)//SGST
                    stmtInsert.bindString(4, "" + price.cgst)//CGST
                    stmtInsert.bindString(5, "" + price.igst)//IGST
                    stmtInsert.bindString(6, "" + price.othertax)//OTHERTAX
                    stmtInsert.bindString(7, "" + price.discount)//DISCOUNT
                    stmtInsert.bindString(8, "" + price.storeid)//STOREID
                    stmtInsert.bindString(9, "" + price.zoneid)//ZONEID
                    stmtInsert.bindString(10, "" + price.startdate)//STARTDATE
                    stmtInsert.bindString(11, "" + price.enddate)//ENDDATE
                    stmtInsert.executeInsert()
                    stmtInsert.close()
                    throw Exception("Price Saved Successfully!")
                }
            }

        } catch (e: Exception) {
            throw e
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun update(price: pricingModel) {
        try {
            var ActivePriceExists : Boolean = false
            //first we should check whether active price is available for the product for the period
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT * FROM tblpricing where storeid = '" + price.storeid + "' and zoneid='" + price.zoneid + "' and productid='"+price.productid+"' " +
                            "and (Date('"+price.startdate+"')BETWEEN startdate and enddate)"+
                            "or (Date('"+price.enddate+"')BETWEEN startdate and enddate)"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        ActivePriceExists = true
                    } else {
                        Log.e("priceDASQLITE", "tblpricing table Empty!")
                    }
                }
            }
            if(ActivePriceExists)
            {
                throw Exception("Active Price Already Exists for the Selected Product with Selected Period!")
            }
            else{
                DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                    val stmtUpdate = sqLiteDatabase.compileStatement(
                        "UPDATE tblpricing SET PRICE = ?, SGST = ?,CGST =?," +
                                "IGST = ?,OTHERTAX = ?,DISCOUNT = ?, STOREID = ?," +
                                " ZONEID = ?,STARTDATE = ?,ENDDATE= ? WHERE productid = ?"
                    )
                    stmtUpdate.bindString(1, ""+price.price)//PRICE
                    stmtUpdate.bindString(2, ""+price.sgst)//SGST
                    stmtUpdate.bindString(3, ""+price.cgst)//CGST
                    stmtUpdate.bindString(4, ""+price.igst)//IGST
                    stmtUpdate.bindString(5, ""+price.othertax)//OTHERTAX
                    stmtUpdate.bindString(6, ""+price.discount)//DISCOUNT
                    stmtUpdate.bindString(7, ""+price.storeid)//STOREID
                    stmtUpdate.bindString(8, ""+price.zoneid)//ZONEID
                    stmtUpdate.bindString(9, ""+price.startdate)//STARTDATE
                    stmtUpdate.bindString(10, ""+price.enddate)//ENDDATE
                    stmtUpdate.bindString(11, ""+price.productid)//PRODUCTID
                    stmtUpdate.executeUpdateDelete()
                    stmtUpdate.close()
                    throw Exception("Price Updated Successfully!")
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun delete(price: pricingModel) {
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val stmtDelete = sqLiteDatabase.compileStatement(
                    "DELETE FROM tblpricing WHERE pricingid = ?"
                )
                stmtDelete.bindString(1, ""+price.pricingid)
                stmtDelete.executeUpdateDelete()
                stmtDelete.close()
            }
        } catch (e: Exception) {
            Log.e("PriceDA_update", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
    }

    fun getarrprices(storeid: String, zoneid: String): ArrayList<pricingModel> {
        var hashprice: ArrayList<pricingModel> = ArrayList<pricingModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT * FROM tblpricing where storeid = '" + storeid + "' and zoneid='" + zoneid + "'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        var index: Int = 0
                        do {
                            var prices = pricingModel()
                            prices.pricingid = cursor.getInt(0)
                            prices.productid = cursor.getInt(1)
                            prices.price = cursor.getDouble(2)
                            prices.sgst = cursor.getDouble(3)
                            prices.cgst = cursor.getDouble(4)
                            prices.igst = cursor.getDouble(5)
                            prices.othertax = cursor.getDouble(6)
                            prices.discount = cursor.getDouble(7)
                            prices.storeid = cursor.getInt(8)
                            prices.zoneid = cursor.getInt(9)
                            prices.startdate = cursor.getString(10)
                            prices.enddate = cursor.getString(11)
                            hashprice.add(index++, prices)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("priceDASQLITE", "tblpricing table Empty!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("priceDASQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashprice;
    }

    fun getarrpriceById(storeid: String, zoneid: String,productId : String): ArrayList<pricingModel> {
        var hashprice: ArrayList<pricingModel> = ArrayList<pricingModel>()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT * FROM tblpricing where storeid = '" + storeid + "' and zoneid='" + zoneid + "' and productid = '"+productId+"'"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        var index: Int = 0
                        do {
                            var prices = pricingModel()
                            prices.pricingid = cursor.getInt(0)
                            prices.productid = cursor.getInt(1)
                            prices.price = cursor.getDouble(2)
                            prices.sgst = cursor.getDouble(3)
                            prices.cgst = cursor.getDouble(4)
                            prices.igst = cursor.getDouble(5)
                            prices.othertax = cursor.getDouble(6)
                            prices.discount = cursor.getDouble(7)
                            prices.storeid = cursor.getInt(8)
                            prices.zoneid = cursor.getInt(9)
                            prices.startdate = cursor.getString(10)
                            prices.enddate = cursor.getString(11)
                            hashprice.add(index++, prices)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("priceDASQLITE", "tblpricing table Empty!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("priceDASQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return hashprice;
    }
    fun getProducts(storeid: String, zoneid: String): List<productModel> {
        val productsList: ArrayList<productModel> = ArrayList()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query =
                    "SELECT PRODUCTID, PRODUCTNAME FROM tblproducts WHERE storeid = ? and zoneid = ?"
                val cursor = sqLiteDatabase.rawQuery(query, arrayOf(storeid, zoneid))

                cursor.use {
                    if (it.moveToFirst()) {
                        var index = 1
                        do {
                            val product = productModel()
                            product.productid = it.getString(0)
                            product.productname = it.getString(1)
                            productsList.add(product)
                        } while (it.moveToNext())
                    } else {
                        Log.e("priceDASQLITE", "tblproducts table is empty!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("priceDASQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
        }
        return productsList
    }
}