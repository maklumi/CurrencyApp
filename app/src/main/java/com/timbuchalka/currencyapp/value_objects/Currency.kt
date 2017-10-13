package com.timbuchalka.currencyapp.value_objects

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by HomePC on 12/10/2017.
 */
data class Currency(var rate: Double, var date: String,
                    var base: String, var name: String,
                    var id: Long) : Parcelable {

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