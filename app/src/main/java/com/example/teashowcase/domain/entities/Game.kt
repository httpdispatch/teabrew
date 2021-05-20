package com.example.teashowcase.domain.entities

import java.util.concurrent.TimeUnit

data class Game(
    val id: String,
    val maxDurationMillis: Long = TimeUnit.SECONDS.toMillis(100)
)
