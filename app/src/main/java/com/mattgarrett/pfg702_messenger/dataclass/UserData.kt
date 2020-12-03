package com.mattgarrett.pfg702_messenger.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var profileImageURL: String?,
    var uid: String?
): Parcelable {
    constructor() : this("","","","","")
}