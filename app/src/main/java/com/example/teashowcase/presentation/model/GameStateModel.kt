package com.example.teashowcase.presentation.model

import android.os.Parcelable
import com.example.teashowcase.tea.RunOnceEvent
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameStateModel(
    val gameLoadingStatus: LoadingState = LoadingState(),
    val checkAnswerStatus: LoadingState = LoadingState(),
    val gameState: GameState = GameState()
) : Parcelable {

    val checkAnswerButtonVisible: Boolean = gameState.isActive
    val checkingAnswerProgressVisible: Boolean =
        checkAnswerButtonVisible && checkAnswerStatus.loading
    val nextGameButtonVisible: Boolean = !gameState.isActive
    val loadingNextGameProgressVisible: Boolean = nextGameButtonVisible && gameLoadingStatus.loading
    val gameLoaded: Boolean = gameState.id.isNotEmpty()
    val gameStatus: CharSequence = gameState.status
    val gameLoadingError: RunOnceEvent? = gameLoadingStatus.error
    val checkAnswerError: RunOnceEvent? = checkAnswerStatus.error
}

@Parcelize
data class LoadingState(
    val loading: Boolean = false,
    val error: RunOnceEvent? = null,
) : Parcelable

@Parcelize
data class GameState(
    val id: String = "",
    val status: CharSequence = "",
    val answerStatus: CharSequence = "",
    val newAnswerStatus: RunOnceEvent? = null,
    val started: Boolean = false,
    val startedAt: Long = -1,
    val maxDurationMillis: Long = 0,
) : Parcelable {

    val isActive: Boolean =
        id.isNotEmpty() && (startedAt == -1L || startedAt != -1L && started)
}

