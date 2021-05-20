package com.example.teashowcase.di

import com.example.teashowcase.data.GameRepositoryImpl
import com.example.teashowcase.domain.CheckAnswerUseCase
import com.example.teashowcase.domain.CheckAnswerUseCaseImpl
import com.example.teashowcase.domain.LoadGameUseCase
import com.example.teashowcase.domain.LoadGameUseCaseImpl
import com.example.teashowcase.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
internal interface GameModule {

    @Binds @Reusable
    fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds fun bindCheckAnswerUseCae(impl: CheckAnswerUseCaseImpl): CheckAnswerUseCase

    @Binds
    fun bindLoadGameUseCase(impl: LoadGameUseCaseImpl): LoadGameUseCase
}
