package com.vst.knotes.MVVM.Model

import androidx.room.Embedded
import androidx.room.Relation

data class MilkCollectionWithFarmerModel(
    @Embedded val collection: MilkCollectionModel,
    @Relation(
        parentColumn = "farmerId",
        entityColumn = "farmerId"
    )
    val farmer: FarmerModel
)