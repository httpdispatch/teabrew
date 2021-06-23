package com.example.teashowcase.presentation.tea

import com.example.teashowcase.presentation.model.GameStateModel
import com.example.teashowcase.tea.android.RxAndroidProgram
import javax.inject.Inject

class GameProgram @Inject constructor(
    loadNextGameCommandHandler: LoadNextGameCommand.Handler,
    checkAnswerCommandHandler: CheckAnswerCommand.Handler,
    controlGameTimeCommandHandler: ControlGameTimeCommand.Handler,
) : RxAndroidProgram<GameStateModel>(
    commandHandlers = listOf(
        loadNextGameCommandHandler,
        checkAnswerCommandHandler,
        controlGameTimeCommandHandler
    ),
    debugEnabled = true
)