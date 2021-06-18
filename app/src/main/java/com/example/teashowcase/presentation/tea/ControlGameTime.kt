package com.example.teashowcase.presentation.tea

import android.os.SystemClock
import com.example.teashowcase.R
import com.example.teashowcase.presentation.model.GameStateModel
import com.example.teashowcase.presentation.providers.ResourceProvider
import com.example.teashowcase.tea.Program
import com.example.teashowcase.tea.android.Cmd
import com.example.teashowcase.tea.android.Msg
import com.example.teashowcase.tea.android.RxCmdHandler
import com.example.teashowcase.tea.android.Update
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class ControlGameTimeCommand : Cmd {
    @Parcelize
    data class StartGameTimeControlCommand(
        val gameId: String,
        val startedAt: Long,
        val maxDurationMillis: Long
    ) : ControlGameTimeCommand()

    @Parcelize
    object StopGameTimeControlCommand : ControlGameTimeCommand() {
        override fun toString(): String {
            return "StopGameTimeControlCommand()"
        }
    }

    class Handler @Inject constructor(
        private val resourceProvider: ResourceProvider,
    ) : RxCmdHandler<GameStateModel, ControlGameTimeCommand> {

        private val canceler: PublishSubject<Any> = PublishSubject.create()

        override fun invoke(cmd: ControlGameTimeCommand): Observable<out Msg<GameStateModel>> {
            return when (cmd) {
                is StartGameTimeControlCommand -> Observable.interval(0, 1, TimeUnit.SECONDS)
                    .map {
                        val remainingTimeSeconds =
                            (cmd.startedAt + cmd.maxDurationMillis - SystemClock.elapsedRealtime())
                                .let(TimeUnit.MILLISECONDS::toSeconds)
                                .toInt()
                        val status = if (remainingTimeSeconds <= 0) {
                            resourceProvider.getString(R.string.time_is_up)
                        } else {
                            resourceProvider.getPlural(
                                R.plurals.remaining_time,
                                remainingTimeSeconds,
                                remainingTimeSeconds
                            )
                        }
                        UpdateGameTime(
                            gameId = cmd.gameId,
                            status = status,
                            gameOver = remainingTimeSeconds <= 0
                        )
                    }
                    .takeUntil(canceler)
                    .takeUntil { it.gameOver }
                StopGameTimeControlCommand -> Completable.fromAction {
                    canceler.onNext(true)
                }
                    .toObservable()
            }
        }

        override fun isForCommand(command: Program.Cmd): Boolean {
            return command is ControlGameTimeCommand
        }
    }
}

@Parcelize
data class UpdateGameTime(
    private val gameId: String,
    private val status: String,
    val gameOver: Boolean
) :
    Msg<GameStateModel> {
    override fun invoke(state: GameStateModel): Update<GameStateModel> {
        return if (state.gameState.id != gameId || !state.gameState.isActive) {
            Update(state = state)
        } else {
            Update(
                state = state.copy(
                    gameState = state.gameState.copy(
                        started = !gameOver,
                        status = status
                    )
                )
            )
        }
    }
}
