package com.vst.knotes

import android.os.Parcel
import android.os.Parcelable

data class MainScreenDM(
    val id: Long,                // Add the ID field
    val title: String,
    val description: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),        // Read ID as Long
        parcel.readString() ?: "", // Read title
        parcel.readString() ?: ""  // Read description
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)       // Write ID to Parcel
        parcel.writeString(title)   // Write title
        parcel.writeString(description) // Write description
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainScreenDM> {
        override fun createFromParcel(parcel: Parcel): MainScreenDM {
            return MainScreenDM(parcel)
        }

        override fun newArray(size: Int): Array<MainScreenDM?> {
            return arrayOfNulls(size)
        }
    }
}
