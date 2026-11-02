package com.example.myfirstandroidapp.data

import com.example.myfirstandroidapp.model.Book

interface BookRepository {
    suspend fun searchBooks(query: String): List<Book>
    suspend fun getBookDescription(workId: String): String?
}
