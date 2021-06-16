package com.example.teashowcase.tea

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RunOnceEvent() : Parcelable {
    var hasBeenHandled = false
        private set // Allow external read but not write

    fun runIfNotHandled(block: () -> Unit) {
        if (!hasBeenHandled) {
            hasBeenHandled = true
            block()
        }
    }

    override fun toString(): String {
        return "SimpleEvent(hasBeenHandled=$hasBeenHandled)"
    }
}