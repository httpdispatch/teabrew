package com.example.teashowcase.di

import android.app.Application
import android.content.Context
import com.example.teashowcase.presentation.providers.DefaultResourceProvider
import com.example.teashowcase.presentation.providers.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        ViewModelModule::class,
        GameModule::class
    ]
)
internal interface AppModule {

    @Binds
    fun bindResourceProvider(impl: DefaultResourceProvider): ResourceProvider

    companion object {
        @Provides
        fun provideContext(application: Application): Context = application
    }
}
