package com.example.teashowcase.domain.repository

import com.example.teashowcase.domain.entities.AnswerResult
import com.example.teashowcase.domain.entities.Game
import io.reactivex.Single

interface GameRepository {

    fun loadGame(): Single<Game>

    fun checkAnswer(gameId: String, answer: Int): Single<AnswerResult>
}
