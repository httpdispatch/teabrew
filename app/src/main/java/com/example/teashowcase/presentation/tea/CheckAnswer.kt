package com.example.teashowcase.presentation.tea

import android.os.SystemClock
import com.example.teashowcase.R
import com.example.teashowcase.domain.CheckAnswerUseCase
import com.example.teashowcase.domain.entities.AnswerResult
import com.example.teashowcase.presentation.model.GameState
import com.example.teashowcase.presentation.model.GameStateModel
import com.example.teashowcase.presentation.model.LoadingState
import com.example.teashowcase.presentation.providers.ResourceProvider
import com.example.teashowcase.tea.Program
import com.example.teashowcase.tea.RunOnceEvent
import com.example.teashowcase.tea.android.Cmd
import com.example.teashowcase.tea.android.Msg
import com.example.teashowcase.tea.android.RxCmdHandler
import com.example.teashowcase.tea.android.Update
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

@Parcelize
data class CheckAnswer(private val answer: Int) : Msg<GameStateModel> {

    override fun invoke(state: GameStateModel): Update<GameStateModel> {
        return if (!state.gameState.isActive) {
            Update(state = state)
        } else {
            Update(
                state = state.copy(checkAnswerStatus = LoadingState(loading = true)),
                commands = listOf(
                    CheckAnswerCommand(gameId = state.gameState.id, answer = answer)
                )
            )
        }
    }
}

@Parcelize
data class CheckAnswerCommand(
    private val gameId: String,
    private val answer: Int
) : Cmd {

    class Handler @Inject constructor(
        private val checkAnswerUseCase: CheckAnswerUseCase,
        private val resourceProvider: ResourceProvider,
    ) : RxCmdHandler<GameStateModel, CheckAnswerCommand> {

        @Volatile
        private var lastCommand: CheckAnswerCommand? = null

        override fun invoke(cmd: CheckAnswerCommand): Observable<Msg<GameStateModel>> {
            return Completable.fromAction { lastCommand = cmd }
                .andThen(checkAnswerUseCase(gameId = cmd.gameId, answer = cmd.answer))
                .filter { lastCommand === cmd }
                .map<Msg<GameStateModel>> { answerResult ->
                    AnswerChecked(
                        gameId = cmd.gameId,
                        answerResult = answerResult,
                        answerStatus = answerResult.toStatus()
                    )
                }
                .onErrorReturn { error: Throwable ->
                    Timber.e(error)
                    CheckAnswerFailed()
                }
                .toObservable()
        }

        private fun AnswerResult.toStatus(): CharSequence {
            return when (this) {
                AnswerResult.MORE -> resourceProvider.getString(R.string.answer_is_more)
                AnswerResult.EQUAL -> resourceProvider.getString(R.string.answer_is_correct)
                AnswerResult.LESS -> resourceProvider.getString(R.string.answer_is_less)
            }
        }

        override fun isForCommand(command: Program.Cmd): Boolean {
            return command is CheckAnswerCommand
        }
    }

    @Parcelize
    class CheckAnswerFailed : Msg<GameStateModel> {

        override fun invoke(state: GameStateModel): Update<GameStateModel> {
            return Update(
                state = state.copy(checkAnswerStatus = LoadingState(error = RunOnceEvent()))
            )
        }

        override fun toString(): String {
            return "CheckAnswerFailed()"
        }
    }

    @Parcelize
    data class AnswerChecked(
        private val gameId: String,
        private val answerResult: AnswerResult,
        private val answerStatus: CharSequence
    ) : Msg<GameStateModel> {

        override fun invoke(state: GameStateModel): Update<GameStateModel> {
            return if (gameId != state.gameState.id || !state.gameState.isActive) {
                Update(state = state)
            } else {
                val gameState = state.gameState.forAnswerResult(
                    answerResult = answerResult,
                    answerStatus = answerStatus,
                )
                val commands: List<Cmd> = when {
                    !gameState.started -> listOf(ControlGameTimeCommand.StopGameTimeControlCommand)
                    state.gameState.started || answerResult == AnswerResult.EQUAL -> emptyList()
                    else -> listOf(
                        ControlGameTimeCommand.StartGameTimeControlCommand(
                            gameId = gameId,
                            startedAt = gameState.startedAt,
                            maxDurationMillis = gameState.maxDurationMillis
                        )
                    )
                }
                Update(
                    state = state.copy(
                        checkAnswerStatus = LoadingState(loading = false),
                        gameState = gameState
                    ),
                    commands = commands
                )
            }
        }

        private fun GameState.forAnswerResult(
            answerResult: AnswerResult,
            answerStatus: CharSequence
        ): GameState {
            val started = answerResult != AnswerResult.EQUAL
            return copy(
                started = started,
                answerStatus = answerStatus,
                status = if (started) status else answerStatus,
                newAnswerStatus = RunOnceEvent(),
                startedAt = startedAt.takeIf { it >= 0 }
                    ?: SystemClock.elapsedRealtime()
            )
        }
    }
}
