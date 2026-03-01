package dev.parcelview.feature.parcels.impl

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val parcelsModule = module {
    viewModel { ParcelsViewModel() }
}
