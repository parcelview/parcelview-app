package dev.parcelview.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform