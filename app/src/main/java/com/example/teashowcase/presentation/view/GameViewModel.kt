package com.example.teashowcase.presentation.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.teashowcase.di.AssistedSavedStateViewModelFactory
import com.example.teashowcase.presentation.model.GameStateModel
import com.example.teashowcase.presentation.tea.*
import com.example.teashowcase.tea.android.RxAndroidProgram
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class GameViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    loadNextGameCommandHandler: LoadNextGameCommand.Handler,
    checkAnswerCommandHandler: CheckAnswerCommand.Handler,
    controlGameTimeCommandHandler: ControlGameTimeCommand.Handler
) : ViewModel() {

    val state: LiveData<GameStateModel> =
        savedStateHandle.getLiveData(GAME_STATE_MODEL, GameStateModel())

    private val program: RxAndroidProgram<GameStateModel> = RxAndroidProgram<GameStateModel>(
        debugEnabled = true
    ).apply {
        start(
            initialState = this@GameViewModel.state.value!!,
            commandHandlers = listOf(
                loadNextGameCommandHandler,
                checkAnswerCommandHandler,
                controlGameTimeCommandHandler
            )
        ) { state ->
            savedStateHandle.set(GAME_STATE_MODEL, state)
        }
    }

    fun onCheckAnswerClicked(answer: String) {
        program.accept(CheckAnswer(answer = answer.toIntOrNull() ?: -1))
    }

    fun onNextGameClicked() {
        program.accept(LoadNextGame())
    }

    override fun onCleared() {
        super.onCleared()
        program.stop()
    }

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<GameViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): GameViewModel
    }

    companion object {
        const val GAME_STATE_MODEL = "game_state_model"
    }
}
