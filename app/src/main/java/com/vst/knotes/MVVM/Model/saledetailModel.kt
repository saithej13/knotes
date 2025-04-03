package com.vst.knotes.MVVM.Model

import java.nio.DoubleBuffer

data class saledetailModel(
    var id : String,
    var saleid : Int,
    var productid : Int,
    var productname: String,
    var price : Double,
    var uom : String,
    var quantity : Double,
    var totaldiscountpercentage : Double,
    var totaldiscountamount : Double,
    var sgst : Double,
    var cgst : Double,
    var igst : Double,
    var taxpercentage : Double,
    var taxamount : Double,
    var amount : Double,
    var totalamount : Double,
    var offerid : Int,
    var batchno : String
)