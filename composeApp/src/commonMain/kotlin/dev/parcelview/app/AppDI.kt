package dev.parcelview.app

import dev.parcelview.feature.parcels.impl.parcelsModule
import dev.parcelview.feature.scanner.impl.scannerModule
import dev.parcelview.feature.settings.impl.settingsModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(parcelsModule, scannerModule, settingsModule)
    }
}
