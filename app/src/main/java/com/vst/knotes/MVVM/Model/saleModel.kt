package com.vst.knotes.MVVM.Model

data class saleModel (
    var id: String,
    var appid: String,
    var usercode : String,
    var customercode : String,
    var customername : String,
    var saledate : String,
    var saletype: Int,
    var paymenttyep : String,
    var totalamount : Double,
    var totaldiscountamount : Double,
    var totaltaxamount : Double,
    var status : Int,
    var updatedon : String,
    var approveddate : String,
    var approvedby : String,
    var geox : String,
    var geoy : String
)
