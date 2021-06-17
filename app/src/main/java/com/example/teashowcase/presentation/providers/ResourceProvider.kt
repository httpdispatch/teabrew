package com.example.teashowcase.presentation.providers

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import javax.inject.Inject

interface ResourceProvider {

    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, vararg args: Any): String

    fun getPlural(@PluralsRes id: Int, quantity: Int, vararg args: Any): String
}

internal class DefaultResourceProvider @Inject constructor(
    private val context: Context
) : ResourceProvider {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    override fun getString(@StringRes id: Int, vararg args: Any): String {
        return context.getString(id, *args)
    }

    override fun getPlural(@PluralsRes id: Int, quantity: Int, vararg args: Any): String {
        return context.resources.getQuantityString(id, quantity, *args)
    }
}
