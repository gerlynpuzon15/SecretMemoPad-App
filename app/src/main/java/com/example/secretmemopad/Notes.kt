package com.example.secretmemopad

data class Notes(
    val id: Int,
    val userId: Int,
    val title: String?,
    val note: String
)
