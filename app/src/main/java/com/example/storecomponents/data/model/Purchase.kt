package com.example.storecomponents.data.model

import java.util.UUID

data class Purchase(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = ""
)
