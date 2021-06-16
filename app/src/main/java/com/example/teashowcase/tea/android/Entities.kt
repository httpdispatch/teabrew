package com.example.teashowcase.tea.android

import android.os.Parcelable
import com.example.teashowcase.tea.Program

interface Msg<T : Any> : Program.Msg<T, Cmd>, Parcelable

interface Cmd : Program.Cmd, Parcelable

data class Update<T : Any>(
    override val state: T,
    override val commands: List<Cmd> = emptyList()
) : Program.Update<T, Cmd>