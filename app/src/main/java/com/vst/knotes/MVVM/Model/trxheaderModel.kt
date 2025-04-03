package com.vst.knotes.MVVM.Model

data class trxheaderModel(
    var trxcode:String,
    var trxappid:String,
    var usercode:String,
    var customercode:String,
    var customername:String,
    var trxdata:String,
    var trxtype:Int,
    var paymenttype:String,
    var totalamount:Double,
    var totaldiscountamount:Double,
    var totaltaxamount:Double,
    var status:Int,
    var updateon:String,
    var approveddate : String,
    var deliverydate : String,
    var geox : String,
    var geoy : String,
    var modifiedby : String,
)