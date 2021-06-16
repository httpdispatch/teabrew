package com.example.teashowcase.tea.android

import com.example.teashowcase.tea.Program
import com.example.teashowcase.tea.rx.RxProgram
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class RxAndroidProgram<T : Any>(
    debugEnabled: Boolean = false,
) : RxProgram<T, Msg<T>, Cmd>(
    outputScheduler = AndroidSchedulers.mainThread(),
    debugEnabled = debugEnabled
)

interface RxCmdHandler<T : Any, C : Cmd> :
    Program.CmdHandler<C, Observable<out Msg<T>>>