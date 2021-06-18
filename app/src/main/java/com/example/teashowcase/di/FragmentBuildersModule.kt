package com.example.teashowcase.di

import com.example.teashowcase.presentation.view.GameFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeGameFragment(): GameFragment
}
