package com.example.teashowcase.presentation.tea

import com.example.teashowcase.R
import com.example.teashowcase.domain.LoadGameUseCase
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
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

@Parcelize
class LoadNextGame : Msg<GameStateModel> {

    override fun invoke(state: GameStateModel): Update<GameStateModel> {
        return if (state.gameLoadingStatus.loading) {
            Update(state = state)
        } else {
            Update(
                state = state.copy(gameLoadingStatus = LoadingState(loading = true)),
                commands = listOf(LoadNextGameCommand())
            )
        }
    }

    override fun toString(): String {
        return "LoadNextGame()"
    }
}

@Parcelize
class LoadNextGameCommand : Cmd {

    override fun toString(): String {
        return "LoadNextGameCommand()"
    }

    class Handler @Inject constructor(
        private val loadGameUseCase: LoadGameUseCase,
        private val resourceProvider: ResourceProvider,
    ) : RxCmdHandler<GameStateModel, LoadNextGameCommand> {

        override fun invoke(cmd: LoadNextGameCommand): Observable<out Msg<GameStateModel>> {
            return loadGameUseCase()
                .map<Msg<GameStateModel>> { game ->
                    GameLoaded(
                        gameId = game.id,
                        maxDurationMillis = game.maxDurationMillis,
                        status = resourceProvider.getString(R.string.next_game_loaded)
                    )
                }
                .onErrorReturn { error ->
                    Timber.e(error)
                    GameLoadFailed()
                }
                .toObservable()
        }

        override fun isForCommand(command: Program.Cmd): Boolean {
            return command is LoadNextGameCommand
        }
    }

    @Parcelize
    class GameLoadFailed : Msg<GameStateModel> {

        override fun invoke(state: GameStateModel): Update<GameStateModel> {
            return Update(
                state = state.copy(gameLoadingStatus = LoadingState(error = RunOnceEvent()))
            )
        }

        override fun toString(): String {
            return "GameLoadFailed()"
        }
    }

    @Parcelize
    data class GameLoaded(
        private val gameId: String,
        private val maxDurationMillis: Long,
        private val status: String
    ) : Msg<GameStateModel> {

        override fun invoke(state: GameStateModel): Update<GameStateModel> {
            return Update(
                state = state.copy(
                    gameLoadingStatus = LoadingState(loading = false),
                    gameState = GameState(
                        id = gameId,
                        status = status,
                        maxDurationMillis = maxDurationMillis
                    )
                )
            )
        }
    }
}
