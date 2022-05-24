package io.github.tuguzt.ddbms.practice8.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val viewModelModule = module {
    // Coroutine scope factory for view models
    factory { CoroutineScope(Dispatchers.Default) }
}
