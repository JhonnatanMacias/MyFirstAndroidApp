package com.example.myfirstandroidapp.ui.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstandroidapp.model.Book

@Composable
fun BooksScreen(
    state: BooksUiState,
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onBookSelected: (Book) -> Unit,
    onBack: () -> Unit
) {
    val selectedBook = state.selectedBook
    if (selectedBook != null) {
        BookDetailScreen(
            book = selectedBook,
            description = state.description,
            isLoadingDescription = state.isLoadingDescription,
            modifier = modifier,
            onBack = onBack
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Libros", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChanged,
            label = { Text("Título o tema") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = onSearch, enabled = !state.isSearching) {
            Text("Buscar")
        }

        if (state.isSearching) {
            CircularProgressIndicator()
        }
        state.searchError?.let { Text(it) }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.books, key = { it.workId }) { book ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookSelected(book) }
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(book.title, style = MaterialTheme.typography.bodyLarge)
                    Text(book.author, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
