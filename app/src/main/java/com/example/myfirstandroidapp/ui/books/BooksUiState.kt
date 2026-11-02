package com.example.myfirstandroidapp.ui.books

import com.example.myfirstandroidapp.model.Book

data class BooksUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val description: String? = null,
    val isLoadingDescription: Boolean = false
)
