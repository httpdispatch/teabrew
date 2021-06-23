package com.example.teashowcase.tea.rx

import com.example.teashowcase.tea.Program
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

open class RxProgram<T : Any, M : Program.Msg<T, C>, C : Program.Cmd>(
    private val commandHandlers: List<Program.CmdHandler<out C, Observable<out M>>>,
    private val outputScheduler: Scheduler,
    private val debugEnabled: Boolean = false,
) : Program<T, M, C, Observable<out M>> {

    private val msgRelay: PublishProcessor<M> = PublishProcessor.create()

    lateinit var state: T
        private set

    private var disposable: Disposable? = null

    override fun start(
        initialState: T,
        component: Program.Component<T>
    ) {
        if (disposable != null) {
            throw IllegalStateException("The program is already started")
        }
        this.state = initialState
        disposable = msgRelay
            .onBackpressureBuffer()
            // new state calculation is performed in the computation thread with no concurrency (buffer size is 1)
            .observeOn(Schedulers.computation(), false, 1)
            .map { msg ->
                if (debugEnabled) {
                    Timber.d("Handling message $msg")
                }
                //update program state and return the new state and command
                msg(state)
            }
            .doOnNext { update ->
                if (debugEnabled) {
                    Timber.d("Message result $update")
                }
                this.state = update.state
            }
            .observeOn(outputScheduler)
            .doOnNext { update ->
                //draw UI
                component.render(state = update.state)
            }
            .filter { it.commands.isNotEmpty() }
            .onBackpressureBuffer()
            // Every command launches in the IO thread with no concurrency (buffer size is 1).
            // Command handler may switch scheduler internally if parallel invocation is allowed
            .observeOn(Schedulers.io(), false, 1)
            .flatMap { update ->
                Flowable.fromIterable(update.commands)
                    .flatMap { cmd ->
                        if (debugEnabled) {
                            Timber.d("Running command cmd")
                        }
                        commandHandlers.handleCommand(cmd)
                            .toFlowable(BackpressureStrategy.BUFFER)
                    }
            }
            .observeOn(outputScheduler)
            .subscribeBy(onNext = (::accept))
    }

    override fun accept(msg: M) {
        if (debugEnabled) {
            Timber.d("Accepting message $msg")
        }
        msgRelay.onNext(msg)
    }

    override fun stop() {
        disposable?.dispose()
        disposable = null
    }

    @Suppress("UNCHECKED_CAST")
    private fun List<Program.CmdHandler<out C, Observable<out M>>>.handleCommand(cmd: C): Observable<out M> {
        return (first { it.isForCommand(cmd) } as Program.CmdHandler<C, Observable<out M>>)(cmd)
    }
}