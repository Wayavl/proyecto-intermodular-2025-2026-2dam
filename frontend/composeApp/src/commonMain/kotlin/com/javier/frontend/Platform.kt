package com.javier.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform