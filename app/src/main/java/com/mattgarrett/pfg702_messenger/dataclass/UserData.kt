package com.mattgarrett.pfg702_messenger.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val profileImageURL: String?,
    val uid: String?
): Parcelable {
    constructor() : this("","","","","")
}