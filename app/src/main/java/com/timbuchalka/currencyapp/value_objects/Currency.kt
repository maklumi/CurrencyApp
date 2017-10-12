package com.timbuchalka.currencyapp.value_objects

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by HomePC on 12/10/2017.
 */
data class Currency (private var rate: Double, private var date: String,
                     private var base: String, private var name: String,
                     private var id: Long = 0L) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(rate)
        dest.writeString(date)
        dest.writeString(base)
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Currency> {
        override fun createFromParcel(parcel: Parcel): Currency {
            return Currency(parcel)
        }

        override fun newArray(size: Int): Array<Currency?> {
            return arrayOfNulls(size)
        }
    }
}