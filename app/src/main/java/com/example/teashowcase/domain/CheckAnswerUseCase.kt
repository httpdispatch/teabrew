package com.example.teashowcase.domain

import com.example.teashowcase.domain.entities.AnswerResult
import com.example.teashowcase.domain.repository.GameRepository
import io.reactivex.Single
import javax.inject.Inject

interface CheckAnswerUseCase {

    operator fun invoke(gameId: String, answer: Int): Single<AnswerResult>
}

internal class CheckAnswerUseCaseImpl @Inject constructor(
    private val repository: GameRepository
) : CheckAnswerUseCase {

    override fun invoke(gameId: String, answer: Int): Single<AnswerResult> {
        return repository.checkAnswer(gameId = gameId, answer = answer)
    }
}
