package dev.parcelview.feature.scanner.impl

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scannerModule = module {
    viewModel { ScannerViewModel() }
}
