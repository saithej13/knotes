package com.vst.knotes.MVVM.Model

data class trxdetailModel(
    var lineno:Int,
    var trxcode:String,
    var productid:String,
    var price:Double,
    var uom : String,
    var quantity : String,
    var totaldiscountpercentage: String,
    var totaldiscountamount:String,
    var productdescription:String,
    var sgst : Double,
    var cgst : Double,
    var igst : Double,
    var taxpercentage: Double,
    var taxamount : Double,
    var amount : Double,
    var totalamount: Double,
    var offerid: String,
    var batchno : String,
    var discount : String,
)