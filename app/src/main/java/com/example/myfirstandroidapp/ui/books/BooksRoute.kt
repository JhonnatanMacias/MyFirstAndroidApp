package com.example.myfirstandroidapp.ui.books

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun BooksRoute(
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel
) {
    val state by viewModel.uiState.collectAsState()

    BooksScreen(
        state = state,
        modifier = modifier,
        onQueryChanged = viewModel::onQueryChanged,
        onSearch = viewModel::searchBooks,
        onBookSelected = viewModel::selectBook,
        onBack = viewModel::clearSelectedBook
    )
}
