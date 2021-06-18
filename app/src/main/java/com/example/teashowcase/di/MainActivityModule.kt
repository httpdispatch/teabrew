package com.example.teashowcase.di

import com.example.teashowcase.presentation.view.MainActivity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(
        modules = [
            FragmentBuildersModule::class,
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}
