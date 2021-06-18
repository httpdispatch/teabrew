package com.example.teashowcase.di

import androidx.lifecycle.ViewModel
import com.example.teashowcase.presentation.view.GameViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    fun bindGameViewModel(fragmentViewModelFactory: GameViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Multibinds
    fun planeViewModels(): Map<Class<out ViewModel>, ViewModel>
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
