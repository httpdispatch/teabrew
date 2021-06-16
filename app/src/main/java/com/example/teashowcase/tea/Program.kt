package com.example.teashowcase.tea

interface Program<T : Any, M : Program.Msg<T, C>, C : Program.Cmd, R> {

    fun start(
        initialState: T,
        commandHandlers: List<CmdHandler<out C, R>>,
        component: Component<T>
    )

    fun accept(msg: M)

    fun stop()

    interface Msg<T : Any, C : Cmd> : (T) -> Update<T, C>

    interface Cmd

    interface CmdHandler<C : Cmd, R> : (C) -> R {

        fun isForCommand(command: Cmd): Boolean
    }

    interface Update<T : Any, C : Cmd> {
        val state: T
        val commands: List<C>
    }

    fun interface Component<S> {

        fun render(state: S)
    }
}
