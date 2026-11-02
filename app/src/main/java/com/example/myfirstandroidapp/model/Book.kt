package com.example.myfirstandroidapp.model

data class Book(
    val workId: String,
    val title: String,
    val author: String,
    val firstPublishYear: Int?,
    val coverId: Int?
)
