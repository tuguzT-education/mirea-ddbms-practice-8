package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.tuguzt.ddbms.practice8.viewmodel.ViewModel
import org.koin.mp.KoinPlatformTools

@Composable
inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> {
    val koinContext = KoinPlatformTools.defaultContext()
    val koin = koinContext.get()
    return remember(koinContext, koin) { koin.inject() }
}
