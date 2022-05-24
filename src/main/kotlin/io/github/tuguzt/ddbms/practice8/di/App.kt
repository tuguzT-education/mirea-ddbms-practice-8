package io.github.tuguzt.ddbms.practice8.di

import io.github.tuguzt.ddbms.practice8.viewmodel.AppViewModel
import io.github.tuguzt.ddbms.practice8.viewmodel.MainScreenViewModel
import org.koin.dsl.module

val appModule = module {
    single { AppViewModel(viewModelScope = get()) }

    single {
        val client = get<AppViewModel>().client.value
        MainScreenViewModel(viewModelScope = get(), client = requireNotNull(client))
    }
}
