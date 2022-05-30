package io.github.tuguzt.ddbms.practice8.di

import io.github.tuguzt.ddbms.practice8.docker.identifier
import io.github.tuguzt.ddbms.practice8.viewmodel.*
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase

val appModule = module {
    single { AppViewModel(viewModelScope = get()) }

    single { get<AppViewModel>().client.value }

    single { get<CoroutineClient>().getDatabase(identifier) }

    single { MainScreenViewModel(viewModelScope = get(), database = get()) }

    single {
        val database = get<CoroutineDatabase>()
        HotelChainViewModel(viewModelScope = get(), collection = database.getCollection())
    }

    single {
        val database = get<CoroutineDatabase>()
        HotelViewModel(viewModelScope = get(), collection = database.getCollection())
    }

    single {
        val database = get<CoroutineDatabase>()
        RoomViewModel(viewModelScope = get(), collection = database.getCollection())
    }

    single {
        val database = get<CoroutineDatabase>()
        BookingViewModel(viewModelScope = get(), collection = database.getCollection())
    }

    single {
        val database = get<CoroutineDatabase>()
        ClientViewModel(viewModelScope = get(), collection = database.getCollection())
    }
}
