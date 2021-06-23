package com.example.teashowcase.tea.android

import com.example.teashowcase.tea.Program
import com.example.teashowcase.tea.rx.RxProgram
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

open class RxAndroidProgram<T : Any>(
    commandHandlers: List<Program.CmdHandler<out Cmd, Observable<out Msg<T>>>>,
    debugEnabled: Boolean = false,
) : RxProgram<T, Msg<T>, Cmd>(
    commandHandlers = commandHandlers,
    outputScheduler = AndroidSchedulers.mainThread(),
    debugEnabled = debugEnabled
)

interface RxCmdHandler<T : Any, C : Cmd> :
    Program.CmdHandler<C, Observable<out Msg<T>>>