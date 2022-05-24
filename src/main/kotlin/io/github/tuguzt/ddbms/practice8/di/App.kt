package io.github.tuguzt.ddbms.practice8.di

import io.github.tuguzt.ddbms.practice8.viewmodel.AppViewModel
import org.koin.dsl.module

val appModule = module {
    single { AppViewModel(viewModelScope = get()) }
}
