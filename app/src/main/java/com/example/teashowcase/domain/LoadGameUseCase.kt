package com.example.teashowcase.domain

import com.example.teashowcase.domain.entities.Game
import com.example.teashowcase.domain.repository.GameRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface LoadGameUseCase {

    operator fun invoke(): Single<Game>
}

internal class LoadGameUseCaseImpl @Inject constructor(
    private val repository: GameRepository
) : LoadGameUseCase {

    override fun invoke(): Single<Game> {
        return repository.loadGame()
    }
}
